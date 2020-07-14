package com.yx.p2p.ds.borrow.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.borrow.mapper.BorrowMapper;
import com.yx.p2p.ds.borrow.mapper.CashflowMapper;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.BorrowProduct;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.model.payment.CustomerBank;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.borrow.BorrowProductService;
import com.yx.p2p.ds.service.borrow.BorrowThreadService;
import com.yx.p2p.ds.service.borrow.DebtDateValueService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/07/14/14:55
 */
@Service
public class BorrowThreadServiceImpl implements BorrowThreadService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CashflowMapper cashflowMapper;

    @Autowired
    private DebtDateValueService debtDateValueService;

    //线程池事务操作：添加现金流和债务每日价值
    @Transactional(rollbackFor = Exception.class)
    @Async("asyncServiceExecutor")
    public void addCashflowAndDebtDateValue(Borrow borrow){
        List<Cashflow> cashflows = new ArrayList<>();
        //添加现金流
        Result result = this.addCashflow(borrow,cashflows);
        //添加债务每日价值
        debtDateValueService.addBatchDebtDateValue(borrow,cashflows);
    }

    //添加现金流
    private Result addCashflow(Borrow borrow,List<Cashflow> cashflows) {
        //构建现金流
        Result result = this.buildCashflow(borrow,cashflows);
        this.insertCashFlowList2DB(cashflows);
        return result;
    }

    private Result insertCashFlowList2DB(List<Cashflow> cashflows) {
        logger.debug("【现金流插入数据库开始】cashflows=" + cashflows);
        Result result = Result.error();
        cashflowMapper.insertList(cashflows);
        result = Result.success();
        return result;
    }

    //构建现金流
    //cashflows作为返回结果
    private Result buildCashflow(Borrow borrow,List<Cashflow> cashflows) {
        logger.debug("【构建现金流开始】borrow=" + borrow);
        Result result = Result.error();
        Integer borrowMonthCount = borrow.getBorrowMonthCount();//借款期限
        Integer monthReturnDay = borrow.getMonthReturnDay();//还款日
        BigDecimal zero = BigDecimal.ZERO;
        //第0期：借款
        Cashflow borrowFlow = new Cashflow();
        borrowFlow.setIdStr("next value for MYCATSEQ_P2P_CASH_FLOW");
        borrowFlow.setBorrowId(borrow.getId());
        borrowFlow.setReturnDateNo(0);//还款日期编号(借款为0，还款从1开始)
        borrowFlow.setTradeDate(borrow.getStartDate());//交易日期：借款开始日期/还款日期
        borrowFlow.setArriveDate(DateUtil.addDay(borrowFlow.getTradeDate(),1));
        borrowFlow.setMonthPayment(borrow.getBorrowAmt());//月供：借款金额/月供
        borrowFlow.setPrincipal(zero);//本金
        borrowFlow.setInterest(zero);//利息
        borrowFlow.setManageFee(zero);//管理费
        borrowFlow.setRemainPrincipal(borrow.getBorrowAmt());//剩余本金
        borrowFlow.setPaidPrincipal(zero);//已还本金
        BeanHelper.setAddDefaultField(borrowFlow);
        cashflows.add(borrowFlow);
        //还款
        BigDecimal remainPrincipal = borrow.getBorrowAmt();//总剩余本金
        BigDecimal paidPrincipal = zero;//已还本金
        Date tradeDate =  borrow.getFirstReturnDate();
        for(int i=1; i <= borrowMonthCount; i++){
            Cashflow returnFlow = new Cashflow();
            returnFlow.setIdStr("next value for MYCATSEQ_P2P_CASH_FLOW");
            returnFlow.setBorrowId(borrow.getId());
            returnFlow.setReturnDateNo(i);//还款日期编号(借款为0，还款从1开始)
            //交易日期：借款开始日期/还款日期
            if(i==1){
                returnFlow.setTradeDate(tradeDate);//交易日期：借款开始日期
            }else{
                tradeDate = DateUtil.addMonth(tradeDate,1);
                returnFlow.setTradeDate(tradeDate);//交易日期：还款日期
            }
            returnFlow.setArriveDate(DateUtil.addDay(returnFlow.getTradeDate(),1));
            returnFlow.setManageFee(borrow.getMonthManageFee());//月管理费
            returnFlow.setMonthPayment(borrow.getMonthPayment());//月供：借款金额/月供
            //月利息=总剩余本金*月利率
            BigDecimal monthRate =
                    BigDecimalUtil.divide4(borrow.getMonthRate(),new BigDecimal("100"));//月利率
            BigDecimal monthInterest = BigDecimalUtil.round2In45(remainPrincipal.multiply(monthRate));
            returnFlow.setInterest(monthInterest);//月利息
            //月本金=月本息-月利息
            BigDecimal monthPrincipal =
                    borrow.getMonthPrincipalInterest().subtract(monthInterest);
            returnFlow.setPrincipal(monthPrincipal);//本金

            //总剩余本金=总剩余本金-月本金
            remainPrincipal = remainPrincipal.subtract(monthPrincipal);
            //总已还本金=借款金额-总剩余本金
            paidPrincipal = borrow.getBorrowAmt().subtract(remainPrincipal);
            //最后一期：剩余本金,总已还本金
            if(i==borrowMonthCount){
                //总已还本金=总已还本金+剩余本金
                paidPrincipal = paidPrincipal.add(remainPrincipal);
                //剩余本金=0
                remainPrincipal = zero;
            }
            returnFlow.setRemainPrincipal(remainPrincipal);//总剩余本金
            returnFlow.setPaidPrincipal(paidPrincipal);//总已还本金
            BeanHelper.setAddDefaultField(returnFlow);
            cashflows.add(returnFlow);
        }
        result = Result.success();
        logger.debug("【构建现金流】cashflows=" + cashflows);
        return result;

    }
}
