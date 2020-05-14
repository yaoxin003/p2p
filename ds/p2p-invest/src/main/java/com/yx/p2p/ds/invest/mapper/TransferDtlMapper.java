package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.TransferDtl;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/09/15:45
 */
public interface TransferDtlMapper extends Mapper<TransferDtl> {
    public void insertBatchTransferDtl(List<TransferDtl> transferDtlList);
}
