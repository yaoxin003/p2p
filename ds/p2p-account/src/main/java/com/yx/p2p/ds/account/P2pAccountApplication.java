package com.yx.p2p.ds.account;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.yx.p2p.ds")
@MapperScan(basePackages = "com.yx.p2p.ds.account.mapper")//tkmybatis的注解
@EnableDubboConfiguration
public class P2pAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pAccountApplication.class, args);
	}

}
