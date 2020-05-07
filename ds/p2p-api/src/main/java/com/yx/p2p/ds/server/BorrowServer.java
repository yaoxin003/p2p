package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.BorrowProduct;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.vo.BorrowContractVo;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/18:57
 */
public interface BorrowServer {

    public List<BorrowProduct> getAllBorrowProductList();

    public BorrowProduct getBorrowProductById(Integer borrowProductId);

    //借款签约
    public Result sign(Borrow borrow);

    List<Borrow> getBorrowListByCustomerId(Integer customerId);

    //补偿调用发送借款撮合
    //Result.target=借款撮合结果
    public Result compensateDealBorrowMatchReq(Integer borrowId);

    public BorrowContractVo getBorrowContractByBorrowId(Integer borrowId);

    //申请放款
    public Result applyLoan(Integer borrowId);
}
