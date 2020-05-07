package com.yx.p2p.ds.borrow.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.borrow.mapper.BorrowProductMapper;
import com.yx.p2p.ds.model.borrow.BorrowProduct;
import com.yx.p2p.ds.model.invest.InvestProduct;
import com.yx.p2p.ds.service.BorrowProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/18:50
 */
@Service
public class BorrowProductServiceImpl implements BorrowProductService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BorrowProductMapper borrowProductMapper;

    public List<BorrowProduct> getAllBorrowProduct(){
        return this.selectDBAllBorrowProduct();
    }

    private List<BorrowProduct> selectDBAllBorrowProduct() {
        List<BorrowProduct> borrowProductList = borrowProductMapper.selectAll();
        logger.debug("【所有借款产品】borrowProductList=" + borrowProductList);
        return borrowProductList;
    }


    public BorrowProduct getBorrowProductById(Integer borrowProductId){
        BorrowProduct borrowProduct = this.selectDBBorrowProduct(borrowProductId);
        return borrowProduct;
    }

    public BorrowProduct selectDBBorrowProduct(Integer borrowProductId) {
        logger.debug("【准备查询数据库获得借款产品】borrowProductId="+borrowProductId);
        BorrowProduct borrowProduct = borrowProductMapper.selectByPrimaryKey(borrowProductId);
        logger.debug("【查询数据库获得借款产品】borrowProduct="+borrowProduct);
        return borrowProduct;
    }
}
