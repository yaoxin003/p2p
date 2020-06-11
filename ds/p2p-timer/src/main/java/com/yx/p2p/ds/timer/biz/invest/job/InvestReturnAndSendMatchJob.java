package com.yx.p2p.ds.timer.biz.invest.job;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.service.InvestReturnArriveService;
import com.yx.p2p.ds.service.ReturnLendingService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

/**
 * @description:投资回款并发送撮合定时任务
 * @author: yx
 * @date: 2020/06/09/15:34
 */
public class InvestReturnAndSendMatchJob implements Job {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestReturnArriveService investReturnArriveService;
    @Autowired
    private ReturnLendingService returnLendingService;

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
            investReturnArriveService.dealInvestReturn(bizDate);
            returnLendingService.dealInvestReturn(bizDate);
            result = Result.success();
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        logger.debug("【投资回款并发送撮合定时任务】结束");
    }
}
