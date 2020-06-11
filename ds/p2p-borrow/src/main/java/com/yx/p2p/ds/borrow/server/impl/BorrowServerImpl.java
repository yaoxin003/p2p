package com.yx.p2p.ds.borrow.server.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.BorrowProduct;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.server.BorrowServer;
import com.yx.p2p.ds.service.BorrowProductService;
import com.yx.p2p.ds.service.BorrowService;
import com.yx.p2p.ds.service.DebtDateValueService;
import com.yx.p2p.ds.vo.BorrowContractVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

import com.alibaba.dubbo.config.annotation.Service;
/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/18:58
 */
@Service//dubbo注解，暴露服务
@Component//dubbo需要@Component注解，否则无法识别该服务
public class BorrowServerImpl implements BorrowServer {
    @Autowired
    private BorrowProductService borrowProductService;

    @Autowired
    private BorrowService borrowService;

    @Override
    public List<BorrowProduct> getAllBorrowProductList() {
        return borrowProductService.getAllBorrowProduct();
    }

    @Override
    public BorrowProduct getBorrowProductById(Integer borrowProductId) {
        return borrowProductService.getBorrowProductById(borrowProductId);
    }

    //借款签约
    @Override
    public Result sign(Borrow borrow) {
        return borrowService.sign(borrow);
    }


    public List<Borrow> getBorrowListByCustomerId(Integer customerId){
        return borrowService.getBorrowListByCustomerId(customerId);
    }

    //补偿调用发送借款撮合
    //Result.target=借款撮合结果
    public Result compensateDealBorrowMatchReq(Integer borrowId){
        return borrowService.compensateDealBorrowMatchReq(borrowId);
    }

    public BorrowContractVo getBorrowContractByBorrowId(Integer borrowId){
        return borrowService.getBorrowContractByBorrowId(borrowId);
    }

    //申请放款
    public Result applyLoan(Integer borrowId){
        return borrowService.applyLoan(borrowId);
    }


    public List<Borrow> getBorrowListByBorrowIdList(Set<Integer> idSet){
        return borrowService.getBorrowListByBorrowIdList(idSet);
    }
}
