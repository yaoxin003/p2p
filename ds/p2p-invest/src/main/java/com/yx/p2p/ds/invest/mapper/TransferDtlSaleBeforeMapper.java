package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.TransferDtlSaleBefore;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/10/15:32
 */
public interface TransferDtlSaleBeforeMapper extends Mapper<TransferDtlSaleBefore>,
        InsertListMapper<TransferDtlSaleBefore> {

}
