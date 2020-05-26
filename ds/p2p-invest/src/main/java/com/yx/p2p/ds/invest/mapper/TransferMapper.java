package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.Transfer;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/09/15:44
 */
public interface TransferMapper extends Mapper<Transfer> {
    public List<Transfer> selectTransferContract(Transfer transfer);
}
