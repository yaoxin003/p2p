package com.yx.p2p.ds.borrow.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.borrow.mapper.BorrowContractVoMapper;
import com.yx.p2p.ds.borrow.mapper.BorrowDtlMapper;
import com.yx.p2p.ds.borrow.mapper.BorrowMapper;
import com.yx.p2p.ds.borrow.mapper.CashflowMapper;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.SystemSourceEnum;
import com.yx.p2p.ds.enums.borrow.BorrowBizStateEnum;
import com.yx.p2p.ds.enums.match.FinanceMatchReqLevelEnum;
import com.yx.p2p.ds.enums.match.MatchRemarkEnum;
import com.yx.p2p.ds.enums.mq.MQStatusEnum;
import com.yx.p2p.ds.enums.payment.PaymentTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.BorrowDtl;
import com.yx.p2p.ds.model.borrow.BorrowProduct;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.payment.CustomerBank;
import com.yx.p2p.ds.model.payment.Payment;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.server.FinanceMatchReqServer;
import com.yx.p2p.ds.server.InvestServer;
import com.yx.p2p.ds.server.PaymentServer;
import com.yx.p2p.ds.service.BorrowProductService;
import com.yx.p2p.ds.service.BorrowService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import com.yx.p2p.ds.vo.BorrowContractVo;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/18:47
 */
