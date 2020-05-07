package com.yx.p2p.ds.borrow.mapper;

import com.yx.p2p.ds.model.borrow.BorrowDtl;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/04/9:34
 */
public interface BorrowDtlMapper extends Mapper<BorrowDtl>{

    public void insertBatchBorrowDtlList(List<BorrowDtl> borrowDtlList);

}
