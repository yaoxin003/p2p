package com.yx.p2p.ds.payment;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.yx.p2p.ds.payment.mapper")//tkmybatis的注解
@ComponentScan("com.yx.p2p.ds")
@EnableDubboConfiguration
public class P2pPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pPaymentApplication.class, args);
	}

}
