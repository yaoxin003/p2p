package com.yx.p2p.ds.invest.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.invest.mapper.InvestProductMapper;
import com.yx.p2p.ds.model.InvestProduct;
import com.yx.p2p.ds.service.InvestProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:06
 */
@Service
public class InvestProductServiceImpl implements InvestProductService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestProductMapper investProductMapper;

    public List<InvestProduct> getAllInvestProductList(){
        List<InvestProduct> investProducts = investProductMapper.selectAll();
        logger.debug("【allInvestProductList=】" + investProducts);
        return investProducts;
    }

    /**
     * 全部投资产品JSON
     * @return
     */
    public String getAllInvestProductJSON(){
        List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
        List<InvestProduct> allInvestProductList = this.getAllInvestProductList();
        for(InvestProduct investProduct: allInvestProductList){
            Map<String,String> map1 = new TreeMap<>();
            map1.put("text","起投金额：" + investProduct.getBeginAmt().toString() + "元");
            map1.put("group",investProduct.getName());
            mapList.add(map1);
            Map<String,String> map2 = new TreeMap<>();
            map2.put("text","年化收益率：" + investProduct.getYearIrr().multiply(new BigDecimal("100")) + "%");
            map2.put("group",investProduct.getName());
            mapList.add(map2);
            Map<String,String> map3 = new TreeMap<>();
            map3.put("text","固定期限：" + investProduct.getDayCount() + "天");
            map3.put("group",investProduct.getName());
            mapList.add(map3);
            Map<String,String> map4 = new TreeMap<>();
            map4.put("text","<button iconCls='icon-add' class='easyui-linkbutton' onclick='openLend("+ investProduct.getId() + ")'>立即出借</button>");
            map4.put("group",investProduct.getName());
            mapList.add(map4);
        }
        String allInvestProductJSON = JSON.toJSONString(mapList);
        logger.debug("【allInvestProductJSON=】" + allInvestProductJSON);
        return allInvestProductJSON;
    }

}
