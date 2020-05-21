package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.match.InvestMatchReq;

import java.util.HashMap;
import java.util.List;

/**
 * @description:融资撮合请求
 * @author: yx
 * @date: 2020/04/30/14:38
 */
public interface BorrowMatchReqService {

    //借款撮合请求
    public Result borrowMatchReq(FinanceMatchReq financeMatchReq);

    public List<FinanceMatchRes> getBorrowMatchResList(Integer financeCustomerId, String financeBizId);

    //放款通知
    public Result loanNotice(HashMap<String, String> loanMap);


}
