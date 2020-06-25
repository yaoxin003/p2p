package com.yx.p2p.ds.invest.com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.service.invest.InvestDebtValService;
import com.yx.p2p.ds.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/17/19:06
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class InvestDebtValServiceImplTest {

    @Autowired
    private InvestDebtValService investDebtValService;

    @Test
    public void testGetInvestDebtValPageList(){
        Date arriveDate = DateUtil.str2Date("2020-07-16");
        List<InvestDebtVal> investDebtValPageList =
                investDebtValService.getInvestDebtValPageList(arriveDate, 0, 100);
        for (InvestDebtVal investDebtVal : investDebtValPageList) {
            System.out.println("=============" + investDebtVal.getIdStr());
        }
    }

    @Test
    public void testGetInvestDebtValReturnAmtPageList(){
        Date arriveDate = DateUtil.str2Date("2020-06-19");
        Integer investDebtValReturnAmtCount = investDebtValService.getInvestDebtValReturnAmtCount(arriveDate);
        System.out.println("investDebtValReturnAmtCount = " + investDebtValReturnAmtCount);
    }

}
