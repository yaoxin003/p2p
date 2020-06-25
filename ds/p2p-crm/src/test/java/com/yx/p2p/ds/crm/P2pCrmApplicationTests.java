package com.yx.p2p.ds.crm;

import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.service.crm.CrmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class P2pCrmApplicationTests {

	@Autowired
	private CrmService crmService;

	@Test
	public void testGetCustomerByIdInDB() {
		Customer crmByIdInDB = crmService.getCustomerByIdInDB(38);
	}



}