@Service
public class BorrowServiceImpl implements BorrowService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BorrowProductService borrowProductService;

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private BorrowDtlMapper borrowDtlMapper;

    @Autowired
    private CashflowMapper cashflowMapper;

    @Autowired
    private BorrowContractVoMapper borrowContractVoMapper;

    @Reference
    private FinanceMatchReqServer financeMatchReqServer;

    @Reference
    private PaymentServer paymentServer;

    @Reference
    private CrmServer crmServer;

    @Reference
    private InvestServer investServer;


    /**
        * @description: 借款签约
        *  1.事务操作：添加借款数据,借款和现金流
        *  2.调用撮合系统发送借款撮合（幂等性接口：多次调用若撮合成功反复调用都为成功且返回撮合结果）
        * 3.更新借款状态为已签约，借款状态更新失败不会影响添加借款和调用借款撮合
        * @author:  YX
        * @date:    2020/04/30 10:11
        * @param: borrow
        * @return: java.util.List<com.yx.p2p.ds.model.match.FinanceMatchRes> 融资撮合结果
        */
    @Override
    public Result sign(Borrow borrow) {
        logger.debug("【借款签约】borrow=" + borrow);
        //事务操作：添加借款数据,借款和现金流
        Result result = this.addBorrowAndCashflow(borrow);
        if(Result.checkStatus(result)){
            //发送借款撮合并签约
            result = this.sendBorrowMatchAndSign(borrow);
        }
        return result;
    }

    //发送借款撮合并签约
    //1.调用撮合系统发送借款撮合
    //2.撮合成功,添加协议明细，更新借款状态已签约
    private Result sendBorrowMatchAndSign(Borrow borrow) {
        Result result = Result.error();
        //调用撮合系统发送借款撮合
        result = this.dealBorrowMatchReq(borrow);
        if(Result.checkStatus(result)){
            Object target = result.getTarget();
            if(target != null ){
                List<FinanceMatchRes> matchResList = (List<FinanceMatchRes>)target;
                //撮合成功,添加协议明细，更新借款状态已签约
                result = this.adddBorrowDtlAndUpdateBorrowSigned(borrow,matchResList);
            }
        }
        return result;
    }

    //事务操作：根据撮合结果，添加借款明细，更新借款状态已签约
    //用catch捕获异常，不向上抛出异常，保证调用者不受该异常影响
    @Transactional
    private Result adddBorrowDtlAndUpdateBorrowSigned(Borrow borrow, List<FinanceMatchRes> matchResList) {
        Result result = Result.error();
        try{
            result = this.addBorrowDtlList(matchResList);
            if(Result.checkStatus(result)){
                // 更新借款状态已签约
                this.updateBorrowBizStateSigned(borrow.getId());
            }
        }catch (Exception e){
            //用catch捕获异常，不向上抛出异常，保证调用者不受该异常影响
            LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Result addBorrowDtlList(List<FinanceMatchRes> matchResList) {
        Result result = Result.error();
        List<BorrowDtl> borrowDtlList = new ArrayList<>();
        for(FinanceMatchRes res : matchResList){
            BorrowDtl borrowDtl = this.buildAddBorrowDtl(res);
            borrowDtlList.add(borrowDtl);
        }
        if(!borrowDtlList.isEmpty()){
            logger.debug("【批量插入借款明细】");
            borrowDtlMapper.insertBatchBorrowDtlList(borrowDtlList);
        }
        result = Result.success();
        return result;
    }

    private BorrowDtl buildAddBorrowDtl(FinanceMatchRes res) {
        logger.debug("【构建添加借款明细：入参】res=" + res);
        Invest invest = investServer.getInvestByInvestId(Integer.valueOf(res.getInvestBizId()));
        BorrowDtl borrowDtl = new BorrowDtl();
        try{
            borrowDtl.setBorrowId(Integer.valueOf(res.getFinanceBizId()));
            BeanUtils.copyProperties(borrowDtl,res);
            BeanUtils.copyProperties(borrowDtl,invest);
            borrowDtl.setId(null);
            BeanHelper.setAddDefaultField(borrowDtl);
        }catch(Exception e){
            LoggerUtil.addExceptionLog(e,logger);
        }
        logger.debug("【构建添加借款明细：结果】borrowDtl=" + borrowDtl);
        return borrowDtl;
    }

    //更新借款状态已签约
    private Result updateBorrowBizStateSigned(Integer borrowId) {
        logger.debug("【更新借款业务状态为已签约】borrowId=" + borrowId);
        Result result = this.updateBorrowBizState(borrowId,BorrowBizStateEnum.SIGNED);
        return result;
    }

    //更新数据库业务状态
    private Result updateBorrowBizState(Integer borrowId,BorrowBizStateEnum borrowBizStateEnum) {
        logger.debug("【更新借款业务状态入参】borrowId=" + borrowId + ",borrowBizStateEnum=" + borrowBizStateEnum);
        Result result = Result.error();
        Borrow updateBorrow = new Borrow();
        updateBorrow.setId(borrowId);
        updateBorrow.setBizState(borrowBizStateEnum.getState());
        int count = borrowMapper.updateByPrimaryKeySelective(updateBorrow);
        if(count == 1){
            result = Result.success();
        }
        logger.debug("【更新借款业务状态结果】count=" + count + ",result=" + result);
        return result;
    }

    //事务操作：添加借款数据,借款和现金流
    @Transactional
    private Result addBorrowAndCashflow(Borrow borrow) {
        //添加借款数据
        Result result = this.addNewBorrow(borrow);
        //添加现金流
        result = this.addCashflow(borrow);
        return result;
    }

    //调用发送借款撮合
    //Result.target=借款撮合结果
    public Result dealBorrowMatchReq(Borrow borrow) {
        logger.debug("【调用发送借款撮合】borrow=" + borrow);
        FinanceMatchReq borrowMatchReq = new FinanceMatchReq();
        Result result = this.buildBorrowMatchReq(borrow,borrowMatchReq);
        //Result.target=借款撮合结果
        result = financeMatchReqServer.borrowMatchReq(borrowMatchReq);
        logger.debug("【调用借款撮合系统：借款撮合】result=" + result);
        return result;
    }

    private Result buildBorrowMatchReq(Borrow borrow, FinanceMatchReq borrowMatchReq) {
        logger.debug("【构建借款撮合开始】borrow=" + borrow);
        Result result = Result.error();
        String borrowId = String.valueOf(borrow.getId());
        borrowMatchReq.setFinanceCustomerName(borrow.getCustomerName());//借款人姓名
        borrowMatchReq.setFinanceAmt(borrow.getBorrowAmt());//借款金额
        borrowMatchReq.setFinanceBizId(borrowId);
        borrowMatchReq.setFinanceCustomerId(borrow.getCustomerId());
        borrowMatchReq.setLevel(FinanceMatchReqLevelEnum.BORROW.getLevel());
        borrowMatchReq.setFinanceOrderSn(borrowId);
        borrowMatchReq.setBorrowProductId(borrow.getBorrowProductId());
        borrowMatchReq.setBorrowProductName(borrow.getBorrowProductName());
        borrowMatchReq.setBorrowYearRate(borrow.getYearRate());
        borrowMatchReq.setRemark(MatchRemarkEnum.BORROW.getDesc());
        borrowMatchReq.setWaitAmt(borrowMatchReq.getFinanceAmt());
        BeanHelper.setAddDefaultField(borrowMatchReq);
        result = Result.success();
        logger.debug("【构建借款撮合】borrowMatchReq=" + borrowMatchReq);
        return result;
    }

    //添加现金流
    private Result addCashflow(Borrow borrow) {
        //构建现金流
        List<Cashflow> cashflows = new ArrayList<>();
        Result result = this.buildCashflow(borrow,cashflows);
        this.insertCashFlowList2DB(cashflows);
        return result;
    }

    private Result insertCashFlowList2DB(List<Cashflow> cashflows) {
        logger.debug("【现金流插入数据库开始】cashflows=" + cashflows);
        Result result = Result.error();
        cashflowMapper.insertBatchCashflowList(cashflows);
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
        borrowFlow.setBorrowId(borrow.getId());
        borrowFlow.setReturnDateNo(0);//还款日期编号(借款为0，还款从1开始)
        borrowFlow.setTradeDate(borrow.getStartDate());//交易日期：借款开始日期/还款日期
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
        for(int i=1; i <= borrowMonthCount; i++){
            Cashflow returnFlow = new Cashflow();
            returnFlow.setBorrowId(borrow.getId());
            returnFlow.setReturnDateNo(i);//还款日期编号(借款为0，还款从1开始)
            returnFlow.setTradeDate(borrow.getStartDate());//交易日期：借款开始日期/还款日期
            returnFlow.setManageFee(borrow.getMonthManageFee());//月管理费
            returnFlow.setMonthPayment(borrow.getMonthPayment());//月供：借款金额/月供
            //月利息=总剩余本金*月利率
            BigDecimal monthRate =
                    BigDecimalUtil.divide4(borrow.getMonthRate(),new BigDecimal("100"));//月利率
            BigDecimal monthInterest = remainPrincipal.multiply(monthRate);

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

    //添加借款数据
    private Result addNewBorrow(Borrow borrow) {
        //构建新借款数据
        Result result = this.buildNewBorrow(borrow);
        //添加新借款数据
        result = this.addNewBorrow2DB(borrow);
        return result;
    }

    private Result buildNewBorrow(Borrow borrow) {
        logger.debug("【构建新借款对象开始】borrow=" + borrow);
        Result result = Result.success();
        //借款产品信息
        BorrowProduct borrowProduct =
                borrowProductService.getBorrowProductById(borrow.getBorrowProductId());
        borrow.setBorrowProductName(borrowProduct.getName());
        borrow.setMonthFeeRate(borrowProduct.getMonthFeeRate());
        borrow.setMonthManageRate(borrowProduct.getMonthManageRate());
        borrow.setMonthRate(borrowProduct.getMonthRate());
        //客户银行卡信息
        CustomerBank customerBank = paymentServer.getCustomerBankById(borrow.getCustomerBankId());
        borrow.setBaseBankName(customerBank.getBaseBankName());
        borrow.setBankAccount(customerBank.getBankAccount());
        borrow.setBankCode(customerBank.getBankCode());
        borrow.setPhone(customerBank.getPhone());
        //客户信息
        Customer customer = crmServer.getCustomerById(borrow.getCustomerId());
        borrow.setCustomerName(customer.getName());
        borrow.setCustomerIdCard(customer.getIdCard());

        //月供(月本息+月管理费)
        BigDecimal monthFeeRate =
                BigDecimalUtil.divide4(borrow.getMonthFeeRate(),new BigDecimal("100"));//月费率
        BigDecimal monthPay = this.calMonthPayOrMonthPrincipalInterest(borrow.getBorrowAmt(),
                monthFeeRate,borrow.getBorrowMonthCount());
        if(monthPay.compareTo(borrow.getMonthPayment()) != 0){
            String monthPayMsg = "【前后台计算月供/月本息不同】：后台月供/月本息金额:" +
                    monthPay + ",前台月供/月本息金额:" + borrow.getMonthPayment();
            result = Result.error(monthPayMsg);
            logger.debug(monthPayMsg);
            throw new RuntimeException(monthPayMsg);
        }else {
            borrow.setMonthPayment(monthPay);
        }
        //月本息
        BigDecimal monthRate =
                BigDecimalUtil.divide4(borrow.getMonthRate(),new BigDecimal("100"));//月利率
        BigDecimal yearRate = monthRate.multiply(new BigDecimal("12"));
        borrow.setYearRate(yearRate);
        BigDecimal monthPrincipalInterest = this.calMonthPayOrMonthPrincipalInterest(borrow.getBorrowAmt(),monthRate,borrow.getBorrowMonthCount());
        borrow.setMonthPrincipalInterest(monthPrincipalInterest);
        //总借款费用=月供*借款月数-借款金额
        BigDecimal totalBorrowFee =this.calTotalBorrowFee(monthPay,borrow.getBorrowMonthCount(),borrow.getBorrowAmt());
        if(totalBorrowFee.compareTo(borrow.getTotalBorrowFee()) != 0){
            String borrowFeeMsg = "【前后台计算借款费用不同】：后台借款费用:" + totalBorrowFee + ",前台借款费用:" + borrow.getTotalBorrowFee();
            result = Result.error(borrowFeeMsg);
            logger.error(borrowFeeMsg);
            throw new RuntimeException(borrowFeeMsg);
        }else{
            borrow.setTotalBorrowFee(totalBorrowFee);
        }
        //总利息=月本息*借款月数-借款金额
        BigDecimal totalInterest =  this.calTotalInterest(monthPrincipalInterest,borrow.getBorrowMonthCount(),borrow.getBorrowAmt());
        borrow.setTotalInterest(totalInterest);
        //总管理费=总借款费用-总利息
        BigDecimal totalManageFee = this.calTotalManageFee(totalBorrowFee,totalInterest);
        borrow.setTotalManageFee(totalManageFee);
        //月管理费1=总管理费/借款月数
        BigDecimal monthManageFee1 = this.calMonthManageFee(totalManageFee,borrow.getBorrowMonthCount());
        borrow.setMonthManageFee(monthManageFee1);
        logger.debug("【月管理费1】" + monthManageFee1);
        //月管理费2=月供-月本息//未使用该值
        BigDecimal monthManageFee2 = monthPay.subtract(monthPrincipalInterest);
       logger.debug("【月管理费2】" + monthManageFee2);
        //借款开始时间
        Date startDate = new Date();
        borrow.setStartDate(startDate);
        //借款结束时间
        Date endDate = DateUtil.addMonth(startDate,borrow.getBorrowMonthCount());
        borrow.setEndDate(endDate);
        //构建还款日和首个还款日期
        this.buildMonthReturnDayAndFirstReturnDate(startDate,borrow);
        //默认信息
        BeanHelper.setAddDefaultField(borrow);
        logger.debug("【构建新借款对象】borrow=" + borrow + "result=" + result);
        return result;
    }

    //月管理费=总管理费/借款月数
    private BigDecimal calMonthManageFee(BigDecimal totalManageFee, Integer borrowMonthCount) {
        logger.debug("【计算月管理费】总管理费=" + totalManageFee + ",借款期限" + borrowMonthCount);
        BigDecimal borrowMonthCountBig = new BigDecimal(String.valueOf(borrowMonthCount));
        BigDecimal monthManageFee = BigDecimalUtil.divide2(totalManageFee,borrowMonthCountBig);
        logger.debug("【月管理费】monthManageFee=" + monthManageFee);
        return monthManageFee;
    }

    /**
        * @description:构建还款日和首个还款日期
        * 借款开始日期1-15日则还款日为本月28，
        * 借款开始日期16-31则还款日为下月15日
        * @author:  YX
        * @date:    2020/04/29 17:12
        * @param: startDate 借款开始日期
        * @return: void
        * @throws: 
        */
    private void buildMonthReturnDayAndFirstReturnDate(Date startDate,Borrow borrow) {
        int monthReturnDay = DateUtil.getDay(startDate);

        Date firstReturnDate = startDate;
        if(monthReturnDay <= 15){
            firstReturnDate = DateUtil.setDayOfDate(firstReturnDate,28);
            logger.debug("if==>firstReturnDate=" + firstReturnDate);
        }else{
            firstReturnDate = DateUtil.setDayOfDate(firstReturnDate,15);
            logger.debug("else1==>firstReturnDate=" + firstReturnDate);
            firstReturnDate = DateUtil.addMonth(firstReturnDate,1);
            logger.debug("else2==>firstReturnDate=" + firstReturnDate);
        }
        borrow.setMonthReturnDay(monthReturnDay);
        borrow.setFirstReturnDate(firstReturnDate);
        logger.debug("【每月还款日】monthReturnDay=" + monthReturnDay);
        logger.debug("【首个还款日期】firstReturnDate=" + DateUtil.dateYMD2Str(firstReturnDate));
    }

    //总管理费=总借款费用-总利息
    private BigDecimal calTotalManageFee(BigDecimal totalBorrowFee, BigDecimal totalInterest) {
        BigDecimal totalManageFee = totalBorrowFee.subtract(totalInterest);
        logger.debug("【总管理费】totalManageFee=" + totalManageFee);
        return totalManageFee;
    }

    //计算总利息=月本息*借款月数-借款金额
    private BigDecimal calTotalInterest(BigDecimal monthPrincipalInterest,Integer borrowMonthCount,BigDecimal borrowAmt) {
        logger.debug("【计算总利息】月本息=" + monthPrincipalInterest + ",借款期限" + borrowMonthCount + ",借款金额" + borrowAmt);
        BigDecimal totalInterest = monthPrincipalInterest.multiply(new BigDecimal(borrowMonthCount)).subtract(borrowAmt);
        logger.debug("【总利息】totalInterest=" + totalInterest);
        return totalInterest;
    }

    /**
        * @description: 借款费用=月供*借款月数-借款金额
        * @author:  YX
        * @date:    2020/04/29 13:59
        * @param: monthPay 月供
        * @param: borrowMonthCount 借款月数
        * @param: borrowAmt 借款金额
        * @return: java.math.BigDecimal
        * @throws:
        */
    private BigDecimal calTotalBorrowFee(BigDecimal monthPay, Integer borrowMonthCount, BigDecimal borrowAmt) {
        logger.debug("【计算借款费用】月供=" + monthPay + ",借款期限" + borrowMonthCount + ",借款金额" + borrowAmt);
        BigDecimal borrowFee = monthPay.multiply(new BigDecimal(borrowMonthCount)).subtract(borrowAmt);
        logger.debug("【借款费用】borrowFee=" + borrowFee);
        return borrowFee;
    }


    /**
        * @description: 计算月供(月本息+月管理费)或计算月本息
        * 月供=[借款金额×月费率×(1+月费率)^借款月数]÷[(1+月费率)^借款月数-1]
        * 月本息=[借款金额×月费率×(1+月利率)^借款月数]÷[(1+月利率)^借款月数-1]
        * @author:  YX
        * @date:    2020/04/29 13:03
        * @param: borrowAmt 借款金额
        * @param: monthRate 月利率
        * @param: borrowMonthCount 借款月数（借款期限）
        * @return: java.math.BigDecimal 月供
        */
    private BigDecimal calMonthPayOrMonthPrincipalInterest(BigDecimal borrowAmt, BigDecimal rate, Integer borrowMonthCount) {
        logger.debug("【计算月供/月本息】借款金额=" + borrowAmt + ",月费率/月利率=" + rate + ",借款期数=" + borrowMonthCount);
        BigDecimal one = new BigDecimal("1");
        BigDecimal monthPay01 = one.add(rate);//1+月利率
        BigDecimal monthPay02 = monthPay01.pow(borrowMonthCount);//(1+月利率)^借款月数
        BigDecimal monthPay1 = borrowAmt.multiply(rate).multiply(monthPay02);
        monthPay1 = BigDecimalUtil.round13In45(monthPay1);
        BigDecimal monthPay2 = monthPay02.subtract(one);//(1+月利率)^借款月数-1
        monthPay2 = BigDecimalUtil.round13In45(monthPay2);
        BigDecimal monthPay = BigDecimalUtil.divide2(monthPay1,monthPay2);
        logger.debug("【月供/月本息】monthPay=" + monthPay);
        return monthPay;
    }

    private Result addNewBorrow2DB(Borrow borrow) {
        logger.debug("【向数据库添加新借款对象开始】borrow=" + borrow);
        Result result = Result.error();
        int count = borrowMapper.insert(borrow);
        if(count == 1){
            result = Result.success();
        }
        logger.debug("【向数据库添加新借款对象】count=" + count + ",result=" + result);
        return result;
    }

    public List<Borrow> getBorrowListByCustomerId(Integer customerId){
        logger.debug("【查询借款人借款列表：入参】customerId=" + customerId);
        Borrow borrow = new Borrow();
        borrow.setCustomerId(customerId);
        List<Borrow> borrowList = borrowMapper.select(borrow);
        logger.debug("【查询借款人借款列表：结果】borrowList=" + borrowList);
        return borrowList;
    }

    //补偿调用发送借款撮合
    //Result.target=借款撮合结果
    public Result compensateDealBorrowMatchReq(Integer borrowId){
        Result result = Result.error();
        logger.debug("【补偿调用发送借款撮合，入参】borrowId=" + borrowId);
        Borrow borrow = this.getBorrowByBorrowId(borrowId);
        if(BorrowBizStateEnum.NEW_ADD.getState().equals(borrow.getBizState())){//新增
             result =this.sendBorrowMatchAndSign(borrow);
        }else{
            logger.debug("【补偿调用发送借款撮合，借款业务状态非“新增”，不能发送借款撮合】borrowId=" + borrowId);
        }
        logger.debug("【补偿调用发送借款撮合，结果】result=" + result);
        return result;
    }

    private Borrow getBorrowByBorrowId(Integer borrowId) {
        logger.debug("【查询数据库获得借款，入参】borrowId=" + borrowId);
        Borrow borrow = borrowMapper.selectByPrimaryKey(borrowId);
        logger.debug("【查询数据库获得借款，结果】borrow=" + borrow);
        return borrow;
    }

    public BorrowContractVo getBorrowContractByBorrowId(Integer borrowId){
        logger.debug("【查询数据库获得借款协议】入参:borrowId=" + borrowId);
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        List<BorrowContractVo> borrowContractVoList = borrowContractVoMapper.selectBorrowContract(borrow);
        BorrowContractVo borrowContractVo = new BorrowContractVo();
        if(!borrowContractVoList.isEmpty()){
            borrowContractVo = borrowContractVoList.get(0);
        }
        logger.debug("【查询数据库获得借款协议】结果:borrowContractVo=" + borrowContractVo);
        return borrowContractVo;
    }

    //申请放款
    public Result applyLoan(Integer borrowId){
        logger.debug("【申请放款】入参borrowId=" + borrowId);
        Result result = Result.error();
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        Payment payment = this.buildAddLoadPayment(borrowId);
        result = paymentServer.loan(payment);
        logger.debug("【调用支付系统，借款人放款接口】result=" + result);
        return result;
    }

    //构建添加放款支付
    private Payment buildAddLoadPayment(Integer borrowId) {
        logger.debug("【构建添加放款支付开始】borrowId=" + borrowId);
        Borrow borrow = this.getBorrowByBorrowId(borrowId);
        Integer customerBankId = borrow.getCustomerBankId();
        Payment payment = new Payment();
        //银行信息
        payment.setBaseBankName(borrow.getBaseBankName());
        payment.setBankAccount(borrow.getBankAccount());
        payment.setBankCode(borrow.getBankCode());
        payment.setPhone(borrow.getPhone());
        //客户信息
        payment.setCustomerId(borrow.getCustomerId());
        payment.setCustomerName(borrow.getCustomerName());
        payment.setIdCard(borrow.getCustomerIdCard());
        //支付信息
        payment.setAmount(borrow.getBorrowAmt());
        payment.setBizId(String.valueOf(borrow.getId()));
        payment.setOrderSn(String.valueOf(borrow.getId()));
        payment.setSystemSource(SystemSourceEnum.BORROW.getName());
        payment.setType(PaymentTypeEnum.BORROW_LOAN.getCode());
        payment.setRemark(PaymentTypeEnum.BORROW_LOAN.getCodeDesc());
        BeanHelper.setAddDefaultField(payment);
        logger.debug("【构建添加放款支付】payment=" + payment);
        return payment;
    }

    //放款通知
    //验证是否已经更新过借款状态
    //根据通知结果更新借款业务状态。更新为借款成功，或借款失败
    public Result loanNotice(HashMap<String, String> loanMap){
        logger.debug("【放款，入参：loadMap=】" + loanMap);
        Result result = Result.error();
        String borrowId = loanMap.get("bizId");//借款编号
        String status = loanMap.get("status");
        Integer borrowIdInt = Integer.parseInt(borrowId);
        //1.验证是否已经更新过借款状态
        result = this.checkNoLoanNotice(borrowIdInt);
        if(Result.checkStatus(result)) {
            result = this.dealLoanNoticeData(borrowIdInt,status);
        }
        logger.debug("【放款，结果：result=】" + result);
        return result;
    }

    //放款成功：借款业务状态改为借款成功
    //放款失败：借款业务状态改为借款失败
    private Result dealLoanNoticeData(Integer borrowId, String status) {
        Result result = Result.error();
        if(status.equals(MQStatusEnum.OK.getStatus())){//放款成功
            result = this.updateBizStateBorrowSuc(borrowId);//借款成功
        }else if(status.equals(MQStatusEnum.FAIL.getStatus())){//放款失败
            result = this.updateBizStateBorrowFail(borrowId);//借款失败
            result = Result.success();
        }
        return result;
    }

    //更新借款状态借款失败
    private Result updateBizStateBorrowFail(Integer borrowId) {
        logger.debug("【更新借款业务状态为借款失败】borrowId=" + borrowId);
        Result result = this.updateBorrowBizState(borrowId,BorrowBizStateEnum.BORROW_FAIL);
        return result;
    }

    //更新借款状态借款成功
    private Result updateBizStateBorrowSuc(Integer borrowId) {
        logger.debug("【更新借款业务状态为借款成功】borrowId=" + borrowId);
        Result result = this.updateBorrowBizState(borrowId,BorrowBizStateEnum.BORROW_SUCCESS);
        return result;
    }

    private Result checkNoLoanNotice(Integer borrowId) {
        logger.debug("【验证是否已经更新过借款状态】入参borrowId=" + borrowId);
        Result result = Result.error();
        Borrow borrow = this.getBorrowByBorrowId(borrowId);
        //业务状态为已签约，则可以继续业务操作
        if(borrow != null && borrow.getBizState().equals(BorrowBizStateEnum.SIGNED.getState())){
            result = Result.success();
        }
        logger.debug("【检查是否已经更新过借款状态】result.status=error说明已经执行操作；" +
                "result.status=ok说明没有更新业务状态，继续执行。result=" + result);
        return result;
    }

}
