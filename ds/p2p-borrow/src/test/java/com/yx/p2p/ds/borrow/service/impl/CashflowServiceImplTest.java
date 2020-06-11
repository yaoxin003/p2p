package com.yx.p2p.ds.borrow.service.impl;

import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.service.CashflowService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.PageUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CashflowServiceImplTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CashflowService cashflowService;

    @Test
    public void testGetPageCountByTotalCountAndPageSize(){
        Integer totalCount = 130;
        Integer pageSize = 50;
        Integer pageCount = PageUtil.getPageCount(totalCount, pageSize);

    }

    @Test
    public void testGetCashflowListByPage(){
        Integer pageSize = 50;
        Date arriveDate = new Date();
        Integer totalCount = cashflowService.getCashflowListCount(arriveDate);;
        Integer pageCount = PageUtil.getPageCount(totalCount, pageSize);
        for(int i=0; i<pageCount; i++){
            List<Cashflow> cashflowList = cashflowService.getCashflowListByPage(arriveDate, i, pageSize);
        }
    }

    @Test
    public void testGetCashflowListCount(){
        Date arriveDate = DateUtil.str2Date("2020-07-16");
        cashflowService.getCashflowListCount(arriveDate);
    }


}
