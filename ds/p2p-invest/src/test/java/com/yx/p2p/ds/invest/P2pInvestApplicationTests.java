package com.yx.p2p.ds.invest;

import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestProduct;
import com.yx.p2p.ds.service.invest.InvestProductService;
import com.yx.p2p.ds.service.invest.InvestService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pInvestApplication.class)
public class P2pInvestApplicationTests {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private InvestProductService investProductService;

	@Autowired
	private InvestService investService;

	@Test
	public void contextLoads() {
		investProductService.getAllInvestProductJSON();
	}

	@Test
	public void testAddDate(){
		Date newD1 = DateUtil.addDay(DateUtil.str2Date("2020-2-28"), 1);
		logger.debug("【date.add】" + newD1);
		Date newD2 = DateUtil.addDay(DateUtil.str2Date("2020-12-30"), 1);
		logger.debug("【date.add】" + newD2);
		Date newD3 = DateUtil.addDay(DateUtil.str2Date("2020-12-31"), 1);
		logger.debug("【date.add】" + newD3);
	}

	@Test
	public void testBuildNewInvest(){
		Invest invest = new Invest();
		invest.setInvestProductId(1);
		invokePrivateMethod(investService,"buildNewInvest",invest);
	}

	private  static <T,R> void invokePrivateMethod(T target,String methodName,R methodParam){
		// 获取class
		Class clazz = target.getClass();
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(methodName, methodParam.getClass());
			method.setAccessible(true);
			method.invoke(target,methodParam);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBuildProfit(){
		Class<?>[] methodParamClass =  new Class[2];
		methodParamClass[0] = InvestProduct.class;
		methodParamClass[1] = Invest.class;

		InvestProduct investProduct = investProductService.getInvestProductById(1);
		Invest invest = new Invest();
		invest.setInvestProductId(1);
		invest.setInvestAmt(new BigDecimal("2000"));

		Object[] methodParamObj = new Object[]{investProduct, invest};

		TestUtil.invokePrivateMethodParms(investService,"buildProfit",methodParamClass,methodParamObj);

		logger.debug("【invest=】" + invest);
	}


}
