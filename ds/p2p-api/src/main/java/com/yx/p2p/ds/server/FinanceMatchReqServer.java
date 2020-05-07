package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;

import java.util.List;

/**
 * @description:融资撮合请求
 * @author: yx
 * @date: 2020/05/01/9:22
 */
public interface FinanceMatchReqServer {
    //借款撮合请求
    public Result borrowMatchReq(FinanceMatchReq financeMatchReq);

    public List<FinanceMatchRes> getBorrowMatchResList(Integer financeCustomerId, String financeBizId);
}
