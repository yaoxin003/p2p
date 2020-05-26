package com.yx.p2p.ds.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.SystemSourceEnum;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.enums.invest.TransferBizStateEnum;
import com.yx.p2p.ds.enums.match.FinanceMatchReqLevelEnum;
import com.yx.p2p.ds.enums.match.FinanceMatchReqTypeEnum;
import com.yx.p2p.ds.enums.match.MatchRemarkEnum;
import com.yx.p2p.ds.enums.payment.PaymentTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.mapper.*;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.invest.*;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.payment.Payment;
import com.yx.p2p.ds.server.BorrowServer;
import com.yx.p2p.ds.service.InvestClaimHistoryService;
import com.yx.p2p.ds.service.InvestService;
import com.yx.p2p.ds.service.LendingService;
import com.yx.p2p.ds.service.TransferService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.util.OrderUtil;
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
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

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

    @Reference
    private BorrowServer borrowServer;

    @Autowired
    private InvestService investService;

    @Autowired
    private LendingService lendingService;

    @Autowired
    private InvestClaimHistoryService investClaimHistoryService;

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
        Transfer transfer =  this.queryTransferByInvestId(investId);
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

    private Transfer getTransfer(Integer transferId){
        Transfer paramT = new Transfer();
        paramT.setId(transferId);
        Transfer transfer = transferMapper.selectOne(paramT);
        return transfer;
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
        //转让人投资编号
        transferMatchReqMap.put("financeExtBizId",String.valueOf(resTransferContractVo.getTransfer().getInvestId()));
        List<Map<String,String>> transferMatchReqList = new ArrayList<>();
        for (TransferDtlSaleBefore transferDtlSB : transferDtlSBList) {
            Map<String,String> transferMatchMap = new HashMap<>();
            transferMatchMap.put("financeCustomerId",String.valueOf(transfer.getCustomerId()));
            transferMatchMap.put("financeCustomerName",transfer.getCustomerName());
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
            transfer.setRedeemAmt(BigDecimal.ZERO);
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

    /**
        * @description: 投资债权交割
        *  幂等性验证：检查没有投资债权交割
        *  检查并处理出借单满额
        * 事务操作：
         *  批量插入协议明细
         *  满额处理：若满额会更新出借和投资
         *  批量插入新投资持有债权明细
         *  批量插入新投资持有债权明细历史
         *  删除旧投资持有债权明细
         *  变更协议状态为转让成功
         *  变更投资状态为转让成功
        * @author:  YX
        * @date:    2020/05/15 17:29
        * @param: paramClaimMap
        * @return: com.yx.p2p.ds.easyui.Result
        */
    @Transactional(rollbackFor=Exception.class)
    public Result changeInvestclaim(Map<String, Object> paramClaimMap){
        logger.debug("【投资债权交割】入参paramClaimMap=" + paramClaimMap);
        Result result = Result.error();
        Integer transferId = Integer.valueOf((String)paramClaimMap.get("transferId"));
        Integer investId = Integer.valueOf((String)paramClaimMap.get("financeExtBizId"));
        List<Map<String, String>> paramTransferMapList = (List<Map<String, String>>)paramClaimMap.get("transferList");
        //幂等性验证：检查没有投资债权交割
        result = this.checkNoChangeInvestClaim(transferId);
        if(Result.checkStatus(result)){
            //构建业务数据
            List<InvestClaim> resNewClaimList = new ArrayList<>();//新投资债权明细，用于批量插入数据库
            List<TransferDtl> resTransferDtlList = new ArrayList<>();
            Map<Integer, InvestClaim> dbClaimMap = new HashMap<>();//数据库查询结果
            result = this.buildNewClaimAndTransferDtlList(transferId,paramTransferMapList,dbClaimMap,
                    resNewClaimList,resTransferDtlList);//新
            //处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例
            logger.debug("处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例【dbClaimMap=】" + dbClaimMap);
            result = this.dealInvestClaimHoldShare(dbClaimMap,resNewClaimList);
            //数据库事务操作：投资债权交割
            this.dealDBChangeInvestclaim(resTransferDtlList,resNewClaimList,paramClaimMap);
        }
        return result;
    }

    /**
     * @description:  数据库事务操作：投资债权交割
     * 批量插入协议明细
     * 满额处理：若满额会更新出借和投资
     *  批量插入新投资持有债权明细
     *  批量插入新投资持有债权明细历史
     *  删除旧投资持有债权明细
     *  变更协议状态为转让成功
     *  变更投资状态为转让成功
        * @author:  YX
        * @date:    2020/05/25 7:36
        * @param: resTransferDtlList
    * @param: resNewClaimList
    * @param: paramClaimMap
        * @return: com.yx.p2p.ds.easyui.Result
        * @throws:
        */
    private Result dealDBChangeInvestclaim(List<TransferDtl> resTransferDtlList, List<InvestClaim> resNewClaimList,
                                           Map<String, Object> paramClaimMap) {
        logger.debug("【投资债权交割数据库处理】入参resTransferDtlList=" + resTransferDtlList);
        Integer transferId = Integer.valueOf((String)paramClaimMap.get("transferId"));
        Integer investId = Integer.valueOf((String)paramClaimMap.get("financeExtBizId"));
        List<Map<String, String>> paramTransferMapList = (List<Map<String, String>>)paramClaimMap.get("transferList");
        Result result = Result.error();
        //批量插入协议明细
        transferDtlMapper.insertList(resTransferDtlList);
        //检查并处理出借单满额
        result = lendingService.checkAndDealFullAmt(resNewClaimList);
        //批量插入新投资持有债权明细
        investClaimMapper.insertList(resNewClaimList);
        //批量插入新投资持有债权明细历史
        investClaimHistoryService.genInvestClaimHistoryList(resNewClaimList);
        //删除旧投资持有债权明细
        this.deleteBatchInvestClaim(paramTransferMapList);
        //变更协议状态为转让成功
        this.updateTransferBizState(transferId, TransferBizStateEnum.TRANSFER_SUC);
        //变更投资状态为转让成功
        investService.updateInvestBizState(investId,InvestBizStateEnum.TRANSFER_SUC);
        result = Result.success();
        logger.debug("【投资债权交割数据库处理】完毕");
        return result;
    }

    public Result deleteBatchInvestClaim(List<Map<String, String>> paramTransferMapList){
        Result result = Result.error();
        List<Integer> resOldClaimIdList = new ArrayList<>();//旧投资债权明细Id，用于批量插入明细历史和删除明细
        for(Map<String, String> map:paramTransferMapList){
            Integer claimId = Integer.valueOf((String)map.get("financeOrderSn"));
            resOldClaimIdList.add(claimId);
        }
        //删除旧投资持有债权明细
        investClaimMapper.deleteBatchInvestClaim(resOldClaimIdList);
        result = Result.success();
        return result;
    }

    private Result buildNewClaimAndTransferDtlList(Integer transferId,List<Map<String, String>> paramTransferMapList,
            Map<Integer,InvestClaim> dbClaimMap,List<InvestClaim> resNewClaimList, List<TransferDtl> resTransferDtlList) {
        logger.debug("【构建投资债权明细和转让协议明细】入参：transferId=" + transferId + ",paramTransferMapList="
                + paramTransferMapList + ",dbClaimMap=" + dbClaimMap + ",resNewClaimList=" + resNewClaimList
                + ",paramTransferMapList=" + paramTransferMapList);
        Result result = Result.error();
        List<InvestClaim> dbClaimList = this.getClaimList(paramTransferMapList);
        Map<Integer, InvestClaim> tmpInvestClaimMap = this.buildClaimMapByQuery(dbClaimList);
        BeanHelper.copyMap(dbClaimMap,tmpInvestClaimMap);
        Set<Integer> borrowIdSet = this.getBorrowIdSet(dbClaimList);
        Map<Integer, Borrow> dbBorrowMap = this.buildBorrowMapByQuery(dbClaimList);
        for (Map<String, String> paramTransferDtlMap : paramTransferMapList) {
            Integer claimId = Integer.valueOf((String)paramTransferDtlMap.get("financeOrderSn"));
            InvestClaim dbClaim = dbClaimMap.get(claimId);
            Integer borrowId = dbClaim.getBorrowId();
            Borrow borrow = dbBorrowMap.get(borrowId);
            //新投资持有债权明细
            InvestClaim newClaim = new InvestClaim();
            newClaim.setParentId(claimId);
            newClaim.setInvestId(Integer.valueOf((String)paramTransferDtlMap.get("investBizId")));
            newClaim.setLendingId(Integer.valueOf((String)paramTransferDtlMap.get("investOrderSn")));
            newClaim.setBuyAmt(new BigDecimal((String)paramTransferDtlMap.get("tradeAmt")));
            newClaim.setClaimAmt(newClaim.getBuyAmt());
            newClaim.setHoldShare(new BigDecimal((String)paramTransferDtlMap.get("matchShare")));//临时设置一会需要根据该值修改
            newClaim.setBorrowProductId(Integer.valueOf((String)paramTransferDtlMap.get("borrowProductId")));
            newClaim.setBorrowProductName((String)paramTransferDtlMap.get("borrowProductName"));
            newClaim.setBorrowYearRate(new BigDecimal((String)paramTransferDtlMap.get("borrowYearRate")));
            newClaim.setBorrowId(dbClaim.getBorrowId());
            newClaim.setCustomerId(dbClaim.getCustomerId());
            newClaim.setCustomerName(dbClaim.getCustomerName());
            BeanHelper.setAddDefaultField(newClaim);
            resNewClaimList.add(newClaim);
            //转让协议明细
            TransferDtl transferDtl = new TransferDtl();
            transferDtl.setTransferId(transferId);
            transferDtl.setInvestClaimId(claimId);
            transferDtl.setBorrowCustomerId(borrow.getCustomerId());
            transferDtl.setBorrowCustomerName(borrow.getCustomerName());
            transferDtl.setBorrowId(borrow.getId());
            transferDtl.setBorrowProductId(Integer.valueOf((String)paramTransferDtlMap.get("borrowProductId")));
            transferDtl.setBorrowProductName((String)paramTransferDtlMap.get("borrowProductName"));
            transferDtl.setBorrowYearRate(new BigDecimal((String)paramTransferDtlMap.get("borrowYearRate")));
            //----------------撮合信息----------------
            transferDtl.setInvestCustomerId(Integer.valueOf((String)paramTransferDtlMap.get("investCustomerId")));
            transferDtl.setInvestCustomerName((String)paramTransferDtlMap.get("investCustomerName"));
            transferDtl.setBuyAmt(new BigDecimal((String)paramTransferDtlMap.get("tradeAmt")));
            transferDtl.setHoldShare(new BigDecimal((String)paramTransferDtlMap.get("matchShare")));//临时设置一会需要根据该值修改
            //----------------投资信息信息----------------
            transferDtl.setInvestId(Integer.valueOf((String)paramTransferDtlMap.get("investBizId")));
            transferDtl.setLendingId(Integer.valueOf((String)paramTransferDtlMap.get("investOrderSn")));
            //----------------借款信息----------------
            transferDtl.setBorrowCustomerIdCard(borrow.getCustomerIdCard());
            transferDtl.setBorrowAmt(borrow.getBorrowAmt());
            transferDtl.setBorrowMonthCount(borrow.getBorrowMonthCount());
            transferDtl.setBorrowTotalBorrowFee(borrow.getTotalBorrowFee());
            transferDtl.setBorrowTotalInterest(borrow.getTotalInterest());
            transferDtl.setBorrowTotalManageFee(borrow.getTotalManageFee());
            transferDtl.setBorrowStartDate(borrow.getStartDate());
            transferDtl.setBorrowEndDate(borrow.getEndDate());
            transferDtl.setBorrowFirstReturnDate(borrow.getFirstReturnDate());
            transferDtl.setBorrowMonthReturnDay(borrow.getMonthReturnDay());
            transferDtl.setBorrowMonthPayment(borrow.getMonthPayment());
            BeanHelper.setAddDefaultField(transferDtl);
            resTransferDtlList.add(transferDtl);
        }
        logger.debug("【构建投资债权明细和转让协议明细】结果：resNewClaimList=" + resNewClaimList
                + ",resTransferDtlList=" + resTransferDtlList + ",dbClaimMap=" + dbClaimMap);
        result = Result.success();
        return result;
    }


    //幂等性验证：检查没有投资债权交割。如果协议状态为新增则继续操作
    private Result checkNoChangeInvestClaim(Integer transferId) {
        logger.debug("【检查没有投资债权交割】入参transferId=" + transferId);
        Result result = Result.error();
        Transfer transfer = this.getTransfer(transferId);
        if(transfer != null && TransferBizStateEnum.NEW_ADD.getState().equals(transfer.getBizState())){
            result = Result.success();
        }
        logger.debug("【检查没有投资债权交割】结果result=" + result);
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

    //处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例
    //resOldClaimIdList 旧投资债权明细Id，用于批量插入明细历史和删除明细
    // resNewClaim 新投资债权明细，用于批量插入数据库
    private Result dealInvestClaimHoldShare(Map<Integer, InvestClaim> dbClaimMap,List<InvestClaim> resNewClaimList) {
        logger.debug("【构建投资债权明细中的新持有比例】入参：dbClaimMap=" + dbClaimMap
                + ",resNewClaimList=" + resNewClaimList);
        Result result = Result.error();
        //resClaimMap目的：计算债权持有比例方便(汇总投资债权编号对应的List<InvestClaim>)
        HashMap<Integer,List<InvestClaim>> resClaimMap = this.buildClaimListMap(resNewClaimList);
        //处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例
        logger.debug("处理新投资债权明细的持有债权比例：新持有比例=原比例*撮合比例");
        for (Integer claimId : resClaimMap.keySet()) {
            InvestClaim dbClaim = dbClaimMap.get(claimId);
            if(resClaimMap.get(claimId).size() == 1){//只有1条数据，说明撮合比例为1，不需要特殊处理
                logger.debug("【转让撮合债权只有1条数据，说明撮合比例为1】");
                InvestClaim newClaim = resClaimMap.get(claimId).get(0);
                //新持有比例=原比例*撮合比例，因为撮合比例=1.
                newClaim.setHoldShare(dbClaim.getHoldShare());
            }else{//撮合多余1条
                logger.debug("【转让撮合债权大于1条数据，说明撮合比例为1】");
                List<InvestClaim> newClaimList = resClaimMap.get(claimId);
                BigDecimal remainHoldShare = dbClaim.getHoldShare();
                int newClaimCount = newClaimList.size();
                for(int i=0; i< newClaimCount; i++){
                    InvestClaim newClaim = newClaimList.get(i);
                    BigDecimal newHoldShare = this.buildNewHoldShare(i,newClaimCount,
                            dbClaim.getHoldShare(),newClaim.getHoldShare(),remainHoldShare);
                    newClaim.setHoldShare(newHoldShare);
                    remainHoldShare = remainHoldShare.subtract(newHoldShare);
                }
            }
        }
        result = Result.success();
        logger.debug("【构建投资债权明细中的新持有比例】结果：resNewClaimList=" + resNewClaimList);
        return result;
    }

    //汇总投资债权编号对应的List<InvestClaim>
    private HashMap<Integer,List<InvestClaim>> buildClaimListMap(List<InvestClaim> resNewClaimList) {
        HashMap<Integer,List<InvestClaim>> resClaimMap = new HashMap<>();
        for (InvestClaim investClaim : resNewClaimList) {
            Integer investClaimId = investClaim.getParentId();//汇总投资债权编号对应的List<InvestClaim>
            List<InvestClaim> investClaimList = resClaimMap.get(investClaimId);
            if(investClaimList == null){
                investClaimList = new ArrayList<>();
                resClaimMap.put(investClaimId,investClaimList);
            }
            investClaimList.add(investClaim);
        }
        return resClaimMap;
    }

    private List<InvestClaim> getClaimList(List<Map<String, String>> paramTransferMapList) {
        Set<Integer> queryClaimIdSet = this.buildClaimIdSet(paramTransferMapList);
        List<InvestClaim> investClaimList = this.queryInvestClaimListByClaimIdSet(queryClaimIdSet);
        return investClaimList;
    }

    private Set<Integer> buildClaimIdSet(List<Map<String, String>> paramTransferMapList) {
        Set<Integer> queryClaimIdSet = new HashSet<>();//查询条件，防止多次查询
        for (Map<String, String> paramTransferDtlMap : paramTransferMapList) {
            Integer claimId = Integer.valueOf((String) paramTransferDtlMap.get("financeOrderSn"));
            queryClaimIdSet.add(claimId);
        }
        return queryClaimIdSet;
    }

    private Map<Integer,InvestClaim> buildClaimMapByQuery(List<InvestClaim> investClaimList) {
        Map<Integer,InvestClaim> dbInvestClaimMap = new HashMap<>();
        for (InvestClaim investClaim : investClaimList) {
            dbInvestClaimMap.put(investClaim.getId(),investClaim);
        }
        logger.debug("【投资债权明细Map】dbInvestClaimMap=" + dbInvestClaimMap);
        return dbInvestClaimMap;
    }

    private Map<Integer,Borrow> buildBorrowMapByQuery(List<InvestClaim> dbClaimList) {
        logger.debug("【查询借款】入参dbClaimList=" + dbClaimList);
        Set<Integer> borrowIdSet = this.getBorrowIdSet(dbClaimList);
        List<Borrow> borrowList = borrowServer.getBorrowListByBorrowIdList(borrowIdSet);//查询Borrow结果
        logger.debug("【查询借款】结果borrowList=" + borrowList);
        Map<Integer,Borrow> borrowMap = new HashMap<>();
        for (Borrow borrow : borrowList) {
            borrowMap.put(borrow.getId(),borrow);
        }
        logger.debug("【借款Map】borrowMap=" + borrowMap);
        return borrowMap;
    }

    private Set<Integer> getBorrowIdSet(List<InvestClaim> investClaimList){
        Set<Integer> borrowIdSet = new HashSet<>();
        for (InvestClaim investClaim : investClaimList) {
            borrowIdSet.add(investClaim.getBorrowId());
        }
        return borrowIdSet;
    }

    private List<InvestClaim> queryInvestClaimListByClaimIdSet(Set<Integer> paramClaimIdSet) {
        logger.debug("【查询投资债权明细】入参：paramClaimIdSet=" + paramClaimIdSet);
        Example example = new Example(InvestClaim.class);
        example.createCriteria().andIn("id", paramClaimIdSet);
        List<InvestClaim> investClaimList = investClaimMapper.selectByExample(example);
        logger.debug("【查询投资债权明细】结果：investClaimList=" + investClaimList);
        return investClaimList;
    }

    /**
        * @description: 投资持有债权比例
        * @author:  YX
        * @date:    2020/05/24 12:03
        * @param: i 循环次数编号
        * @param: newClaimCount 总次数
        * @param: oldHoldShare 旧投资持有债权比例
        * @param: matchShare 撮合比例
        * @param: remainHoldShare 剩余投资持有债权比例
        * @return: java.math.BigDecimal  新投资持有债权比例
        */
    private BigDecimal buildNewHoldShare(int i, int newClaimCount, BigDecimal oldHoldShare,
                                         BigDecimal matchShare, BigDecimal remainHoldShare) {
        BigDecimal newMatchShare = BigDecimal.ZERO;
        if( i == newClaimCount-1){
            logger.debug("最后1条【新持有比例=剩余比例】");
            newMatchShare = remainHoldShare;
        }else{
            logger.debug("【新持有比例=原比例*撮合比例】");
            //新持有比例=原比例*撮合比例
            BigDecimal newHoldShare = oldHoldShare.multiply(matchShare);
            newMatchShare = BigDecimalUtil.round2In45(newHoldShare);
        }
        return newMatchShare;
    }

    //投资提现申请
    @Override
    public Result withdrawApply(Integer investId){
        Result result = Result.error();
        logger.debug("【投资提现申请】入参：investId=" + investId);
        Payment payment = this.buildWithdrawPayment(investId);
        logger.debug("【投资提现申请】结果：");
        result = Result.success();
        return result;
    }

    private Payment buildWithdrawPayment(Integer investId) {
        Transfer transfer =  this.queryTransferByInvestId(investId);
        Payment payment = new Payment();
        try{
            String sysSourceName = SystemSourceEnum.INVEST.getName();
            payment.setSystemSource(sysSourceName);
            payment.setBizId(String.valueOf(transfer.getId()));
            payment.setOrderSn(OrderUtil.genOrderSn(sysSourceName));
            payment.setAmount(transfer.getRedeemAmt());
            payment.setType(PaymentTypeEnum.TRANSFER_WITHDRAW.getCode());
            payment.setRemark(PaymentTypeEnum.TRANSFER_WITHDRAW.getCodeDesc());
            BeanUtils.copyProperties(payment,transfer);
            payment.setIdCard(transfer.getCustomerIdCard());
            BeanHelper.setAddDefaultField(payment);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        logger.debug("【构建添加投资提现支付对象】payment=" + payment);
        return payment;
    }

    private Transfer queryTransferByInvestId(Integer investId) {
        Transfer param = new Transfer();
        param.setInvestId(investId);
        Transfer transfer = transferMapper.selectOne(param);
        return transfer;
    }

    //获得转让协议文本
    public Map<String,Object> getTransferContractText(Integer investId){
        logger.debug("【查询转让协议文本】入参：investId=" + investId);
        Transfer param = new Transfer();
        param.setInvestId(investId);
        List<Transfer> transferList = transferMapper.selectTransferContract(param);
        logger.debug("【查询转让协议文本】结果：transferList=" + transferList);
        Map<String,Object> map = this.buildTransferContractText(transferList);
        return map;
    }

    private Map<String,Object> buildTransferContractText(List<Transfer> transferList) {
        //转让协议
        Map<String,Object> contractMap = new HashMap<>();
        //转让人
        Map<String,String> financerMap = new HashMap<>();//对象
        List<Map<String,String>> financerMapList = new ArrayList<>();//对象集合
        //受让人
        Map<Integer,Map<String,String>> investorMapMap = new HashMap<> ();
        List<Map<String,String>> investorMapList = new ArrayList<>();
        //债权列表
        Set<Integer> borrowIdSet = new HashSet<> ();
        List<Map<String,String>> borrowMapList = new ArrayList<>();

        if (!transferList.isEmpty() && transferList.size()==1) {
            Transfer transfer = transferList.get(0);
            //转让人
            financerMap.put("redeemAmt",transfer.getRedeemAmt().toString());
            financerMap.put("investProductName",transfer.getInvestProductName());
            financerMap.put("investYearIrr",transfer.getInvestYearIrr().toString());
            financerMap.put("customerName",transfer.getCustomerName());
            financerMap.put("customerIdCard",transfer.getCustomerIdCard());
            financerMap.put("baseBankName",transfer.getBaseBankName());
            financerMap.put("bankAccount",transfer.getBankAccount());
            financerMap.put("phone",transfer.getPhone());
            financerMap.put("investAmt",transfer.getInvestAmt().toString());
            financerMap.put("startDate", DateUtil.dateYMD2Str(transfer.getStartDate()));
            financerMapList.add(financerMap);

            BigDecimal totalBuyAmt = BigDecimal.ZERO;
            BigDecimal totalHoldShare = BigDecimal.ZERO;
            for (TransferDtl transferDtl : transfer.getTransferDtlList()) {
                //受让人
                Integer investId = transferDtl.getInvestId();
                BigDecimal buyAmt = transferDtl.getBuyAmt();
                BigDecimal holdShare = transferDtl.getHoldShare();
                HashMap<String,String> investorMap = null;
                if(!investorMapMap.containsKey(investId) ){
                    investorMap = new HashMap<>();
                    investorMap.put("investId",investId.toString());
                    investorMap.put("investCustomerId",transferDtl.getInvestCustomerId().toString());
                    investorMap.put("investCustomerName",transferDtl.getInvestCustomerName());
                }else{
                    investorMap = (HashMap<String,String>)investorMapMap.get(investId);
                }
                totalBuyAmt = totalBuyAmt.add(buyAmt);
                totalHoldShare = totalHoldShare.add(holdShare);
                investorMap.put("buyAmt",totalBuyAmt.toString());
                investorMap.put("holdShare",totalHoldShare.toString());
                investorMapMap.put(investId,investorMap);
                //借款列表
                Integer borrowId = transferDtl.getBorrowId();
                BigDecimal borrowBuyAmt = transferDtl.getBuyAmt();
                if(!borrowIdSet.contains(borrowId)){
                    borrowIdSet.add(borrowId);
                    Map<String,String> borrowMap = new HashMap<>();
                    borrowMap.put("borrowId",borrowId.toString());
                    borrowMap.put("borrowCustomerId",transferDtl.getBorrowCustomerId().toString());
                    borrowMap.put("borrowCustomerName",transferDtl.getBorrowCustomerName());
                    borrowMap.put("borrowCustomerIdCard",transferDtl.getBorrowCustomerIdCard());
                    borrowMap.put("borrowAmt",transferDtl.getBorrowAmt().toString());
                    borrowMap.put("borrowMonthCount",transferDtl.getBorrowMonthCount().toString());
                    borrowMap.put("borrowTotalBorrowFee",transferDtl.getBorrowTotalBorrowFee().toString());
                    borrowMap.put("borrowTotalInterest",transferDtl.getBorrowTotalInterest().toString());
                    borrowMap.put("borrowTotalManageFee",transferDtl.getBorrowTotalManageFee().toString());
                    borrowMap.put("borrowStartDate", DateUtil.dateYMD2Str(transferDtl.getBorrowStartDate()));
                    String borrowEndDate = transferDtl.getBorrowEndDate()==null ? null : DateUtil.dateYMD2Str(transferDtl.getBorrowEndDate());
                    borrowMap.put("borrowEndDate",borrowEndDate);
                    borrowMap.put("borrowFirstReturnDate",DateUtil.dateYMD2Str(transferDtl.getBorrowFirstReturnDate()));
                    borrowMap.put("borrowMonthReturnDay",transferDtl.getBorrowMonthReturnDay().toString());
                    borrowMap.put("borrowMonthPayment",transferDtl.getBorrowMonthPayment().toString());
                    borrowMapList.add(borrowMap);
                }
            }
        }
        for (Integer investorId : investorMapMap.keySet()) {
            investorMapList.add(investorMapMap.get(investorId));
        }
        contractMap.put("financerMapList",financerMapList);//转让人
        contractMap.put("investorMapList",investorMapList);//受让人
        contractMap.put("borrowMapList",borrowMapList);//借款列表
        logger.debug("【转让协议文本】contractMap=" + contractMap);
        return contractMap;
    }

}
