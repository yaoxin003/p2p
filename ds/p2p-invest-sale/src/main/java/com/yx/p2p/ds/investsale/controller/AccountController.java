package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Pagination;
import com.yx.p2p.ds.model.account.MasterAcc;
import com.yx.p2p.ds.server.AccountServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/22/16:04
 */
@Controller
@RequestMapping("account")
public class AccountController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private AccountServer accountServer;

    @RequestMapping("getMasterAccByCustomerId")
    @ResponseBody
    public Map<String,Object> getMasterAccByCustomerId(Integer customerId){
        logger.debug("【查询主账户，入参：customerId=】" + customerId);
        MasterAcc masterAcc = accountServer.getMasterAccByCustomerId(customerId);
        List<MasterAcc> masterAccList = new ArrayList<>();
        masterAccList.add(masterAcc);
        logger.debug("【查询主账户，结果：masterAcc=】" + masterAcc);
        return Pagination.buildMap(0,masterAccList);
    }
}
