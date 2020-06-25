package com.yx.p2p.ds.invest.com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.invest.P2pInvestApplication;
import com.yx.p2p.ds.invest.mapper.InvestMapper;
import com.yx.p2p.ds.invest.mapper.InvestProductMapper;
import com.yx.p2p.ds.model.invest.InvestProduct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/25/15:54
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InvestMapperTest {

    @Autowired
    private InvestMapper investMapper;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testQuerySumInvestAmt(){
        Integer customerId = 1;
        BigDecimal sum = investMapper.querySumInvestAmt(2);
        logger.debug("【sum=】" + sum);
    }
}
