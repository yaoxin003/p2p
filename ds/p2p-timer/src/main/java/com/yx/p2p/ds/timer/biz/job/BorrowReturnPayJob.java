package com.yx.p2p.ds.timer.biz.job;

import com.yx.p2p.ds.timer.biz.service.CashflowJobService;
import com.yx.p2p.ds.util.DateUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @description:定时任务：借款人还款支付
 * @author: yx
 * @date: 2020/06/27/19:24
 */
@DisallowConcurrentExecution //作业不并发
@Component
public class BorrowReturnPayJob implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CashflowJobService cashflowJobService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String bizDateStr = jobDataMap.getString("bizDate");
        logger.debug("【借款人还款支付定时任务】开始bizDate=" + bizDateStr);
        Date bizDate = DateUtil.str2Date(bizDateStr);

        cashflowJobService.borrowReturnPayment(bizDate);
    }
}
