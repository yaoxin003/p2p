package com.yx.p2p.ds.service.match;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/09/18:04
 */
public interface TransferMatchReqService {
    //转让撮合请求
    public Result transferMatchReq(List<Map<String,Object>> transferMatchReqMapList);

}
