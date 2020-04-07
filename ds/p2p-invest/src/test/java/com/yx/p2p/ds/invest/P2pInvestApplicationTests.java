package com.yx.p2p.ds.invest;

import com.yx.p2p.ds.service.InvestProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pInvestApplication.class)
public class P2pInvestApplicationTests {

	@Autowired
	private InvestProductService investProductService;

	@Test
	public void contextLoads() {
		investProductService.getAllInvestProductJSON();
	}

}
