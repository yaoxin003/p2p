package com.yx.p2p.ds.match.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.match.FinanceMatchReqBizStateEnum;
import com.yx.p2p.ds.enums.match.FinanceMatchResBizStateEnum;
import com.yx.p2p.ds.enums.match.InvestMatchReqBizStateEnum;
import com.yx.p2p.ds.enums.match.MatchRemarkEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.match.mapper.FinanceMatchReqMapper;
import com.yx.p2p.ds.match.mapper.FinanceMatchResMapper;
import com.yx.p2p.ds.match.mapper.InvestMatchReqMapper;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.server.FinanceMatchReqServer;
import com.yx.p2p.ds.service.FinanceMatchReqService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @description:融资撮合请求
 * @author: yx
 * @date: 2020/04/30/14:37
 */
@Service
public class FinanceMatchReqServiceImpl implements FinanceMatchReqService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestMatchReqMapper investMatchReqMapper;

    @Autowired
    private FinanceMatchReqMapper financeMatchReqMapper;

    @Autowired
    private FinanceMatchResMapper financeMatchResMapper;

    //幂等性接口：借款撮合请求
    //Result.target=借款撮合结果
    @Override
    @Transactional
    public Result borrowMatchReq(FinanceMatchReq borrowMatchReq) {
        logger.debug("【借款撮合请求开始】borrowMatchReq=" + borrowMatchReq);
        //处理借款撮合
        List<InvestMatchReq> resultInvestReqs = new ArrayList<>();
        List<FinanceMatchRes> resultBorrowMatchResList = new ArrayList<>();
        Result result = this.checkNoBorrowMatch(borrowMatchReq);//检查借款未撮合
        if(Result.checkStatus(result)){//未撮合
            result = this.dealBorrowMatch(borrowMatchReq,resultInvestReqs,resultBorrowMatchResList);
            logger.debug("【借款撮合结果】resultBorrowMatchResList=" + resultBorrowMatchResList);
            if(Result.checkStatus(result)){
                //result.target中借款撮合结果
                result = this.updateMatchReqAndAddMatchReqAndRes(resultInvestReqs,borrowMatchReq,resultBorrowMatchResList);
            }
        }else{//已经撮合，返回借款撮合结果
            result = Result.success();
            List<FinanceMatchRes> borrowMatchResList = this.getBorrowMatchResList(
                    borrowMatchReq.getFinanceCustomerId(),borrowMatchReq.getFinanceBizId());
            result.setTarget(borrowMatchResList);
            logger.debug("【借款撮合请求,幂等性验证，已经撮合过】result=" + result);
        }
        return result;
    }

    //检查借款未撮合。
    //Result.status=true未撮合
    //Result.status=error 已撮合

    private Result checkNoBorrowMatch(FinanceMatchReq borrowMatchReq) {
        Result result = Result.error();
        List<FinanceMatchRes> borrowMatchRes = this.getBorrowMatchResList(borrowMatchReq.getFinanceCustomerId(),
                borrowMatchReq.getFinanceBizId());
        if(borrowMatchRes.isEmpty()){
            result = Result.success();
            result.setTarget(borrowMatchRes);
        }
        return result;
    }

    public List<FinanceMatchRes> getBorrowMatchResList(Integer financeCustomerId,String financeBizId) {
        FinanceMatchRes param = new FinanceMatchRes();
        param.setFinanceCustomerId(financeCustomerId);
        param.setFinanceBizId(financeBizId);
        List<FinanceMatchRes> res = financeMatchResMapper.select(param);
        return res;
    }

    private Result updateMatchReqAndAddMatchReqAndRes(List<InvestMatchReq> resultInvestReqs,
             FinanceMatchReq borrowMatchReq, List<FinanceMatchRes> borrowMatchResList) {
        Result result = Result.error();
        //更新撮合请求
        for(InvestMatchReq req : resultInvestReqs){
            BeanHelper.setUpdateDefaultField(req);
        }
        this.updateBatchMatchReqListWaitAmt(resultInvestReqs);
        //插入借款撮合请求
        int count = financeMatchReqMapper.insert(borrowMatchReq);
        //插入借款撮合结果
        if(count == 1){
            for(FinanceMatchRes borrowMatchRes: borrowMatchResList){
                borrowMatchRes.setFinanceMatchId(borrowMatchReq.getId());
            }
            financeMatchResMapper.insertBatchFinanceMatchResList(borrowMatchResList);
        }
        result = Result.success();
        result.setTarget(borrowMatchResList);
        logger.debug("【插入借款撮合和借款撮合结果】result=" + result);
        return result;
    }

    //更新投资撮合请求的等待撮合金额
    private Result updateBatchMatchReqListWaitAmt(List<InvestMatchReq> resultInvestReqs) {
        logger.debug("【更新投资撮合请求的等待撮合金额，入参】resultInvestReqs=" + resultInvestReqs);
        Result result = Result.error();
        int count = 0;
        for(InvestMatchReq req: resultInvestReqs){
            InvestMatchReq param = new InvestMatchReq();
            param.setId(req.getId());
            param.setWaitAmt(req.getWaitAmt());
            BeanHelper.setUpdateDefaultField(param);
            investMatchReqMapper.updateByPrimaryKeySelective(param);
            count++;
        }
        result = Result.success();
        logger.debug("【更新投资撮合请求的等待撮合金额，结果】count="+ count + ",result=" + result);
        return result;
    }


    //若借款撮合成功：一个事务完成
    // 1.更新借款撮合状态和金额
    //2.插入借款撮合结果
    //3.批量更新投资撮合状态和金额
    //从投资撮合请求集合中选择数据，存入waitAmt字段中
    //返回值：resultInvestReqs 投资撮合集合 resultBorrowMatchRes借款撮合结果
    private Result dealBorrowMatch(FinanceMatchReq borrowMatchReq,List<InvestMatchReq> resultInvestReqs,
                                   List<FinanceMatchRes> resultBorrowMatchResList) {
        logger.debug("【借款撮合开始】borrowMatchReq=" + borrowMatchReq);
        Result result = Result.error();
        BeanHelper.setAddDefaultField(borrowMatchReq);
        List<FinanceMatchRes> borrowRes = new ArrayList<>();
        List<InvestMatchReq> queryInvestReqList = this.getWaitMatchAmtInvestReqList(borrowMatchReq);
        BigDecimal zero = BigDecimal.ZERO;
        String bizId = borrowMatchReq.getFinanceBizId();
        BigDecimal remainShare = new BigDecimal("1");
        int count =0;
        for(InvestMatchReq investReq : queryInvestReqList){
            logger.debug("【借款撮合】第" + ++count + "次撮合，bizId=" + bizId);
            BigDecimal borrowWaitAmt = borrowMatchReq.getWaitAmt();
            logger.debug("【借款撮合】bizId=" + bizId + "，借款待撮合金额=" + borrowWaitAmt);
            if(borrowWaitAmt.compareTo(zero) == 1){//需要借款待撮合
                BigDecimal investWaitAmt = investReq.getWaitAmt();
                BigDecimal subAmt =  borrowWaitAmt.subtract(investWaitAmt);//差额=借款待撮合-投资待撮合
                logger.debug("【借款撮合】bizId=" + bizId + "，差额=" + subAmt);
                //构建借款撮合结果
                FinanceMatchRes financeMatchRes = this.buildAddFinanceMatchRes(investReq,borrowMatchReq);
                resultBorrowMatchResList.add(financeMatchRes);
                if(subAmt.compareTo(zero) == 1){//未撮完
                    logger.debug("【借款撮合】bizId=" + bizId + "，进入未撮完条件，撮合前：剩余借款" + borrowWaitAmt);
                    financeMatchRes.setTradeAmt(investWaitAmt);
                    borrowMatchReq.setWaitAmt(subAmt);//融资剩余金额
                    investReq.setWaitAmt(zero);//投资剩余金额
                    //计算撮合比例（需要先计算出交易金额和借款待撮合金额）
                    remainShare = this.calMatchShareAndReturnRemainShare(financeMatchRes,borrowMatchReq,remainShare);
                    resultInvestReqs.add(investReq);
                    logger.debug("【借款撮合】bizId=" + bizId + "，未撮完，撮合后：剩余借款" + borrowMatchReq.getWaitAmt());
                }else if(subAmt.compareTo(zero) == 0){//撮合完成
                    logger.debug("【借款撮合】bizId=" + bizId + "，进入撮正好条件，撮合前：剩余借款" + borrowWaitAmt);
                    financeMatchRes.setTradeAmt(investWaitAmt);
                    borrowMatchReq.setWaitAmt(zero);
                    investReq.setWaitAmt(zero);
                    //计算撮合比例（需要先计算出交易金额和借款待撮合金额）
                    remainShare = this.calMatchShareAndReturnRemainShare(financeMatchRes,borrowMatchReq,remainShare);
                    resultInvestReqs.add(investReq);
                    logger.debug("【借款撮合】bizId=" + bizId + "，撮正好，撮合后：剩余借款" + borrowMatchReq.getWaitAmt());
                }else{//撮多了
                    logger.debug("【借款撮合】bizId=" + bizId + "，进入撮多了条件，撮合前：剩余借款" + borrowWaitAmt);
                    financeMatchRes.setTradeAmt(borrowWaitAmt);
                    borrowMatchReq.setWaitAmt(zero);
                    investReq.setWaitAmt(investWaitAmt.subtract(borrowWaitAmt));
                    //计算撮合比例（需要先计算出交易金额和借款待撮合金额）
                    remainShare = this.calMatchShareAndReturnRemainShare(financeMatchRes,borrowMatchReq,remainShare);
                    resultInvestReqs.add(investReq);
                    logger.debug("【借款撮合】bizId=" + bizId + "，撮多了，撮合后：待撮合金额" + borrowMatchReq.getWaitAmt());
                }
            }else if(borrowWaitAmt.compareTo(zero) == 0){//撮合成功
                logger.debug("【借款撮合】bizId=" + bizId + "，撮合成功");
                result = Result.success();
                break;
            }else if(borrowWaitAmt.compareTo(zero) == -1){//撮合异常
                logger.debug("【借款撮合】bizId=" + bizId + "，撮合异常，待撮合金额=" + borrowWaitAmt);
                result = Result.success();
                break;
            }
        }
        if(borrowMatchReq.getWaitAmt().compareTo(zero) != 0){//撮合失败，
            logger.debug("【借款撮合】bizId=" + bizId + "，撮合失败，投资人钱不足");
            result = Result.error("【借款撮合失败，投资不足】借款未撮合金额：" + borrowMatchReq.getWaitAmt());
        }else{//撮合成功
            result = Result.success();
        }
        return result;
    }

    private FinanceMatchRes buildAddFinanceMatchRes(InvestMatchReq investReq, FinanceMatchReq borrowMatchReq) {
        FinanceMatchRes financeMatchRes = new FinanceMatchRes();
        financeMatchRes.setInvestCustomerId(investReq.getInvestCustomerId());
        financeMatchRes.setInvestCustomerName(investReq.getInvestCustomerName());
        financeMatchRes.setFinanceCustomerId(borrowMatchReq.getFinanceCustomerId());
        financeMatchRes.setFinanceCustomerName(borrowMatchReq.getFinanceCustomerName());
        financeMatchRes.setInvestMatchId(investReq.getId());
        financeMatchRes.setFinanceAmt(borrowMatchReq.getFinanceAmt());
        financeMatchRes.setBorrowProductId(borrowMatchReq.getBorrowProductId());
        financeMatchRes.setBorrowProductName(borrowMatchReq.getBorrowProductName());
        financeMatchRes.setBorrowYearRate(borrowMatchReq.getBorrowYearRate());
        financeMatchRes.setFinanceBizId(borrowMatchReq.getFinanceBizId());
        financeMatchRes.setFinanceOrderSn(borrowMatchReq.getFinanceOrderSn());
        financeMatchRes.setInvestBizId(investReq.getInvestBizId());
        financeMatchRes.setInvestMatchId(investReq.getId());
        financeMatchRes.setInvestOrderSn(investReq.getInvestOrderSn());
        financeMatchRes.setRemark(MatchRemarkEnum.BORROW.getDesc());
        BeanHelper.setAddDefaultField(financeMatchRes);
        return financeMatchRes;
    }

    //计算撮合比例并返回剩余撮合比例（需要先计算出交易金额和借款待撮合金额）
    // 非最后一次撮合：交易金额/借款金额
    //最后一次撮合：1-前面撮合比例
    private BigDecimal calMatchShareAndReturnRemainShare(FinanceMatchRes financeMatchRes, FinanceMatchReq borrowMatchReq, BigDecimal remainShare) {
        BigDecimal share = BigDecimal.ZERO;
        if(borrowMatchReq.getWaitAmt().compareTo(BigDecimal.ZERO) == 0){
            share = remainShare;
        }else{
            share = BigDecimalUtil.divide2(financeMatchRes.getTradeAmt(),borrowMatchReq.getFinanceAmt());
            remainShare = remainShare.subtract(share);
        }
        financeMatchRes.setMatchShare(share);
        return remainShare;
    }

    private List<InvestMatchReq> getWaitMatchAmtInvestReqList(FinanceMatchReq financeMatchReq) {
        List<InvestMatchReq> investReqList = this.selectWaitMatchAmtInvestReqList();
        return investReqList;
    }

    //查询数据库中投资撮合数据。
    // where条件：状态新增或撮合中，排序：按照level倒序
    private List<InvestMatchReq> selectWaitMatchAmtInvestReqList() {
        Example example = new Example(InvestMatchReq.class);
        List<String> bizStateList = new ArrayList<>();
        bizStateList.add(InvestMatchReqBizStateEnum.NEW_ADD.getState());
        bizStateList.add(InvestMatchReqBizStateEnum.MATCHING.getState());
        example.createCriteria().andIn("bizState",bizStateList)
                                .andGreaterThan("waitAmt",new BigDecimal("0"));
        example.setOrderByClause("level desc");
        List<InvestMatchReq> investReqList = investMatchReqMapper.selectByExample(example);
        logger.debug("【查询数据库获得投资撮合请求】investReqList=" + investReqList);
        return investReqList;
    }

    //放款通知
    //幂等性检查：查询撮合请求数据的业务状态是否为撮合确认
    //若状态不是撮合确认，
    // 事务操作：撮合请求和撮合结果状态更新为撮合确认
    @Override
    public Result loanNotice(HashMap<String, String> loanMap) {
        logger.debug("【放款，入参：loadMap=】" + loanMap);
        Result result = Result.error();
        String financeOrderSn = loanMap.get("orderSn");//都是借款编号borrowId
        String borrowId = loanMap.get("bizId");//都是借款编号borrowId
        String financeCustomerId = loanMap.get("customerId");//融资客户
        String status = loanMap.get("status");
        //1.验证撮合请求的业务状态是否为新增
        Integer financeCustomerIdInt = Integer.parseInt(financeCustomerId);//融资客户
        FinanceMatchReq resFinanceMatchReq = new FinanceMatchReq();//不需要二次查询主键
        result = this.checkNoLoanNotice(financeOrderSn,financeCustomerIdInt,resFinanceMatchReq);
        if(Result.checkStatus(result)){
            //查询借款撮合数据
            List<FinanceMatchRes> borrowMatchResList = this.getBorrowMatchResList(
                    financeCustomerIdInt, borrowId);
            //2.事务操作：撮合请求和撮合结果状态更新为撮合确认
            result = this.dealLoanNoticeData(financeCustomerId,borrowId,borrowMatchResList,resFinanceMatchReq.getId());
        }
        result = Result.success();
        logger.debug("【放款，结果：result=】" + result);
        return result;
    }

    //事务操作：撮合请求和撮合结果状态更新为撮合确认
    @Transactional
    private Result dealLoanNoticeData(String financeCustomerId, String borrowId,
                                List<FinanceMatchRes> borrowMatchResList, Integer financeMatchReqId) {
        logger.debug("【撮合请求和撮合结果状态更新为撮合确认】入参：financeCustomerId="+ financeCustomerId
                + ",borrowId=" + borrowId+ ",borrowMatchResList=" + borrowMatchResList
                + ",financeMatchReqId=" + financeMatchReqId);
        Result result = Result.error();
        //1.撮合请求业务状态更新为撮合确认
        FinanceMatchReq reqParam = new FinanceMatchReq();
        reqParam.setId(financeMatchReqId);
        reqParam.setBizState(FinanceMatchReqBizStateEnum.BORROW_MATCH_CONFIRM.getBizState());//借款撮合确认
        BeanHelper.setUpdateDefaultField(reqParam);
        financeMatchReqMapper.updateByPrimaryKeySelective(reqParam);
        //2.撮合结果业务状态更新为撮合确认
        for(FinanceMatchRes res : borrowMatchResList){
            FinanceMatchRes resParam = new FinanceMatchRes();
            resParam.setId(res.getId());
            resParam.setBizState(FinanceMatchResBizStateEnum.BORROW_MATCH_CONFIRM.getBizState());//借款撮合确认
            BeanHelper.setUpdateDefaultField(resParam);
            financeMatchResMapper.updateByPrimaryKeySelective(resParam);
        }
        result = Result.success();
        return result;
    }

    //1.检查撮合请求的业务状态是否为新增
    //resFinanceMatchReq后续业务使用，不需要二次查询
    private Result checkNoLoanNotice(String financeOrderSn, Integer financeCustomerId,FinanceMatchReq resFinanceMatchReq) {
        logger.debug("【检查没有放款数据】入参financeOrderSn=" + financeOrderSn
                + ",financeCustomerId=" + financeCustomerId);
        Result result = Result.error();
        FinanceMatchReq param = new FinanceMatchReq();
        param.setFinanceOrderSn(financeOrderSn);
        param.setFinanceCustomerId(financeCustomerId);
        FinanceMatchReq dbFinanceMatchReq = financeMatchReqMapper.selectOne(param);
        logger.debug("撮合请求【resFinanceMatchReq=】" + dbFinanceMatchReq);
        //业务状态为新增才可以进行后续操作
        if(dbFinanceMatchReq != null &&
                (dbFinanceMatchReq.getBizState().equals(FinanceMatchReqBizStateEnum.NEW_ADD.getBizState()))){
            resFinanceMatchReq.setId(dbFinanceMatchReq.getId());
            result = Result.success();
        }else{
            result = Result.error();
        }
        logger.debug("【检查撮合请求的业务状态是否为新增】result.status=error说明已经执行操作；" +
                "result.status=ok说明没有更新，继续执行。result=" + result);
        return result;
    }
}
