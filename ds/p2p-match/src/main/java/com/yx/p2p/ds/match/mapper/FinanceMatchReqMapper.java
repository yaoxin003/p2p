package com.yx.p2p.ds.match.mapper;

import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/30/18:13
 */
public interface FinanceMatchReqMapper extends Mapper<FinanceMatchReq>,MyInsertListMapper<FinanceMatchReq> {

}
