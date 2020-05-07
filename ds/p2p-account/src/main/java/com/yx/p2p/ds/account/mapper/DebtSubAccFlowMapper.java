package com.yx.p2p.ds.account.mapper;

import com.yx.p2p.ds.model.account.ClaimSubAccFlow;
import com.yx.p2p.ds.model.account.DebtSubAccFlow;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/06/16:16
 */
public interface DebtSubAccFlowMapper extends Mapper<DebtSubAccFlow> {

    public void insertBatchDebtSubAccFlow(List<DebtSubAccFlow> debtSubAccFlowList);

}
