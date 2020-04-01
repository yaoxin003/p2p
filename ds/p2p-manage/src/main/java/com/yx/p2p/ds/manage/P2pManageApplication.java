package com.yx.p2p.ds.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//@MapperScan(basePackages = "com.yx.p2p.ds.crm.mapper")//tkmybatis的注解
@ComponentScan("com.yx.p2p.ds")
public class P2pManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pManageApplication.class, args);
	}

}
