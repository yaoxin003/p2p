package com.yx.p2p.ds.invest.com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.enums.mq.MQStatusEnum;
import com.yx.p2p.ds.invest.P2pInvestApplication;
import com.yx.p2p.ds.service.invest.InvestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/07/18:25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pInvestApplication.class)
public class InvestServiceImplTest {

    @Autowired
    private InvestService investService;

    //放款通知
    @Test
    public void testLoanNotice(){
        HashMap<String,String> loanNotify = new HashMap<>();
        loanNotify.put("bizId","3");//biz=borrow.id
        loanNotify.put("orderSn","3");//orderSn=borrow.id
        loanNotify.put("customerId","15");//融资客户
        loanNotify.put("status", MQStatusEnum.OK.getStatus());
        investService.loanNotice(loanNotify);
    }

    @Test
    public void testUpdateBizStateByTransferId(){
        Integer investId = 99;
        InvestBizStateEnum bizStateEnum = InvestBizStateEnum.TRANSFER_SUC;
        investService.updateInvestBizState(investId,bizStateEnum);
    }
}
