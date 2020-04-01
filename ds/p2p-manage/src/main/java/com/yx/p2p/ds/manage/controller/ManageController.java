package com.yx.p2p.ds.manage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:系统管理
 * @author: yx
 * @date: 2020/03/27/9:53
 */
@Controller
@RequestMapping("manage")
public class ManageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("welcome")
    public String welcome(){
        logger.debug("welcome");
        return "welcome";
    }

}
