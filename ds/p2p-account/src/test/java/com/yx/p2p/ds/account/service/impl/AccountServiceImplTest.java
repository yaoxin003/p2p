package com.yx.p2p.ds.account.service.impl;

import com.yx.p2p.ds.account.P2pAccountApplication;
import com.yx.p2p.ds.enums.mq.MQStatusEnum;
import com.yx.p2p.ds.service.account.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.HashMap;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/07/8:37
 */
@SpringBootTest(classes = P2pAccountApplication.class)
@RunWith(SpringRunner.class)
public class AccountServiceImplTest {

    @Autowired
    private AccountService accountService;

    @Test
    public void testLoanNotice(){
        HashMap<String,String> loanNotify = new HashMap<>();
        loanNotify.put("bizId","1");//biz=borrow.id
        loanNotify.put("orderSn","1");//orderSn=borrow.id
        loanNotify.put("customerId","18");//融资客户
        loanNotify.put("status", MQStatusEnum.OK.getStatus());
        accountService.loanNotice(loanNotify);
    }
}
