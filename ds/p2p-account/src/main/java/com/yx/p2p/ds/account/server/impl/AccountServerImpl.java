package com.yx.p2p.ds.account.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.model.account.MasterAcc;
import com.yx.p2p.ds.server.AccountServer;
import com.yx.p2p.ds.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/22/16:00
 */
@Service
@Component
public class AccountServerImpl implements AccountServer {

    @Autowired
    private AccountService accountService;

    public MasterAcc getMasterAccByCustomerId(Integer customerId){
        return accountService.getMasterAccByCustomerId(customerId);
    }
}
