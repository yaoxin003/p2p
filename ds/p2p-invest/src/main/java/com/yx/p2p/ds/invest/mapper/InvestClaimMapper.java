package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.base.insert.InsertMapper;

import java.util.List;

/**
 * @description:投资债权明细
 * @author: yx
 * @date: 2020/05/07/18:05
 */
public interface InvestClaimMapper extends Mapper<InvestClaim>,MyInsertListMapper<InvestClaim>{

    public void deleteBatchInvestClaim(List<Integer> investClaimIdList);
}
