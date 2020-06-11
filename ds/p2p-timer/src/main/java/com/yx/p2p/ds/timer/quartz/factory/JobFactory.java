package com.yx.p2p.ds.timer.quartz.factory;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class JobFactory extends AdaptableJobFactory {

    //这个对象Spring会帮我们自动注入进来,也属于Spring技术范畴.
    //Job对象的实例化过程是在Quartz中进行的，这时候将spring的东西注入进来，肯定是行不通的，所以需要这个类
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //调用父类的方法
        Object jobInstance = super.createJobInstance(bundle);
        //进行注入
        capableBeanFactory.autowireBean(jobInstance);

        return jobInstance;
    }
}
