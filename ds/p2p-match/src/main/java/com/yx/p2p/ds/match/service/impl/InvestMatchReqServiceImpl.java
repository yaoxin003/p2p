package com.yx.p2p.ds.match.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.match.mapper.InvestMatchReqMapper;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.service.InvestMatchReqService;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:投资撮合请求
 * @author: yx
 * @date: 2020/04/26/14:19
 */
@Service
public class InvestMatchReqServiceImpl implements InvestMatchReqService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestMatchReqMapper investMatchReqMapper;

    @Override
    public Result addInvestMatchReq(InvestMatchReq investMatchReq) {
        logger.debug("【插入投资撮合请求入参】investMatchReq=" + investMatchReq);
        Result result  = this.checkNoInvestMatchReq(investMatchReq);
        int count = 0;
        if(Result.checkStatus(result)){
            BeanHelper.setAddDefaultField(investMatchReq);
            count = investMatchReqMapper.insert(investMatchReq);
            if( count == 1){
                result = Result.success();
            }
        }
        logger.debug("【插入投资撮合请求结果】count=" + count + "，result=" + result);
        return result;
    }



    private Result checkNoInvestMatchReq(InvestMatchReq req) {
        logger.debug("【验证不存在投资撮合请求数据入参】req=" + req);
        Result result = Result.error();
        String orderSn = req.getInvestOrderSn();
        InvestMatchReq investMatchReq = this.queryDBInvestMatchReq(orderSn);
        if(investMatchReq == null){
            result = Result.success();
        }
        logger.debug("【验证不存在投资撮合请求数据结果】result=" + result);
        return result;
    }

    private InvestMatchReq queryDBInvestMatchReq(String orderSn) {
        logger.debug("【查询投资撮合请求入参】orderSn=" + orderSn);
        InvestMatchReq req = new InvestMatchReq();
        req.setInvestOrderSn(orderSn);
        InvestMatchReq investMatchReq = investMatchReqMapper.selectOne(req);
        logger.debug("【查询投资撮合请求结果】investMatchReq=" + investMatchReq);
        return investMatchReq;
    }
}
