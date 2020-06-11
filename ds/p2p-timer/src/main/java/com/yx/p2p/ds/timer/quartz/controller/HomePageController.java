package com.yx.p2p.ds.timer.quartz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/30/10:30
 */
@Controller
public class HomePageController {

    @RequestMapping("index")
    public String index(){
        return "redirect:/task/init";
    }

    @RequestMapping("")
    public String home(){
        return "redirect:/index";
    }
}
