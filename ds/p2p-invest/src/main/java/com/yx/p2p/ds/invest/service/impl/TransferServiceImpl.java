package com.yx.p2p.ds.invest.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.enums.invest.TransferBizStateEnum;
import com.yx.p2p.ds.enums.match.FinanceMatchReqLevelEnum;
import com.yx.p2p.ds.enums.match.FinanceMatchReqTypeEnum;
import com.yx.p2p.ds.enums.match.MatchRemarkEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.mapper.*;
import com.yx.p2p.ds.model.invest.*;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.service.InvestClaimHistoryService;
import com.yx.p2p.ds.service.InvestService;
import com.yx.p2p.ds.service.LendingService;
import com.yx.p2p.ds.service.TransferService;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.vo.TransferContractVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/09/15:27
 */
@Service
public class TransferServiceImpl implements TransferService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private InvestService investService;

    @Autowired
    private LendingService lendingService;

    @Autowired
    private InvestClaimMapper investClaimMapper;

    @Autowired
    private TransferMapper transferMapper;

    @Autowired
    private TransferDtlMapper transferDtlMapper;
    @Autowired
    private TransferDtlSaleBeforeMapper transferDtlSaleBeforeMapper;

    @Value("${mq.match.transfer.producer.group}")
    private String matchTransferProducerGroup;

    @Value("${mq.match.transfer.topic}")
    private String matchTransferTopic;

    @Value("${mq.match.transfer.tag}")
    private String matchTransferTag;

    @Autowired
    private InvestClaimHistoryService investClaimHistoryService;

    //幂等性接口：转让赎回申请
    //1.查询是否存在转让协议，若存在，则直接调用“发送转让撮合(Match系统.MQ)”
    //2.若转让协议不存在，查询投资债权关系
    //3.查询债权转让当日债权(Borrow系统.Redis)
    //4.事务操作：生成转让协议（转让和转让明细数据），并保持数据库
    //5.发送转让撮合(Match系统.MQ)
    @Override
    public Result transferApply(Integer investId) {
        logger.debug("【转让赎回申请】入参：investId=" + investId);
        Result result = Result.error();
        TransferContractVo resTransferContractVo = null;
        //1.查询是否存在转让协议，若存在，则接调用“发送转让撮合(Match系统.MQ)”
        result = this.checkNoTransfer(investId);
        if(Result.checkStatus(result)){
            //1.查询投资债权关系
            InvestClaim param = new InvestClaim();
            param.setInvestId(investId);
            List<InvestClaim> investClaimList = investService.getInvestClaimList(param);
            if(!investClaimList.isEmpty()){
                //2.查询债权转让当日债权(Borrow系统.Redis)
                //3.事务操作：生成转让协议（转让和转让明细数据），更新投资状态为转让中，并保持数据库
                resTransferContractVo = new TransferContractVo();
                result = this.genTransferContractVo(investId,investClaimList,resTransferContractVo);
            }
        }else{
            resTransferContractVo = this.getTransferContractVo(investId);
        }
        //4.发送转让撮合(Match系统.MQ)
        result = this.dealTransferMatch(resTransferContractVo);
        logger.debug("【转让赎回申请】结果：result=" + result);
        return result;
    }

    private Result checkNoTransfer(Integer investId) {
        Result result = Result.error();
        logger.debug("【检查不存在转让协议】入参：investId=" + investId);
        TransferContractVo transferContractVo = this.getTransferContractVo(investId);
        if(transferContractVo == null){
            result = Result.success();
        }
        logger.debug("【检查不存在转让协议】结果：result=" + result);
        return result;
    }

    //数据库查询转让协议和协议明细
    private TransferContractVo getTransferContractVo(Integer investId) {
        TransferContractVo vo = null;
        Transfer paramT = new Transfer();
        paramT.setInvestId(investId);
        Transfer transfer = transferMapper.selectOne(paramT);
        if(transfer != null){
            TransferDtlSaleBefore paramTDSB = new TransferDtlSaleBefore();
            paramTDSB.setTransferId(transfer.getId());
            List<TransferDtlSaleBefore> transferDtlSBList = transferDtlSaleBeforeMapper.select(paramTDSB);
            vo = new TransferContractVo();
            vo.setTransfer(transfer);
            vo.setTransferDtlSaleBeforeList(transferDtlSBList);
        }
        return vo;
    }

    private Result dealTransferMatch(TransferContractVo resTransferContractVo) {
        Result result = Result.error();
        List<Map<String,Object>> transferMatchMapList = this.buildTransferMatchMap(resTransferContractVo);
        result = this.sendTransferMatchMQ(resTransferContractVo.getTransfer().getId(),transferMatchMapList);
        return result;
    }

    private Result sendTransferMatchMQ(Integer transferId, List<Map<String,Object>>  transferMatchMapList) {
        Result result = Result.error();
        try {
            Message message = new Message(matchTransferTopic,matchTransferTag,
                    "transferId"+transferId,
                    JSON.toJSONString(transferMatchMapList).getBytes());
            DefaultMQProducer producer = rocketMQTemplate.getProducer();
            producer.setProducerGroup(matchTransferProducerGroup);
            SendResult sendResult = producer.send(message);
            logger.debug("【发送转让撮合MQ】，producer=" + producer.getProducerGroup() +
                    "，topic="+ matchTransferTopic + ",tag=" + matchTransferTag + ",message=" + message);
            result = Result.success();
            logger.debug("【发送转让撮合结果MQ】，sendResult" + sendResult);
        } catch (Exception e) {
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    //构建转让撮合数据
    private  List<Map<String,Object>> buildTransferMatchMap(TransferContractVo resTransferContractVo) {
        logger.debug("【构建转让撮合MQListener参数】入参：resTransferContractVo=" + resTransferContractVo );
        Result result = Result.error();
        Transfer transfer = resTransferContractVo.getTransfer();
        String transferId = String.valueOf(transfer.getId());
        List<TransferDtlSaleBefore> transferDtlSBList = resTransferContractVo.getTransferDtlSaleBeforeList();
        //开始构建
        List<Map<String,Object>> transferMatchReqMapList = new ArrayList<>();
        Map<String,Object> transferMatchReqMap = new HashMap<>();
        transferMatchReqMap.put("transferId",String.valueOf(resTransferContractVo.getTransfer().getId()));
        transferMatchReqMap.put("transferAmt",String.valueOf(resTransferContractVo.getTransfer().getTransferAmt()));
        List<Map<String,String>> transferMatchReqList = new ArrayList<>();
        for (TransferDtlSaleBefore transferDtlSB : transferDtlSBList) {
            Map<String,String> transferMatchMap = new HashMap<>();
            transferMatchMap.put("financeCustomerId",String.valueOf(transferDtlSB.getCustomerId()));
            transferMatchMap.put("financeCustomerName",transferDtlSB.getCustomerName());
            transferMatchMap.put("financeBizId",String.valueOf(transferId));
            transferMatchMap.put("financeOrderSn",String.valueOf(transferDtlSB.getInvestClaimId()));//p2p_invest_claim.id
            transferMatchMap.put("financeAmt",String.valueOf(transferDtlSB.getClaimAmt()));
            transferMatchMap.put("waitAmt",String.valueOf(transferDtlSB.getClaimAmt()));//债权当日价值
            transferMatchMap.put("level",String.valueOf(FinanceMatchReqLevelEnum.TRANSFER.getLevel()));
            transferMatchMap.put("borrowProductId",String.valueOf(transferDtlSB.getBorrowProductId()));
            transferMatchMap.put("borrowProductName",transferDtlSB.getBorrowProductName());
            transferMatchMap.put("borrowYearRate",String.valueOf(transferDtlSB.getBorrowYearRate()));
            transferMatchMap.put("type",String.valueOf(FinanceMatchReqTypeEnum.TRANSFER.getType()));
            transferMatchMap.put("remark",MatchRemarkEnum.TRANSFER.getDesc());
            transferMatchReqList.add(transferMatchMap);
        }
        transferMatchReqMap.put("transferList",transferMatchReqList);
        transferMatchReqMapList.add(transferMatchReqMap);
        logger.debug("【构建转让撮合MQListener参数】结果：transferMatchReqMapList=" + transferMatchReqMapList );
        return transferMatchReqMapList;
    }

    //事务操作：生成转让协议（转让和转让明细数据），更新投资状态为转让中，并保持数据库
    @Transactional(rollbackFor = Exception.class)
    public Result genTransferContractVo(Integer investId,
           List<InvestClaim> investClaimList,TransferContractVo resTransferContractVo) {
        Result result = Result.error();
        //更新投资状态为转让中
        investService.updateInvestBizState(investId, InvestBizStateEnum.TRANSFERING);
        //生成转让协议（转让和转让明细数据）
        TransferContractVo transferContractVo =
                this.buildTransferContractVo(investId,investClaimList,resTransferContractVo);
        this.addTransferContractVo(transferContractVo);
        result = Result.success();
        return result;
    }

    //保持数据库:转让协议（转让和转让明细数据）
    public void addTransferContractVo(TransferContractVo transferContractVo) {
        logger.debug("【数据库操作：插入协议和批量插入协议明细】入参：" +
                "transferContractVo=" +transferContractVo);
        Transfer transfer = transferContractVo.getTransfer();
        transferMapper.insert(transfer);
        List<TransferDtlSaleBefore> transferDtlSBList = transferContractVo.getTransferDtlSaleBeforeList();
        for(TransferDtlSaleBefore transferDtlSB : transferDtlSBList){
            transferDtlSB.setTransferId(transfer.getId());
        }
        transferDtlSaleBeforeMapper.insertList(transferDtlSBList);
    }

    //构建转让合同
    private TransferContractVo buildTransferContractVo(Integer investId,
                    List<InvestClaim> investClaimList,TransferContractVo resTransferContractVo) {
        Transfer transfer = this.buildTransfer(investId);
        List<TransferDtlSaleBefore> transferDtlSBList = this.buildTransferDtlSaleBeforeList(investClaimList);
        resTransferContractVo.setTransfer(transfer);
        resTransferContractVo.setTransferDtlSaleBeforeList(transferDtlSBList);
        return resTransferContractVo;
    }

    //构建转让协议
    private Transfer buildTransfer(Integer investId) {
        Transfer transfer = new Transfer();
        Invest invest = investService.getInvestByInvestId(investId);
        try {
            BeanUtils.copyProperties(transfer,invest);
            transfer.setId(null);
            transfer.setInvestId(investId);
            transfer.setTransferAmt(invest.getInvestAmt());//需要修改
            transfer.setCashAmt(BigDecimal.ZERO);//需要修改
            transfer.setClaimAmt(invest.getInvestAmt());//需要修改
            transfer.setDiscountFee(BigDecimal.ZERO);//需要修改
            transfer.setExpressFee(BigDecimal.ZERO);//需要修改
            transfer.setServiceFee(BigDecimal.ZERO);//需要修改
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        BeanHelper.setAddDefaultField(transfer);
        return transfer;
    }

    //构建转让明细
    private List<TransferDtlSaleBefore> buildTransferDtlSaleBeforeList(List<InvestClaim> investClaimList) {
        List<TransferDtlSaleBefore> transferDtlSBList = new ArrayList<>();
        for(InvestClaim investClaim : investClaimList){
            TransferDtlSaleBefore transferDtlSB = new TransferDtlSaleBefore();
            transferDtlSB.setInvestClaimId(investClaim.getId());
            try {
                BeanUtils.copyProperties(transferDtlSB,investClaim);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            BeanHelper.setAddDefaultField(transferDtlSB);
            transferDtlSBList.add(transferDtlSB);
        }
        return transferDtlSBList;
    }

      //投资债权交割
    public Result changeInvestclaim(Map<String, Object> paramClaimMap){
        Result result = Result.error();
        Integer transferId = Integer.valueOf((String)paramClaimMap.get("transferId"));
        //构建业务数据
        List<Integer> resOldClaimIdList = new ArrayList<>();//旧投资债权明细Id，用于批量插入明细历史和删除明细
        List<InvestClaim> resNewClaimList = new ArrayList<>();//新投资债权明细，用于批量插入数据库
        HashMap<Integer,List<InvestClaim>> claimMap = new HashMap<>();
        for (Map<String, String> inClaimMap : (List<Map<String, String>>)paramClaimMap.get("transferList")) {
            this.genNewInvestClaimList(inClaimMap,claimMap);
        }
        //处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例
        result = this.dealHoldShare(claimMap,resOldClaimIdList,resNewClaimList);
        logger.debug("债权交割数据：【resOldClaimIdList=】" + resOldClaimIdList);
        logger.debug("债权交割数据：【resNewClaimList=】" + resNewClaimList);
        //检查并处理出借单满额
        result = lendingService.checkAndDealFullAmt(resNewClaimList);
        //批量插入新投资持有债权明细
        investClaimMapper.insertList(resNewClaimList);
        //批量插入新投资持有债权明细历史
        investClaimHistoryService.genInvestClaimHistoryList(resNewClaimList);
        //删除旧投资持有债权明细
        investClaimMapper.deleteBatchInvestClaim(resOldClaimIdList);
        //变更协议状态为已赎回
        this.updateTransferBizState(transferId, TransferBizStateEnum.TRANSFER_SUC);
        //变更投资状态为已赎回
        investService.updateBizStateByTransferId(transferId,InvestBizStateEnum.TRANSFER_SUC);
        return result;
    }

    private Result updateTransferBizState(Integer transferId, TransferBizStateEnum transferBizStateEnum) {
        Result result = Result.error();
        Transfer transfer = new Transfer();
        transfer.setId(transferId);
        transfer.setBizState(transferBizStateEnum.getState());
        transferMapper.updateByPrimaryKeySelective(transfer);
        result = Result.success();
        return result;
    }

    private void genNewInvestClaimList(Map<String, String> paramClaimMap,
                                       HashMap<Integer, List<InvestClaim>> claimMap) {
        Integer investClaimId = Integer.valueOf((String)paramClaimMap.get("investClaimId"));
        //插入新投资持有债权明细
        InvestClaim newClaim = new InvestClaim();
        newClaim.setCustomerId(Integer.valueOf((String)paramClaimMap.get("customerId")));
        newClaim.setCustomerName((String)paramClaimMap.get("customerName"));
        newClaim.setInvestId(Integer.valueOf((String)paramClaimMap.get("investId")));
        newClaim.setLendingId(Integer.valueOf((String)paramClaimMap.get("lendingId")));
        newClaim.setBorrowId(Integer.valueOf((String)paramClaimMap.get("borrowId")));
        newClaim.setBuyAmt(new BigDecimal((String)paramClaimMap.get("tradeAmt")));
        newClaim.setClaimAmt(newClaim.getBuyAmt());
        newClaim.setBorrowYearRate(new BigDecimal((String)paramClaimMap.get("borrowYearRate")));
        newClaim.setBorrowProductName((String)paramClaimMap.get("borrowProductName"));
        newClaim.setBorrowProductId(Integer.valueOf((String)paramClaimMap.get("borrowProductId")));
        newClaim.setHoldShare(new BigDecimal((String)paramClaimMap.get("matchShare")));//临时设置一会需要根据该值修改
        newClaim.setParentId(investClaimId);

        List<InvestClaim> investClaimList = claimMap.get(investClaimId);
        if(investClaimList == null){
            investClaimList = new ArrayList<>();
            claimMap.put(investClaimId,investClaimList);
        }
        investClaimList.add(newClaim);
    }

    //处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例
    //resOldClaimIdList 旧投资债权明细Id，用于批量插入明细历史和删除明细
    // resNewClaim 新投资债权明细，用于批量插入数据库
    private Result dealHoldShare(Map<Integer, List<InvestClaim>> claimMap,
                                 List<Integer> resOldClaimIdList,List<InvestClaim> resNewClaimList) {
        Result result = Result.error();
        //处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例
        logger.debug("处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例");
        for (Integer claimId : claimMap.keySet()) {
            resOldClaimIdList.add(claimId);//
            if(claimMap.get(claimId).size() == 1){//只有1条数据，说明撮合比例为1，不需要特殊处理
                logger.debug("【转让撮合债权只有1条数据，说明撮合比例为1】");
                InvestClaim newClaim = claimMap.get(claimId).get(0);
                InvestClaim oldClaim = investClaimMapper.selectByPrimaryKey(claimId);
                //新持有比例=原比例*撮合比例，因为撮合比例=1.
                newClaim.setHoldShare(oldClaim.getHoldShare());
                BeanHelper.setAddDefaultField(newClaim);
                resNewClaimList.add(newClaim);
            }else{//撮合多余1条
                logger.debug("【转让撮合债权大于1条数据，说明撮合比例为1】");
                List<InvestClaim> newClaimList = claimMap.get(claimId);
                InvestClaim oldClaim = investClaimMapper.selectByPrimaryKey(claimId);
                BigDecimal remainHoldShare = oldClaim.getHoldShare();
                int newClaimCount = newClaimList.size();
                for(int i=0; i< newClaimCount; i++){
                    InvestClaim newClaim = newClaimList.get(i);
                    if( i == newClaimCount-1){
                        logger.debug("最后1条【新持有比例=剩余比例】");
                        newClaim.setHoldShare(remainHoldShare);
                    }else{
                        logger.debug("【新持有比例=原比例*撮合比例】");
                        //新持有比例=原比例*撮合比例
                        BigDecimal newHoldShare = oldClaim.getHoldShare().multiply(newClaim.getHoldShare());
                        newClaim.setHoldShare(newHoldShare);
                        remainHoldShare = remainHoldShare.subtract(newHoldShare);
                    }
                    BeanHelper.setAddDefaultField(newClaim);
                    resNewClaimList.add(newClaim);
                }
            }
        }
        result = Result.success();
        return result;
    }

}
