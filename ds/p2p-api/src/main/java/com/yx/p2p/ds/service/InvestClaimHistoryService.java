package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.InvestClaim;

import java.util.List; /**
 * @description:
 * @author: yx
 * @date: 2020/05/13/14:26
 */
public interface InvestClaimHistoryService {

    public Result genInvestClaimHistoryList(List<InvestClaim> investClaimList);
}
