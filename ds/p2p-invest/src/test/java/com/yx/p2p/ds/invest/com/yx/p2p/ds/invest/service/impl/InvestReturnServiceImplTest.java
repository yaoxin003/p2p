package com.yx.p2p.ds.invest.com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.service.InvestReturnService;
import com.yx.p2p.ds.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/04/18:04
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class InvestReturnServiceImplTest {
    @Autowired
    private InvestReturnService investReturnService;

    @Test
    public void testGetInvestReturnGroupListCount(){
        Date arriveDate = new Date();
        investReturnService.getInvestReturnGroupListCount(arriveDate);
    }

    @Test
    public void testGetInvestReturnGroupList(){
        Date arriveDate = DateUtil.date2DateYMD(new Date());
        investReturnService.getInvestReturnGroupList(arriveDate,1,2);
        investReturnService.getInvestReturnGroupList(arriveDate,2,2);
    }
}
