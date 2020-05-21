package com.yx.p2p.ds.service.util.p2p;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/16/18:26
 */
public class IrrUtil {

    public static final int SCALE = 20;//精度

    //生成日现金流
    public static List<CashFlowVo> genDayCashFlow(List<CashFlowVo> cashFlowList) throws Exception {
        //按日期排序
        Collections.sort(cashFlowList, new Comparator<CashFlowVo>() {
            public int compare(CashFlowVo arg0, CashFlowVo arg1) {
                return arg0.getOccurDate().compareTo(arg1.getOccurDate());
            }
        });
        //准备时间段
        Date startDate = cashFlowList.get(0).getOccurDate();
        Date endDate = cashFlowList.get(cashFlowList.size()-1).getOccurDate();
        final int days = P2PDateUtil.diff(startDate, endDate)+1;

		/*if(cashFlowList.size()==days){//已经是日现金流，则直接返回
			return cashFlowList;
		}*/

        //生成按日的现金流（无发生额时补0）
        List<CashFlowVo> dayCashFlowList = new ArrayList<CashFlowVo>(days);
        Map<Date,CashFlowVo> cfvMap = new HashMap<Date,CashFlowVo>();
        for(CashFlowVo cfv : cashFlowList){
            cfvMap.put(cfv.getOccurDate(), cfv);
        }
        for(int i=0; i<days; i++){
            Date date = P2PDateUtil.addDays(startDate, i);
            CashFlowVo cfv = null;
            if(cfvMap.containsKey(date)){//插入发生额
                cfv = cfvMap.get(date);
            }else{//不存在发生额，则补0
                cfv = new CashFlowVo();
                cfv.setOccurDate(date);
                cfv.setAmount(BigDecimal.ZERO);
            }
            dayCashFlowList.add(cfv);
        }
        return dayCashFlowList;
    }

    // 计算日irr
    public static BigDecimal calDayIrr(List<CashFlowVo> cashFlowList) throws Exception{
        List<CashFlowVo> dayCashFlowList = genDayCashFlow(cashFlowList);

        double[] inputCashFlow = new double[dayCashFlowList.size()];
        for(int i=0; i<dayCashFlowList.size(); i++){
            CashFlowVo cfv = dayCashFlowList.get(i);
            inputCashFlow[i] = cfv.getAmount().doubleValue();
        }

        BigDecimal rtn = round(IRRapi.getDayIRR(Double.NaN, inputCashFlow),SCALE);
        return rtn;
    }

    public static BigDecimal round(double src, int scale){
        BigDecimal rtn = new BigDecimal(
                new Double(
                        Arith.round(src, scale)
                ).toString()
        );
        return rtn;
    }

}
