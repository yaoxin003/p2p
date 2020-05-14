package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.match.FinanceMatchRes;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/09/15:28
 */
public interface TransferService {

    //转让赎回申请
    public Result transferApply(Integer investId);

    //投资债权交割
    public Result changeInvestclaim(Map<String,Object> claimMap);
}
