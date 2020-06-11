package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.TransferDtl;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/09/15:45
 */
public interface TransferDtlMapper extends Mapper<TransferDtl>,InsertListMapper<TransferDtl> {
}
