package com.yx.p2p.ds.match;

import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.service.match.BorrowMatchReqService;
import com.yx.p2p.ds.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pMatchApplication.class)
public class P2pMatchApplicationTests {

	@Autowired
	private BorrowMatchReqService borrowMatchReqService;

	@Test
	public void testSelectWaitMatchAmtInvestReqList(){
		Class<?>[] methodParamClass =  new Class[1];
		methodParamClass[0] = FinanceMatchReq.class;

		Object[] methodParamObj = new Object[]{new FinanceMatchReq()};

		TestUtil.invokePrivateMethodParms(borrowMatchReqService,
				"getWaitMatchAmtInvestReqList",
				methodParamClass,methodParamObj);
	}

	@Test
	public void testRemainAmt(){
        BigDecimal remainAmt = new BigDecimal("1");

        remainAmt = this.calRemain(remainAmt);
        System.out.println(remainAmt);
    }

    private BigDecimal calRemain(BigDecimal remainAmt) {
	    return remainAmt = remainAmt.subtract(new BigDecimal("0.2"));
    }

}
