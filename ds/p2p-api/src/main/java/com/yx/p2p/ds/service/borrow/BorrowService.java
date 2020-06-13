package com.yx.p2p.ds.service.borrow;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.vo.BorrowContractVo;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/18:49
 */
public interface BorrowService {

    //借款签约
    public Result sign(Borrow borrow);

    public List<Borrow> getBorrowListByCustomerId(Integer customerId);

    //补偿调用发送借款撮合
    //Result.target=借款撮合结果
    public Result compensateDealBorrowMatchReq(Integer borrowId);

    public BorrowContractVo getBorrowContractByBorrowId(Integer borrowId);

    //申请放款
    public Result applyLoan(Integer borrowId);

    //放款通知
    public Result loanNotice(HashMap<String, String> loanMap);

    public List<Borrow> getBorrowListByBorrowIdList(Set<Integer> idSet);
}
