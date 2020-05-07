package com.yx.p2p.ds.borrow.service.impl;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.yx.p2p.ds.borrow.P2pBorrowApplication;
import com.yx.p2p.ds.service.BorrowProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/19:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pBorrowApplication.class)
public class BorrowProductServiceImplTest {
    @Autowired
    private BorrowProductService borrowProductService;

    @Test
    public void testGetAllBorrowProduct(){
        borrowProductService.getAllBorrowProduct();
    }
}
