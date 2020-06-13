package com.yx.p2p.ds.invest.com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.service.invest.TransferService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/23/18:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransferServiceTest {

    @Autowired
    private TransferService transferService;

    @Test
    public void testGetTransferContractText(){
        Integer investId = 1;
        transferService.getTransferContractText(investId);
    }

    public static void main(String[] args) {
        Map<Integer, InvestClaim> tmpInvestClaimMap = new HashMap<Integer, InvestClaim>();
        InvestClaim investClaim1 = new InvestClaim();
        investClaim1.setId(1);
        tmpInvestClaimMap.put(1,investClaim1);
        InvestClaim investClaim2 = new InvestClaim();
        investClaim2.setId(2);
        tmpInvestClaimMap.put(2,investClaim2);

        Map<Integer, InvestClaim> dbClaimMap = new HashMap<Integer, InvestClaim>();
        BeanHelper.copyMap(dbClaimMap,tmpInvestClaimMap);
        for (Integer key : dbClaimMap.keySet()) {
            System.out.println(key + "=" + dbClaimMap.get(key));
        }

    }


}
