package com.yx.p2p.ds.timer.biz.job;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.account.AccountRemarkEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.account.ClaimSubAccFlow;
import com.yx.p2p.ds.model.account.CurrentSubAccFlow;
import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.timer.biz.service.AccountJobService;
import com.yx.p2p.ds.timer.biz.service.InvestJobService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.OrderUtil;
import com.yx.p2p.ds.util.PageUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:定时任务：计算投资每日价值
 * 投资每日增值：
 * 投资回款：
 * @author: yx
 * @date: 2020/06/09/15:34
 */
@DisallowConcurrentExecution //作业不并发
@Component
public class InvestAddJob implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    static final Integer PAGE_SIZE = 500;

    @Autowired
    private AccountJobService accountJobService;

    @Autowired
    private InvestJobService investJobService;

    //1.插入投资回款明细和投资回款数据
    //2.投资回款记账：债权户减，活期户加
    //3.插入出借单并批量发送投资撮合请求
    //2或3失败，不影响1
    @Override
    public void execute(JobExecutionContext jobExecutionContext)  {
        Result result = Result.error();
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String bizDateStr = jobDataMap.getString("bizDate");
        logger.debug("【投资回款并发送撮合定时任务】开始bizDate=" + bizDateStr);
        Date bizDate = DateUtil.str2Date(bizDateStr);
        //插入投资回款明细和投资回款数据
        result = investJobService.dealInvestReturn(bizDate);
        //投资记账：投资增值记账，投资回款记账
        result = this.investArriveAccount(bizDate);
        //插入出借单并批量发送投资撮合请求
        result = investJobService.dealInvestReturnLending(bizDate);
        result = Result.success();
        logger.debug("【投资回款并发送撮合定时任务】结束");
    }

    //投资记账：投资增值记账，投资回款记账
    private Result investArriveAccount(Date bizDate) {
        Result result = Result.error();
        //投资增值记账：债权户加
        result = this.investAddAccount(bizDate);
        //投资回款记账：债权户减，活期户加
        result = this.investReturnArriveAccount(bizDate);
        result = Result.success();
        return result;
    }

    private Result investReturnArriveAccount(Date bizDate) {
        Result result = Result.error();
        Integer totalCount = investJobService.getInvestDebtValReturnAmtCount(bizDate);
        int pageCount = PageUtil.getPageCount(totalCount, PAGE_SIZE);
        for(int i=1; i<= pageCount; i++){
            List<InvestDebtVal> investDebtValPageList = investJobService.getInvestDebtValReturnAmtPageList(bizDate, i, PAGE_SIZE);
            List<ClaimSubAccFlow> claimFlowList = new ArrayList<>();
            List<CurrentSubAccFlow> currentFlowList = new ArrayList<>();
            this.buildInvestReturnAccout(investDebtValPageList,claimFlowList,currentFlowList);
            result = accountJobService.investReturnArriveAccount(claimFlowList,currentFlowList);
        }
        result = Result.success();
        return result;
    }

    //债权户减，活期户加
    private void buildInvestReturnAccout( List<InvestDebtVal> investDebtValPageList ,
            List<ClaimSubAccFlow> claimFlowList, List<CurrentSubAccFlow> currentFlowList) {
        BigDecimal zero = BigDecimal.ZERO;
        for (InvestDebtVal investDebtVal : investDebtValPageList) {
            BigDecimal amt = zero.subtract(investDebtVal.getTotalHoldReturnAmt());//负值
            String orderSn = OrderUtil.ORDERSN_PREFIX_INVEST_DEBT_VAL_RETURN + investDebtVal.getIdStr();
            //债权减
            ClaimSubAccFlow claimSubAccFlow = new ClaimSubAccFlow();
            claimSubAccFlow.setOrderSn(orderSn);
            claimSubAccFlow.setRemark(AccountRemarkEnum.INVEST_RETURN.getDesc());
            claimSubAccFlow.setCustomerId(investDebtVal.getInvestCustomerId());
            claimSubAccFlow.setBizId(investDebtVal.getInvestId().toString());
            claimSubAccFlow.setAmount(amt);
            BeanHelper.setAddDefaultField(claimSubAccFlow);
            claimFlowList.add(claimSubAccFlow);
            //活期加
            CurrentSubAccFlow currentSubAccFlow = new CurrentSubAccFlow();
            currentSubAccFlow.setOrderSn(orderSn);
            currentSubAccFlow.setRemark(AccountRemarkEnum.INVEST_RETURN.getDesc());
            currentSubAccFlow.setCustomerId(investDebtVal.getInvestCustomerId());
            currentSubAccFlow.setBizId(investDebtVal.getInvestId().toString());
            currentSubAccFlow.setAmount(investDebtVal.getTotalHoldReturnAmt());
            BeanHelper.setAddDefaultField(currentSubAccFlow);
            currentFlowList.add(currentSubAccFlow);
        }
    }

    //投资增值记账：债权户加
    private Result investAddAccount(Date bizDate) {
        Result result = Result.error();
        Integer investDebtValCount = investJobService.getInvestDebtValCount(bizDate);
        int pageCount = PageUtil.getPageCount(investDebtValCount,PAGE_SIZE);
        for(int i=1; i<=pageCount; i++){
            List<InvestDebtVal> investDebtValList = investJobService.getInvestDebtValPageList(bizDate,i,PAGE_SIZE);
            List<ClaimSubAccFlow> claimFlowList = this.buildInvestAddAccountList(investDebtValList);
            accountJobService.investAddAccount(claimFlowList);
        }
        result = Result.success();
        return result;
    }

    private List<ClaimSubAccFlow> buildInvestAddAccountList(List<InvestDebtVal> investDebtValList) {
        List<ClaimSubAccFlow> claimFlowList = new ArrayList<>();
        for (InvestDebtVal investDebtVal : investDebtValList) {
            ClaimSubAccFlow claimSubAccFlow = new ClaimSubAccFlow();
            claimSubAccFlow.setAmount(investDebtVal.getTotalHoldAddAmt());
            claimSubAccFlow.setBizId(investDebtVal.getInvestId().toString());
            claimSubAccFlow.setCustomerId(investDebtVal.getInvestCustomerId());
            claimSubAccFlow.setOrderSn(OrderUtil.ORDERSN_PREFIX_INVEST_DEBT_VAL_ADD + investDebtVal.getIdStr());
            claimSubAccFlow.setRemark(AccountRemarkEnum.INVEST_ADD.getDesc());
            BeanHelper.setAddDefaultField(claimSubAccFlow);
            claimFlowList.add(claimSubAccFlow);
        }
        return claimFlowList;
    }


}
