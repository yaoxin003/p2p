package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.InvestClaim;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:投资债权明细
 * @author: yx
 * @date: 2020/05/07/18:05
 */
public interface InvestClaimMapper extends Mapper<InvestClaim>{

    public void insertBathInvestClaim(List<InvestClaim> investClaimList);
}
