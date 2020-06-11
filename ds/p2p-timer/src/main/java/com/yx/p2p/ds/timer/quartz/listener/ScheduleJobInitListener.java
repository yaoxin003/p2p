package com.yx.p2p.ds.timer.quartz.listener;

import com.yx.p2p.ds.timer.quartz.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/08/9:42
 */
@Component
@Order(value = 1)
public class ScheduleJobInitListener implements CommandLineRunner {

    @Autowired
    TaskService taskService;

    @Override
    public void run(String... arg0) throws Exception {
        try {
            taskService.initSchedule();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
