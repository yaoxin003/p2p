package com.yx.p2p.ds.crm;

import com.alibaba.dubbo.qos.common.Constants;
import com.alibaba.dubbo.qos.server.Server;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.yx.p2p.ds.crm.mapper")//tkmybatis的注解
@ComponentScan("com.yx.p2p.ds")
@EnableDubboConfiguration
public class P2pCrmApplication {

	public static void main(String[] args) {

		SpringApplication.run(P2pCrmApplication.class, args);
	}

}
