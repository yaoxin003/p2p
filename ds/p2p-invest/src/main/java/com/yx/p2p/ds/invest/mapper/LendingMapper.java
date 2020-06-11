package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.Lending;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/21/13:18
 */
public interface LendingMapper  extends Mapper<Lending>,MyInsertListMapper<Lending> {
}
