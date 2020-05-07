package com.yx.p2p.ds.invest.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestProduct;
import com.yx.p2p.ds.server.InvestServer;
import com.yx.p2p.ds.service.InvestProductService;
import com.yx.p2p.ds.service.InvestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:10
 */
@Service
@Component//dubbo需要@Component注解，否则无法识别该服务
public class InvestServerImpl implements InvestServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestProductService investProductService;

    @Autowired
    private InvestService investService;

    public List<InvestProduct> getAllInvestProductList(){
        return investProductService.getAllInvestProductList();
    }

    public String getAllInvestProductJSON(){
        return investProductService.getAllInvestProductJSON();
    }

    public InvestProduct getInvestProductById(Integer investProductId){
        return investProductService.getInvestProductById(investProductId);
    }

    //充值投资
    public Result rechargeInvest(Invest invest){
        return investService.rechargeInvest(invest);
    }

    public List<Invest> getInvestVoList(Invest invest){
        return investService.getInvestVoList(invest);
    }

    //补偿网关支付
    public Result compensateGateway(Invest invest){
        return investService.compensateGateway(invest);
    }


    public Invest getInvestByInvestId(Integer investId){
        return investService.getInvestByInvestId(investId);
    }
}
