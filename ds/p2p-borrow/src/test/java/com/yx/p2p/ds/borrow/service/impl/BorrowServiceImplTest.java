package com.yx.p2p.ds.borrow.service.impl;

import com.yx.p2p.ds.enums.mq.MQStatusEnum;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.service.borrow.BorrowProductService;
import com.yx.p2p.ds.service.borrow.BorrowService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/29/13:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BorrowServiceImplTest {

    @Autowired
    private BorrowProductService borrowProductService;

    @Autowired
    private BorrowService borrowService;

    @Test
    public void testCalMonthPay(){
        //月供
        Class<?>[] methodParamClass1 =  new Class[3];
        methodParamClass1[0] = BigDecimal.class;
        methodParamClass1[1] = BigDecimal.class;
        methodParamClass1[2] = Integer.class;
        Object[] methodParamObj11 = new Object[]{new BigDecimal("100000"), new BigDecimal("0.0134"),12};
        Object calMonthPay1 = TestUtil.invokePrivateMethodParms(borrowService, "calMonthPay", methodParamClass1, methodParamObj11);
        //借款费用
        Class<?>[] methodParamClass2 =  new Class[3];
        methodParamClass2[0] = BigDecimal.class;
        methodParamClass2[1] = Integer.class;
        methodParamClass2[2] = BigDecimal.class;
        Object[] methodParamObj12 = new Object[]{ (BigDecimal) calMonthPay1,12,new BigDecimal("100000")};
        TestUtil.invokePrivateMethodParms(borrowService,"calTotalBorrowFee",methodParamClass2,methodParamObj12);


        //月供
        Object[] methodParamObj21 = new Object[]{new BigDecimal("100000"), new BigDecimal("0.0134"),24};
        Object calMonthPay2 = TestUtil.invokePrivateMethodParms(borrowService,"calMonthPay",methodParamClass1,methodParamObj21);
        //借款费用
        Object[] methodParamObj22 = new Object[]{ (BigDecimal) calMonthPay2,24,new BigDecimal("100000")};
        TestUtil.invokePrivateMethodParms(borrowService,"calTotalBorrowFee",methodParamClass2,methodParamObj22);


        //月供
        Object[] methodParamObj31 = new Object[]{new BigDecimal("100000"), new BigDecimal("0.0134"),36};
        Object calMonthPay3 = TestUtil.invokePrivateMethodParms(borrowService,"calMonthPay",methodParamClass1,methodParamObj31);
        //借款费用
        Object[] methodParamObj32 = new Object[]{ (BigDecimal) calMonthPay3,36,new BigDecimal("100000")};
        TestUtil.invokePrivateMethodParms(borrowService,"calTotalBorrowFee",methodParamClass2,methodParamObj32);

    }

    @Test
    public void testBuildMonthReturnDayAndFirstReturnDate(){
        Class<?>[] methodParamClass1 =  new Class[2];
        methodParamClass1[0] = Date.class;
        methodParamClass1[1] = Borrow.class;

        Borrow borrow = new Borrow();
        Object[] methodParamObj1 = new Object[]{DateUtil.str2Date("2020-04-15"), borrow};

        Object calMonthPay1 = TestUtil.invokePrivateMethodParms(borrowService,
                "buildMonthReturnDayAndFirstReturnDate", methodParamClass1, methodParamObj1);
    }

    //放款通知
    @Test
    public void testLoanNotice(){
        HashMap<String,String> loanNotify = new HashMap<>();
        loanNotify.put("bizId","3");//biz=borrow.id
        loanNotify.put("orderSn","3");//orderSn=borrow.id
        loanNotify.put("customerId","15");//融资客户
        loanNotify.put("status", MQStatusEnum.OK.getStatus());
        borrowService.loanNotice(loanNotify);
    }

    @Test
    public void testGetBorrowListByBorrowIdList(){
        Set<Integer> idSet = new HashSet<>();
        idSet.add(1);
        borrowService.getBorrowListByBorrowIdList(idSet);
    }


}
