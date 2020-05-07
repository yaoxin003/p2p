package com.yx.p2p.ds.vo;

import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.BorrowDtl;

import java.io.Serializable;
import java.util.List;

/**
 * @description:借款合同（借款主表+借款明细表）
 * @author: yx
 * @date: 2020/05/04/10:49
 */
public class BorrowContractVo implements Serializable{
    //没有id字段不行，BorrowContractVoMapper.xml中的selectBorrowContract会出现异常
    //Invocation of init method failed; nested exception is tk.mybatis.mapper.MapperException:
    // tk.mybatis.mapper.MapperException: java.lang.StringIndexOutOfBoundsException: String index out of range: -1
    private Integer id;

    private Borrow borrow;
    private List<BorrowDtl> borrowDtlList;

   public Borrow getBorrow() {
        return borrow;
    }

    public void setBorrow(Borrow borrow) {
        this.borrow = borrow;
    }

    public List<BorrowDtl> getBorrowDtlList() {
        return borrowDtlList;
    }

    public void setBorrowDtlList(List<BorrowDtl> borrowDtlList) {
        this.borrowDtlList = borrowDtlList;
    }

    @Override
    public String toString() {
        return "BorrowContractVo{" +
                "borrow=" + borrow +
                ", borrowDtlList=" + borrowDtlList +
                '}';
    }
}
