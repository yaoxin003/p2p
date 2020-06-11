package com.yx.p2p.ds.borrow.service.impl;

import com.yx.p2p.ds.borrow.mapper.DebtDateValueMapper;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.model.borrow.DebtDateValue;
import com.yx.p2p.ds.service.DebtDateValueService;
import com.yx.p2p.ds.service.util.p2p.CashFlowVo;
import com.yx.p2p.ds.service.util.p2p.NpvUtil;
import com.yx.p2p.ds.service.util.p2p.P2PDateUtil;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description:使用Mycat保存DebtDateValue数据
 * @author: yx
 * @date: 2020/05/30/15:29
 */
@Service
public class DebtDateValueServiceImpl implements DebtDateValueService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DebtDateValueMapper debtDateValueMapper;

    public Result addBatchDebtDateValue(Borrow borrow, List<Cashflow> cashflows){
        logger.debug("【批量插入债务每日价值】入参：cashflows=" + cashflows);
        Result result = Result.error();
        try{
            List<DebtDateValue> debtDailyValueList = this.buildDebtDateValueList(borrow,cashflows);
            debtDateValueMapper.insertList(debtDailyValueList);
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        result = Result.success();
        return result;
    }

    private List<DebtDateValue> buildDebtDateValueList(Borrow borrow,List<Cashflow> cashflows) {
        List<CashFlowVo> cashFlowVoList = this.buildCashFlowVoList(cashflows);
        List<DebtDateValue> debtDateValueList = new ArrayList<>();
        try {
            Date beginOccurDate = cashFlowVoList.get(0).getOccurDate();
            int count = P2PDateUtil.diff(beginOccurDate,
                    cashFlowVoList.get(cashFlowVoList.size()-1).getOccurDate());
            Date occurDate = beginOccurDate;
            logger.debug("【批量插入Vo债务每日价值】入参：cashFlowVoList=" + cashFlowVoList);
            Map<Date,BigDecimal> cashflowMap = this.buildCashFlowMap(cashflows);
            BigDecimal npv = BigDecimal.ZERO;
            for(int j=0;j <= count+1; j++){
                if(beginOccurDate.compareTo(occurDate) == 0){
                    npv = borrow.getBorrowAmt();
                }else{
                    npv = NpvUtil.calNpv(occurDate, cashFlowVoList);
                }
                DebtDateValue debtDateValue =
                        this.buildDebtDateValue(cashflowMap,borrow,occurDate,
                        BigDecimalUtil.round2In45(npv));
                occurDate = P2PDateUtil.addDays(occurDate,1);
                debtDateValueList.add(debtDateValue);
            }
            logger.debug("【批量插入Vo债务每日价值】结果：debtDateValueList=" + debtDateValueList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return debtDateValueList;
    }

    private Map<Date,BigDecimal> buildCashFlowMap(List<Cashflow> cashflows) {
        Map<Date,BigDecimal> cashflowMap = new HashMap<>();
        for (Cashflow cashflow : cashflows) {
            if(cashflow.getReturnDateNo() != 0){//还款计划
                cashflowMap.put(DateUtil.addDay(cashflow.getTradeDate(),1),cashflow.getMonthPayment());
            }
        }
        logger.debug("【构建带还款金额的还款计划】cashflowMap=" + cashflowMap);
        return cashflowMap;
    }

    private List<CashFlowVo> buildCashFlowVoList(List<Cashflow> cashflows) {
        List<CashFlowVo> cashFlowVoList = new ArrayList<>();
        int i = 1;
        for (Cashflow cashflow : cashflows) {
            CashFlowVo cashFlowVo = new CashFlowVo();
            cashFlowVo.setOccurDate(cashflow.getTradeDate());
            BigDecimal monthPayment = cashflow.getMonthPayment();
            if(i == 1){
                cashflow.setReturnDateNo(i-1);
                cashFlowVo.setAmount(BigDecimal.ZERO.subtract(monthPayment));//首期为借款金额(负值)，在计算npv时需要一个负值
            }else{
                cashflow.setReturnDateNo(i-1);
                cashFlowVo.setAmount(monthPayment);//月供，每期还款金额，正值
            }
            cashFlowVoList.add(cashFlowVo);
            i++;
        }
        logger.debug("【构建计算npv现金流】cashFlowVoList=" + cashFlowVoList);
        return cashFlowVoList;
    }

    private DebtDateValue buildDebtDateValue(Map<Date,BigDecimal> cashflowMap,
           Borrow borrow,Date occurDate, BigDecimal npv) {
        DebtDateValue debtDateValue = new DebtDateValue();
        debtDateValue.setIdStr("next value for MYCATSEQ_P2P_DEBT_DAILY_VALUE");
        debtDateValue.setDaily(occurDate);
        debtDateValue.setBorrowId(borrow.getId());
        debtDateValue.setValue(npv);
        if(cashflowMap.containsKey(occurDate)){
            debtDateValue.setReturnAmt(cashflowMap.get(occurDate));
        }else{
            debtDateValue.setReturnAmt(BigDecimal.ZERO);
        }
        BeanHelper.setAddDefaultField(debtDateValue);
        return debtDateValue;
    }

    /**
     * @description: 分页查询某日债务价值集合
     * @author:  YX
     * @date:    2020/05/20 10:27
     * @param: daily 日期(年月日)
     * @param: page 第几页，首页编号1
     * @param: rows 每页记录数
     * @return: java.util.List<com.yx.p2p.ds.mongo.borrow.DebtDailyValue>
     */
    public List<DebtDateValue> queryDebtDateValuePageList(Date daily, Integer page, Integer rows){
        logger.debug("【分页查询债务某日价值】入参daily=" + daily + ",page=" + page + ",rows=" + rows);
        DebtDateValue param = new DebtDateValue();
        param.setDaily(daily);
        int offset = (page - 1) * rows;
        List<DebtDateValue> debtDateValueList = debtDateValueMapper.selectByRowBounds(
                param, new RowBounds(offset, rows));
        logger.debug("【分页查询债务某日价值】debtDateValueList=" + debtDateValueList);
        return debtDateValueList;
    }

    //查询债务某日价值数量
    public Integer queryDebtDateValuePageCount(Date daily){
        logger.debug("【查询债务某日价值数量】入参daily=" + daily);
        DebtDateValue param = new DebtDateValue();
        param.setDaily(daily);
        int count = debtDateValueMapper.selectCount(param);
        logger.debug("【查询债务某日价值数量】count=" + count);
        return (int)count;
    }

    //获得债权和还款价值总和
    @Override
    public Map<String,BigDecimal> getSumDebtAndReturnByBorrowIdList(Date daily,List<Integer> borrowIdList) {
        Map<String, BigDecimal> debtAndReturnMap =
                debtDateValueMapper.querySumDebtAndReturnByBorrowIdList(daily,borrowIdList);
        return debtAndReturnMap;
    }
}
