package com.yx.p2p.ds.borrowsale.controller;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.BorrowProduct;
import com.yx.p2p.ds.server.BorrowServer;
import com.yx.p2p.ds.vo.BorrowContractVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import com.alibaba.dubbo.config.annotation.Reference;
/**
 * @description:
 * @author: yx
 * @date: 2020/04/27/15:52
 */
@Controller
@RequestMapping("borrow")
public class BorrowController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private BorrowServer borrowServer;

    @RequestMapping("getAllBorrowProductList")
    @ResponseBody
    public List<BorrowProduct> getAllBorrowProductList(){
        List<BorrowProduct> allBorrowProductList = borrowServer.getAllBorrowProductList();
        logger.debug("【查询所有借款产品】allBorrowProductList=" + allBorrowProductList);
        return allBorrowProductList;
    }

    @RequestMapping("getBorrowProductById")
    @ResponseBody
    public BorrowProduct getBorrowProductById(Integer borrowProductId){
        logger.debug("【准备查询借款产品】borrowProductId=" + borrowProductId);
        BorrowProduct borrowProduct = borrowServer.getBorrowProductById(borrowProductId);
        logger.debug("【查询借款产品】borrowProduct=" + borrowProduct);
        return borrowProduct;
    }

    //借款签约
    @RequestMapping("sign")
    @ResponseBody
    public Result sign(Borrow borrow){
        logger.debug("【借款签约】borrow=" + borrow);
        Result result = borrowServer.sign(borrow);
        logger.debug("【借款签约撮合结果】result=" + result);
        return result;
    }

    @RequestMapping("getBorrowListByCustomerId")
    @ResponseBody
    public List<Borrow> getBorrowListByCustomerId(Integer customerId){
        logger.debug("【查询借款人借款列表：入参】customerId=" + customerId);
        List<Borrow> borrowList = borrowServer.getBorrowListByCustomerId(customerId);
        logger.debug("【查询借款人借款列表：结果】borrowList=" + borrowList);
        return borrowList;
    }

    /**
     * @description: 补偿调用借款撮合
     * @author:  YX
     * @date:    2020/05/03 14:58
     * @param: financeCustomerId 融资客户编号
     * @param: financeBizId 融资业务编号:借款编号/转让协议编号
     * @return: com.yx.p2p.ds.easyui.Result result.target为借款撮合结果List<FinanceMatchRes>
     */
    @RequestMapping("compensateDealBorrowMatchReq")
    @ResponseBody
    public Result compensateDealBorrowMatchReq(Integer borrowId){
        logger.debug("【补偿调用借款撮合，入参：】borrowId=" + borrowId);
        Result result = borrowServer.compensateDealBorrowMatchReq(borrowId);//幂等性接口
        logger.debug("【补偿调用借款撮合，结果：】result=" + result);
        return result;

    }

    //借款合同（借款+借款明细）
    @RequestMapping("getBorrowContractByBorrowId")
    @ResponseBody
    public BorrowContractVo getBorrowContractByBorrowId(Integer borrowId){
        logger.debug("【查询数据库获得借款协议】入参:borrowId=" + borrowId);
        BorrowContractVo borrowContractVo = borrowServer.getBorrowContractByBorrowId(borrowId);
        logger.debug("【查询数据库获得借款协议】结果：borrowContractVo=" + borrowContractVo);
        return borrowContractVo;
    }

    //申请放款
    @RequestMapping("applyLoan")
    @ResponseBody
    public Result applyLoan(Integer borrowId){
        logger.debug("【申请放款】入参borrowId=" + borrowId);
        Result result = borrowServer.applyLoan(borrowId);
        logger.debug("【申请放款】结果result=" + result);
        return result;
    }
}
