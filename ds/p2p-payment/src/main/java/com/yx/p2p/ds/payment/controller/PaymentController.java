package com.yx.p2p.ds.payment.controller;

import com.yx.p2p.ds.model.BaseBank;
import com.yx.p2p.ds.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/08/14:51
 */
@Controller
@RequestMapping("payment")
public class PaymentController {

   @Autowired
    private PaymentService paymentService;

    @RequestMapping("getAllBaseBankList")
    @ResponseBody
    public List<BaseBank> getAllBaseBankList(){
        return paymentService.getAllBaseBankList();
    }

    @RequestMapping("openAdd")
    public String openAdd(Integer customerId){
        return "openAdd";
    }
}
