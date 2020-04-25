package com.yx.p2p.ds.match;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.yx.p2p.ds")
public class P2pMatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2pMatchApplication.class, args);
	}

}
