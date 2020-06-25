package com.yx.p2p.ds.timer.biz.job;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.account.AccountRemarkEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.account.CurrentSubAccFlow;
import com.yx.p2p.ds.model.account.DebtSubAccFlow;
import com.yx.p2p.ds.model.borrow.DebtDateValue;
import com.yx.p2p.ds.timer.biz.service.AccountJobService;
import com.yx.p2p.ds.timer.biz.service.BorrowJobService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.OrderUtil;
import com.yx.p2p.ds.util.PageUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:定时任务：计算债务每日价值Timer
 * 债务每日增值：
 * 债务还款到账：
 * @author: yx
 * @date: 2020/06/15/15:11
 */
@DisallowConcurrentExecution //作业不并发
@Component
public class DebtAddJob implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    static final int PAGE_SIZE = 500;

    @Autowired
    private BorrowJobService borrowJobService;

    @Autowired
    private AccountJobService accountJobService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String bizDateStr = jobDataMap.getString("bizDate");
        logger.debug("【债务增值定时任务】开始bizDate=" + bizDateStr);
        Date bizDate = DateUtil.str2Date(bizDateStr);
        Integer totalCount = borrowJobService.getDebtDateValueCount(bizDate);
        logger.debug("【查询债务每日价值数量】totalCount={}",totalCount);
        int pageCount = PageUtil.getPageCount(totalCount,PAGE_SIZE);
        for(int i=1; i<=pageCount; i++){
            List<DebtDateValue> debtDateValueList = borrowJobService.getDebtDateValuePageList(bizDate, i, PAGE_SIZE);
            logger.debug("【分页查询债务每日价值】debtDateValueList={}",debtDateValueList);
            //债务增值
            this.debtAddAccount(debtDateValueList);
            //债务还款到账(只有还款到账日才会有数据，需要根据还款金额判断)
            this.debtReturnArriveAccount(debtDateValueList);
        }
    }

    //债务增值
    private Result debtAddAccount(List<DebtDateValue> debtDateValueList) {
        logger.debug("【账户处理：债务增值】");
        List<DebtSubAccFlow> debtAddFlowList = this.buildDebtAddFlowList(debtDateValueList);
        return accountJobService.debtAddAccount(debtAddFlowList);
    }

    //债务还款到账(只有还款到账日才会有数据，需要根据还款金额判断)
    private Result debtReturnArriveAccount(List<DebtDateValue> debtDateValueList) {
        logger.debug("【账户处理：债务还款到账】");
        Result result = Result.error();
        List<DebtSubAccFlow> debtArrFlowList =  new ArrayList<>();
        List<CurrentSubAccFlow> currentFlowList = new ArrayList<>();
        result = this.buildDebtArrFlowList(debtDateValueList,debtArrFlowList,currentFlowList);
        if(!debtArrFlowList.isEmpty() && !currentFlowList.isEmpty()){
            return accountJobService.debtReturnArriveAccount(debtArrFlowList);
        }else{
            logger.debug("【当日没有还款】");
        }
        return result;
    }

    //构建债务还款到账集合
    private Result buildDebtArrFlowList(List<DebtDateValue> debtDateValueList,
                            List<DebtSubAccFlow> debtDebtArrFlowList ,List<CurrentSubAccFlow> currentFlowList) {
        Result result = Result.error();
        for (DebtDateValue debtDateValue : debtDateValueList) {
            DebtSubAccFlow debtSubAccFlow = this.buildDebtArrFlow(debtDateValue);
            if(debtSubAccFlow != null){
                debtDebtArrFlowList.add(debtSubAccFlow);
            }
            CurrentSubAccFlow currentSubAccFlow = this.buildCurrentSubAccFlow(debtDateValue);
            if(currentSubAccFlow != null){
                currentFlowList.add(currentSubAccFlow);
            }
        }
        result = Result.success();
        return result;
    }

    private CurrentSubAccFlow buildCurrentSubAccFlow(DebtDateValue debtDateValue) {
        BigDecimal zero = BigDecimal.ZERO;
        CurrentSubAccFlow currentSubAccFlow = null;
        if(debtDateValue.getReturnAmt().compareTo(zero) > 0){
            currentSubAccFlow = new CurrentSubAccFlow();
            currentSubAccFlow.setBizId(debtDateValue.getBorrowId().toString());
            currentSubAccFlow.setAmount(debtDateValue.getReturnAmt());
            currentSubAccFlow.setOrderSn(OrderUtil.ORDERSN_PREFIX_DEBTDATEVALUE_RETURN_ARR + debtDateValue.getIdStr());
            currentSubAccFlow.setCustomerId(debtDateValue.getCustomerId());
            currentSubAccFlow.setRemark(AccountRemarkEnum.DEBT_RETURN_ARRIVE.getDesc());
            BeanHelper.setAddDefaultField(currentSubAccFlow);
        }
        return currentSubAccFlow;
    }

    //构建债务还款到账
    private DebtSubAccFlow buildDebtArrFlow(DebtDateValue debtDateValue) {
        BigDecimal zero = BigDecimal.ZERO;
        DebtSubAccFlow debtSubAccFlow = null;
        if(debtDateValue.getReturnAmt().compareTo(zero) > 0){
            debtSubAccFlow = new DebtSubAccFlow();
            debtSubAccFlow.setBizId(debtDateValue.getBorrowId().toString());
            debtSubAccFlow.setAmount(zero.subtract(debtDateValue.getReturnAmt()));
            debtSubAccFlow.setOrderSn(OrderUtil.ORDERSN_PREFIX_DEBTDATEVALUE_RETURN_ARR + debtDateValue.getIdStr());
            debtSubAccFlow.setCustomerId(debtDateValue.getCustomerId());
            debtSubAccFlow.setRemark(AccountRemarkEnum.DEBT_RETURN_ARRIVE.getDesc());
            BeanHelper.setAddDefaultField(debtSubAccFlow);
        }
        return debtSubAccFlow;
    }

    //构建债务增值集合
    private List<DebtSubAccFlow> buildDebtAddFlowList(List<DebtDateValue> debtDateValueList) {
        List<DebtSubAccFlow> debtSubAccFlowList = new ArrayList<>();
        for (DebtDateValue debtDateValue : debtDateValueList) {
            DebtSubAccFlow debtSubAccFlow = this.buildDebtAddFlow(debtDateValue);
            debtSubAccFlowList.add(debtSubAccFlow);
        }
        return debtSubAccFlowList;
    }

    //构建债务增值
    private DebtSubAccFlow buildDebtAddFlow(DebtDateValue debtDateValue) {
        DebtSubAccFlow debtSubAccFlow = new DebtSubAccFlow();
        debtSubAccFlow.setBizId(debtDateValue.getBorrowId().toString());
        debtSubAccFlow.setAmount(debtDateValue.getAddAmt());
        debtSubAccFlow.setOrderSn(OrderUtil.ORDERSN_PREFIX_DEBTDATEVALUE_DEBT_ADD + debtDateValue.getIdStr());
        debtSubAccFlow.setCustomerId(debtDateValue.getCustomerId());
        debtSubAccFlow.setRemark(AccountRemarkEnum.DEBT_ADD.getDesc());
        BeanHelper.setAddDefaultField(debtSubAccFlow);
        return debtSubAccFlow;
    }
}
