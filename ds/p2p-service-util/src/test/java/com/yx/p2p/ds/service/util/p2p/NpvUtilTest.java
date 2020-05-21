package com.yx.p2p.ds.service.util.p2p;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/17/13:59
 */
public class NpvUtilTest {

    //测试计算npv
    public static void main(String[] args) {
        try{
            List<CashFlowVo> cashFlowVoList = new ArrayList<CashFlowVo> ();
            CashFlowVo cashFlowVo0 = new CashFlowVo();
            cashFlowVo0.setOccurDate(P2PDateUtil.str2Date("2020-05-17",null));
            cashFlowVo0.setAmount(new BigDecimal("-100000"));
            cashFlowVoList.add(cashFlowVo0);

            CashFlowVo cashFlowVo1 = new CashFlowVo();
            cashFlowVo1.setOccurDate(P2PDateUtil.str2Date("2020-06-15",null));
            cashFlowVo1.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo1);
            CashFlowVo cashFlowVo2 = new CashFlowVo();
            cashFlowVo2.setOccurDate(P2PDateUtil.str2Date("2020-07-15",null));
            cashFlowVo2.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo2);
            CashFlowVo cashFlowVo3 = new CashFlowVo();
            cashFlowVo3.setOccurDate(P2PDateUtil.str2Date("2020-08-15",null));
            cashFlowVo3.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo3);
            CashFlowVo cashFlowVo4 = new CashFlowVo();
            cashFlowVo4.setOccurDate(P2PDateUtil.str2Date("2020-09-15",null));
            cashFlowVo4.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo4);
            CashFlowVo cashFlowVo5 = new CashFlowVo();
            cashFlowVo5.setOccurDate(P2PDateUtil.str2Date("2020-10-15",null));
            cashFlowVo5.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo5);
            CashFlowVo cashFlowVo6 = new CashFlowVo();
            cashFlowVo6.setOccurDate(P2PDateUtil.str2Date("2020-11-15",null));
            cashFlowVo6.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo6);
            CashFlowVo cashFlowVo7 = new CashFlowVo();
            cashFlowVo7.setOccurDate(P2PDateUtil.str2Date("2020-12-15",null));
            cashFlowVo7.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo7);
            CashFlowVo cashFlowVo8 = new CashFlowVo();
            cashFlowVo8.setOccurDate(P2PDateUtil.str2Date("2021-01-15",null));
            cashFlowVo8.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo8);
            CashFlowVo cashFlowVo9 = new CashFlowVo();
            cashFlowVo9.setOccurDate(P2PDateUtil.str2Date("2021-02-15",null));
            cashFlowVo9.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo9);
            CashFlowVo cashFlowVo10 = new CashFlowVo();
            cashFlowVo10.setOccurDate(P2PDateUtil.str2Date("2021-03-15",null));
            cashFlowVo10.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo10);
            CashFlowVo cashFlowVo11 = new CashFlowVo();
            cashFlowVo11.setOccurDate(P2PDateUtil.str2Date("2021-04-15",null));
            cashFlowVo11.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo11);
            CashFlowVo cashFlowVo12 = new CashFlowVo();
            cashFlowVo12.setOccurDate(P2PDateUtil.str2Date("2021-05-15",null));
            cashFlowVo12.setAmount(new BigDecimal("9145.17"));
            cashFlowVoList.add(cashFlowVo12);

            int count = P2PDateUtil.diff(cashFlowVo0.getOccurDate(),cashFlowVo12.getOccurDate());

            Date occurDate = cashFlowVo0.getOccurDate();
           for(int j=0;j <= count+1; j++){
               BigDecimal npv = NpvUtil.calNpv(occurDate, cashFlowVoList);
               System.out.println( P2PDateUtil.date2Str(occurDate,null) + "=====" +
                       npv.setScale(2, BigDecimal.ROUND_HALF_UP));
               occurDate = P2PDateUtil.addDays(occurDate,1);
           }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
