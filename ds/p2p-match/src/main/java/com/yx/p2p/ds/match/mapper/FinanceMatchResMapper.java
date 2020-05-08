package com.yx.p2p.ds.match.mapper;

import com.yx.p2p.ds.model.match.FinanceMatchRes;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/07/19:31
 */
public interface FinanceMatchResMapper extends Mapper<FinanceMatchRes> {

    public void insertBatchFinanceMatchResList(List<FinanceMatchRes> financeMatchResList);
}
