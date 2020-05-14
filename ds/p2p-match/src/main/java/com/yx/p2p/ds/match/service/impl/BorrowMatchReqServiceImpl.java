package com.yx.p2p.ds.match.service.impl;

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
import com.yx.p2p.ds.service.BorrowMatchReqService;
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
public class BorrowMatchReqServiceImpl implements BorrowMatchReqService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestMatchReqMapper investMatchReqMapper;

    @Autowired
    private FinanceMatchReqMapper financeMatchReqMapper;

    @Autowired
    private FinanceMatchResMapper financeMatchResMapper;

    @Autowired
    private FinanceMatchReqService financeMatchReqService;

    //幂等性接口：借款撮合请求
    //1.借款撮合
    //2.事务操作：更新投资撮合请求，添加借款撮合请求和借款撮合结果
    @Override
    public Result borrowMatchReq(FinanceMatchReq borrowMatchReq) {
        logger.debug("【借款撮合请求开始】borrowMatchReq=" + borrowMatchReq);
        //处理借款撮合
        Result result = this.checkNoBorrowMatch(borrowMatchReq);//检查借款未撮合
        if(Result.checkStatus(result)){//未撮合
            List<InvestMatchReq> resNoMatchInvestReqList = this.getWaitMatchAmtInvestReqList();
            List<InvestMatchReq> resMatchedInvestReqList = new ArrayList<>();
            List<FinanceMatchRes> resultBorrowMatchResList = new ArrayList<>();
            result = financeMatchReqService.dealFinanceMatch(borrowMatchReq,resNoMatchInvestReqList,resMatchedInvestReqList,resultBorrowMatchResList);
            logger.debug("【借款撮合结果】resultBorrowMatchResList=" + resultBorrowMatchResList);
            if(Result.checkStatus(result)){
                //事务操作：更新投资撮合请求，添加借款撮合请求和借款撮合结果
                result = this.updateMatchReqAndAddMatchReqAndRes(resMatchedInvestReqList,borrowMatchReq,resultBorrowMatchResList);
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

    //更新投资撮合请求，添加借款撮合请求和借款撮合结果
    @Transactional
    public Result updateMatchReqAndAddMatchReqAndRes(List<InvestMatchReq> resultInvestReqs,
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
            financeMatchResMapper.insertList(borrowMatchResList);
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

    public List<InvestMatchReq> getWaitMatchAmtInvestReqList() {
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