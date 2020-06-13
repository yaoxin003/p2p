package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.InvestReturn;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/04/9:53
 */
public interface InvestReturnMapper extends Mapper<InvestReturn>,MyInsertListMapper<InvestReturn> {

}
