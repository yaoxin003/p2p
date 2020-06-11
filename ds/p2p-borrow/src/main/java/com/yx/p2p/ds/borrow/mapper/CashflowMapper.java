package com.yx.p2p.ds.borrow.mapper;

import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/30/12:35
 */
public interface CashflowMapper extends Mapper<Cashflow> ,MyInsertListMapper<Cashflow> {
}
