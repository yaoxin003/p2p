package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/11/9:10
 */
public interface FinanceMatchReqService {
    //处理融资撮合
    public Result dealFinanceMatch(FinanceMatchReq financeMatchReq, List<InvestMatchReq> resNoMatchInvestReqList,
                                   List<InvestMatchReq> resMatchedInvestReqList, List<FinanceMatchRes> resFinanceMatchResList);
}
