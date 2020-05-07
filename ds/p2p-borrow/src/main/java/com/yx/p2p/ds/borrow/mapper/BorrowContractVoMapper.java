package com.yx.p2p.ds.borrow.mapper;

import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.vo.BorrowContractVo;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/29/14:57
 */
public interface BorrowContractVoMapper extends Mapper<BorrowContractVo> {

    List<BorrowContractVo> selectBorrowContract(Borrow borrow);
}
