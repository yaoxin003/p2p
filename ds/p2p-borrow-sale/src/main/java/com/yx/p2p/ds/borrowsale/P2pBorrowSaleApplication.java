package com.yx.p2p.ds.borrowsale;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.yx.p2p.ds")
@EnableDubboConfiguration
public class P2pBorrowSaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pBorrowSaleApplication.class, args);
	}

}
