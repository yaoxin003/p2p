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

/**
 * @description:
 * @author: yx
 * @date: 2020/04/25/15:54
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pInvestApplication.class)
public class InvestProductMapperTest {

    @Autowired
    private InvestProductMapper investProductMapper;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testSelectInvestProductByInvestId(){
        InvestProduct investProduct = investProductMapper.selectInvestProductByInvestId(1);
        logger.debug("【investProduct=】" + investProduct);
    }
}
