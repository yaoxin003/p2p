package com.yx.p2p.ds.borrow.mapper;

import com.yx.p2p.ds.borrow.P2pBorrowApplication;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.BorrowDtl;
import com.yx.p2p.ds.vo.BorrowContractVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/04/14:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = P2pBorrowApplication.class)
public class BorrowContractVoMapperTest {

    @Autowired
    private BorrowContractVoMapper borrowContractVoMapper;

    @Test
    public void testSelectBorrowContract(){
        Borrow borrow = new Borrow();
        borrow.setId(1);
        List<BorrowContractVo> borrowContractVoList = borrowContractVoMapper.selectBorrowContract(borrow);
        for(BorrowContractVo borrowContractVo:borrowContractVoList){
            Borrow borrow1 = borrowContractVo.getBorrow();
            List<BorrowDtl> borrowDtlList = borrowContractVo.getBorrowDtlList();
            System.out.println("【借款】borrow1=" + borrow1);
            System.out.println("【借款明细】borrowDtlList=" + borrowDtlList);
        }
    }

}
