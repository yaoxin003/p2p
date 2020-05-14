package com.yx.p2p.ds.match.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.server.FinanceMatchReqServer;
import com.yx.p2p.ds.service.BorrowMatchReqService;
import com.yx.p2p.ds.service.TransferMatchReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:融资撮合请求
 * @author: yx
 * @date: 2020/04/30/17:02
 */
@Service//dubbo注解，暴露服务
@Component//dubbo需要@Component注解，否则无法识别该服务
public class FinanceMatchReqServerImpl implements FinanceMatchReqServer {

    @Autowired
    private BorrowMatchReqService borrowMatchReqService;

    @Autowired
    private TransferMatchReqService transferMatchReqService;

    //借款撮合请求
    //Result.target=借款撮合结果
    public Result borrowMatchReq(FinanceMatchReq financeMatchReq){
        return borrowMatchReqService.borrowMatchReq(financeMatchReq);
    }

    public List<FinanceMatchRes> getBorrowMatchResList(Integer financeCustomerId, String financeBizId){
        return borrowMatchReqService.getBorrowMatchResList(financeCustomerId,financeBizId);
    }

}
