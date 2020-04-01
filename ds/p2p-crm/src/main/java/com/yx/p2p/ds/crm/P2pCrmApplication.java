package com.yx.p2p.ds.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.yx.p2p.ds.crm.mapper")//tkmybatis的注解
@ComponentScan("com.yx.p2p.ds")
public class P2pCrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pCrmApplication.class, args);
	}

}
