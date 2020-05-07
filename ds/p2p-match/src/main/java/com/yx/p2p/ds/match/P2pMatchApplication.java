package com.yx.p2p.ds.match;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.yx.p2p.ds")
@MapperScan("com.yx.p2p.ds.match.mapper")
@EnableDubboConfiguration
public class P2pMatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pMatchApplication.class, args);
	}

}
