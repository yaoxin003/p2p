package com.yx.p2p.ds.timer.biz.invest.job;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.timer.biz.invest.service.InvestReturnArriveJobService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * @description:定时任务：投资回款并发送撮合MQ和记账MQ
 * @author: yx
 * @date: 2020/06/09/15:34
 */
@DisallowConcurrentExecution //作业不并发
@Component
public class InvestReturnAndSendMatchJob implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestReturnArriveJobService investReturnArriveJobService;

    //1.插入投资回款明细和投资回款数据
    //2.插入出借单并批量发送投资撮合请求
    //2失败不影响1
    @Override
    public void execute(JobExecutionContext jobExecutionContext)  {
        Result result = Result.error();
        try{
            JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
            String bizDateStr = jobDataMap.getString("bizDate");
            logger.debug("【投资回款并发送撮合定时任务】开始bizDate=" + bizDateStr);
            Date bizDate = DateUtil.str2Date(bizDateStr);
            //插入投资回款明细和投资回款数据
            result = investReturnArriveJobService.dealInvestReturn(bizDate);
            //插入出借单并批量发送投资撮合请求
            result = investReturnArriveJobService.dealInvestReturnLending(bizDate);
            result = Result.success();
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        logger.debug("【投资回款并发送撮合定时任务】结束");
    }
}
