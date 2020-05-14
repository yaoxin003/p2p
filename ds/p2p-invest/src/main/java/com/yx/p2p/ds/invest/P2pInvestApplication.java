package com.yx.p2p.ds.invest;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.yx.p2p.ds")
@MapperScan(basePackages = "com.yx.p2p.ds.invest.mapper")//tkmybatis的注解
@EnableDubboConfiguration
@EnableTransactionManagement//开启事务管理
public class P2pInvestApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pInvestApplication.class, args);
	}

}
