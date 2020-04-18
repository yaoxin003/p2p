package com.yx.p2p.ds;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.yx.p2p.ds.payment.P2pPaymentApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/12/17:11
 */
@SpringBootApplication
@MapperScan(basePackages = "com.yx.p2p.ds.payment.mapper")//tkmybatis的注解
@ComponentScan("com.yx.p2p.ds")
@EnableDubboConfiguration
public class PaymentAndMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(P2pPaymentApplication.class, args);
    }
}
