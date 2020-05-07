package com.yx.p2p.ds.service.init;

import com.alibaba.dubbo.qos.server.Server;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @description: 解决Dubbo BUG
 * ERROR [com.alibaba.dubbo.qos.server.Server:102] -  [DUBBO] qos-server can not bind localhost:22222,
 * dubbo version: 2.6.0, current host: 127.0.0.1
java.net.BindException: Address already in use: bind
参考内容：springboot+dubbo2.6.0关闭QOS服务
https://blog.csdn.net/xdkprosperous/article/details/99583204
qos是远程连接dubbo服务，上线下线dubbo服务
 * @author: yx
 * @date: 2020/04/23/16:59
 */
@Component
public class DubboQosInit implements ApplicationRunner {

    public void run(ApplicationArguments args) throws Exception {
        //关闭QOS服务
        Server.getInstance().stop();
    }
}
