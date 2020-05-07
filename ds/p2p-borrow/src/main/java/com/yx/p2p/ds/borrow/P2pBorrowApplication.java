package com.yx.p2p.ds.borrow;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.yx.p2p.ds")
@MapperScan(basePackages = "com.yx.p2p.ds.borrow.mapper")//tkmybatis的注解
@EnableDubboConfiguration
public class P2pBorrowApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pBorrowApplication.class, args);
	}

}
