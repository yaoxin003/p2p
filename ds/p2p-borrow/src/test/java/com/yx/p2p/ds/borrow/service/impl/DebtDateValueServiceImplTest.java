package com.yx.p2p.ds.borrow.service.impl;

import com.yx.p2p.ds.borrow.mongo.borrow.DebtDailyValue;
import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.service.borrow.DebtDateValueService;
import com.yx.p2p.ds.service.util.p2p.P2PDateUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.PageUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.math.BigDecimal;
import java.util.*;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/19/15:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DebtDateValueServiceImplTest {

    @Autowired
    private DebtDateValueService debtDateValueService;

    @Test
    public void testAddBatchDebtDailyValue() throws Exception{
        String beginDate = DateUtil.datetimeMS2Str(new Date());
        //构建现金流
        List<Cashflow> cashflows = this.buildCashFlowList();

        Borrow borrow = new Borrow();
        borrow.setId(106);
        borrow.setBorrowAmt(new BigDecimal("100000"));

        debtDateValueService.addBatchDebtDateValue(borrow,cashflows);
        String endDate = DateUtil.datetimeMS2Str(new Date());
    }

    private List<Cashflow> buildCashFlowList() throws Exception{
        List<Cashflow> cashFlowList = new ArrayList<>();
        Cashflow cashflow0 = new Cashflow();
        cashflow0.setTradeDate(P2PDateUtil.str2Date("2020-05-17",null));
        cashflow0.setMonthPayment(new BigDecimal("100000"));
        cashFlowList.add(cashflow0);

        Cashflow cashFlow1 = new Cashflow();
        cashFlow1.setTradeDate(P2PDateUtil.str2Date("2020-06-15",null));
        cashFlow1.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow1);
        Cashflow cashFlow2 = new Cashflow();
        cashFlow2.setTradeDate(P2PDateUtil.str2Date("2020-07-15",null));
        cashFlow2.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow2);
        Cashflow cashFlow3 = new Cashflow();
        cashFlow3.setTradeDate(P2PDateUtil.str2Date("2020-08-15",null));
        cashFlow3.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow3);
        Cashflow cashFlow4 = new Cashflow();
        cashFlow4.setTradeDate(P2PDateUtil.str2Date("2020-09-15",null));
        cashFlow4.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow4);
        Cashflow cashFlow5 = new Cashflow();
        cashFlow5.setTradeDate(P2PDateUtil.str2Date("2020-10-15",null));
        cashFlow5.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow5);
        Cashflow cashFlow6 = new Cashflow();
        cashFlow6.setTradeDate(P2PDateUtil.str2Date("2020-11-15",null));
        cashFlow6.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow6);
        Cashflow cashFlow7 = new Cashflow();
        cashFlow7.setTradeDate(P2PDateUtil.str2Date("2020-12-15",null));
        cashFlow7.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow7);
        Cashflow cashFlow8 = new Cashflow();
        cashFlow8.setTradeDate(P2PDateUtil.str2Date("2021-01-15",null));
        cashFlow8.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow8);
        Cashflow cashFlow9 = new Cashflow();
        cashFlow9.setTradeDate(P2PDateUtil.str2Date("2021-02-15",null));
        cashFlow9.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow9);
        Cashflow cashFlow10 = new Cashflow();
        cashFlow10.setTradeDate(P2PDateUtil.str2Date("2021-03-15",null));
        cashFlow10.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow10);
        Cashflow cashFlow11 = new Cashflow();
        cashFlow11.setTradeDate(P2PDateUtil.str2Date("2021-04-15",null));
        cashFlow11.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow11);
        Cashflow cashFlow12 = new Cashflow();
        cashFlow12.setTradeDate(P2PDateUtil.str2Date("2021-05-15",null));
        cashFlow12.setMonthPayment(new BigDecimal("9145.17"));
        cashFlowList.add(cashFlow12);
        return cashFlowList;
    }

    @Test
    public void testQueryDebtDailyValueList() throws Exception{
        String beginDate = DateUtil.datetimeMS2Str(new Date());
        System.out.println("==============================");
        System.out.println("==============================");
        System.out.println("==============================");
        Date date = DateUtil.str2Date("2020-05-20");

       Integer total = debtDateValueService.queryDebtDateValuePageCount(date);
        System.out.println("【total=】" + total);

      Integer pageSize = PageUtil.getPageCount(total,SysConstant.DEBT_DAILY_VALUE_MONGO_PAGE_COUNT);
      System.out.println("【pageSize=】" + pageSize);

        for(int i=1; i<= pageSize; i++){
            System.out.println("【第" + i + "次查询】");
            debtDateValueService.queryDebtDateValuePageList(
                    date,i, SysConstant.DEBT_DAILY_VALUE_MONGO_PAGE_COUNT);
            System.out.println("------------------------------");
        }
    }

   private DebtDailyValue buildMongoDebtDailyValue(Date occurDate, BigDecimal npv) {
        DebtDailyValue debtDailyValue = new DebtDailyValue();
        //debtDailyValue.setId(ObjectId.get());
/*        debtDailyValue.setCreateTime(new Date());
        debtDailyValue.setDaily(occurDate);
        debtDailyValue.setBorrowId(100);
        debtDailyValue.setValue(npv.doubleValue());*/

        return debtDailyValue;
    }

    @Test
    public void testGetSumDebtAndReturnByBorrowIdList(){
        Date daily = DateUtil.str2Date("2020-06-16");
        List<Integer> borrowIdList = new ArrayList<>();
        borrowIdList.add(103);
        borrowIdList.add(105);
        Map<String, BigDecimal> sumDebtAndReturnByBorrowIdList =
                debtDateValueService.getSumDebtAndReturnByBorrowIdList(daily, borrowIdList);
        System.out.println(sumDebtAndReturnByBorrowIdList.get("sumValue"));
        System.out.println(sumDebtAndReturnByBorrowIdList.get("sumReturnAmt"));
    }


}
