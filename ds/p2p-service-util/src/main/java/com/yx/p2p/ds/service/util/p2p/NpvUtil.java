package com.yx.p2p.ds.service.util.p2p;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/16/18:22
 */
public class NpvUtil {
    public static BigDecimal calNpv(Date now, List<CashFlowVo> cashFlowList) throws Exception{
        //生成日现金流
        List<CashFlowVo> dayCashFlowList = IrrUtil.genDayCashFlow(cashFlowList);
        //计算日IRR
        BigDecimal dayIrr = IrrUtil.calDayIrr(dayCashFlowList);
		/*log.debug("dayIrr="+dayIrr);
		log.debug("dayIrr.doubleValue()="+dayIrr.doubleValue());*/
        //获取子现金流
        int nowIndex = 0;
        for(CashFlowVo cfv : dayCashFlowList){
            if(P2PDateUtil.diff(cfv.getOccurDate(), now)<1){
                break;
            }
            nowIndex++;
        }
        List<CashFlowVo> subList = dayCashFlowList.subList(nowIndex, dayCashFlowList.size());
        double[] cashFlows = new double[subList.size()];
        for(int i=0; i<subList.size(); i++){
            CashFlowVo cfv = subList.get(i);
            cashFlows[i] = cfv.getAmount().doubleValue();
        }

        BigDecimal rtn = IrrUtil.round(NPresentValueAPI.pV(dayIrr.doubleValue(), cashFlows),IrrUtil.SCALE);
        return rtn;
    }
}
