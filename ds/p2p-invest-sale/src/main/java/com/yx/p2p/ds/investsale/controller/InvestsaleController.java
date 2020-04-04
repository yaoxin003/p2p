package com.yx.p2p.ds.investsale.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/02/15:26
 */
@Controller
@RequestMapping("investsale")
public class InvestsaleController{

    @RequestMapping("init")
    public String init(){
        return "init";
    }
}
