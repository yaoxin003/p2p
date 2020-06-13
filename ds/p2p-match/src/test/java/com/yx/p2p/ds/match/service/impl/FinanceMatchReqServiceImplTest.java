package com.yx.p2p.ds.match.service.impl;

import com.yx.p2p.ds.enums.mq.MQStatusEnum;
import com.yx.p2p.ds.match.P2pMatchApplication;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.service.match.BorrowMatchReqService;
import com.yx.p2p.ds.service.match.InvestMatchReqService;
import com.yx.p2p.ds.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/06/11:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pMatchApplication.class)
public class FinanceMatchReqServiceImplTest {

    @Autowired
    private BorrowMatchReqService borrowMatchReqService;

    @Autowired
    private InvestMatchReqService investMatchReqService;

    @Test
    public void testSelectWaitMatchAmtInvestReqList(){
        Class<?>[] methodParamClass =  new Class[1];
        methodParamClass[0] = FinanceMatchReq.class;

        Object[] methodParamObj = new Object[]{new FinanceMatchReq()};

        TestUtil.invokePrivateMethodParms(investMatchReqService,
                "getWaitMatchAmtInvestReqList",
                methodParamClass,methodParamObj);
    }

    @Test
    public void testLoanNotice(){
        HashMap<String,String> loanNotify = new HashMap<>();
        loanNotify.put("bizId","3");//biz=borrow.id
        loanNotify.put("orderSn","3");//orderSn=borrow.id
        loanNotify.put("customerId","15");//融资客户
        loanNotify.put("status", MQStatusEnum.OK.getStatus());
        borrowMatchReqService.loanNotice(loanNotify);
    }

}
