package com.yx.p2p.ds.service;

import com.yx.p2p.ds.model.borrow.BorrowProduct;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/18:50
 */
public interface BorrowProductService {

    public List<BorrowProduct> getAllBorrowProduct();

    public BorrowProduct getBorrowProductById(Integer borrowProductId);

}
