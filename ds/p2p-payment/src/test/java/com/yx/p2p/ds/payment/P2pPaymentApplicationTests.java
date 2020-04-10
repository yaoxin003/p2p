package com.yx.p2p.ds.payment;

import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.CustomerBank;
import com.yx.p2p.ds.vo.CustomerVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pPaymentApplication.class)
public class P2pPaymentApplicationTests {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void contextLoads() {
		CustomerBank customerBank = new CustomerBank();
		CustomerVo customerVo = new CustomerVo();
		//设置时间和操作人
		BeanHelper.setDefaultTimeField(customerBank,"createTime","updateTime");
		BeanHelper.setDefaultTimeField(customerVo,"createTime","updateTime");
		System.out.println("========================================" + customerBank);
		System.out.println("========================================" + customerVo);

	}

}
