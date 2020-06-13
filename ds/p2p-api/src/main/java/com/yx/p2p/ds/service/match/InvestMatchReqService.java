package com.yx.p2p.ds.service.match;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.match.InvestMatchReq;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/14:20
 */
public interface InvestMatchReqService {

    public Result addInvestMatchReq(List<InvestMatchReq> investMatchReqList);

    //获得待撮合的投资撮合请求集合
    public List<InvestMatchReq> getWaitMatchAmtInvestReqList();
}
