package com.yx.p2p.ds.investsale;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.yx.p2p.ds")
@EnableDubboConfiguration
public class P2pInvestSaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pInvestSaleApplication.class, args);
	}

}
