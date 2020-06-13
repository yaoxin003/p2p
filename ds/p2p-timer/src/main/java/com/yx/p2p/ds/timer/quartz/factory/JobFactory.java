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
    /**
     * AutowireCapableBeanFactory接口是BeanFactory的子类
     * 可以连接和填充那些生命周期不被Spring管理的已存在的bean实例
     * 具体请参考：http://blog.csdn.net/iycynna_123/article/details/52993542
     */
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;



    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //首先，调用父类的方法创建好Quartz所需的Job实例
        Object jobInstance = super.createJobInstance(bundle);
        //然后，使用BeanFactory为创建好的Job实例进行属性自动装配并将其纳入到Spring容器的管理之中，属于Spring的技术范畴.
        capableBeanFactory.autowireBean(jobInstance);

        return jobInstance;
    }
}
