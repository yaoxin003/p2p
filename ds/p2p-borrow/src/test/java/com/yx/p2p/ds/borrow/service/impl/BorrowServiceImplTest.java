package com.yx.p2p.ds.borrow.service.impl;

import com.yx.p2p.ds.borrow.P2pBorrowApplication;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.service.BorrowProductService;
import com.yx.p2p.ds.service.BorrowService;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/29/13:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pBorrowApplication.class)
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
}
