package com.yx.p2p.ds.crm.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.service.crm.CrmService;
import com.yx.p2p.ds.vo.CustomerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/03/18:18
 */
@Service//dubbo注解，暴露服务
@Component//dubbo需要@Component注解，否则无法识别该服务
public class CrmServerImpl implements CrmServer{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CrmService crmService;

    public Result add(CustomerVo customerVo){
        logger.debug("add【customerVo=】" + customerVo);
        return crmService.add(customerVo);
    }

    public Result update(CustomerVo customerVo){
        logger.debug("update【customerVo=】" + customerVo);
        return crmService.update(customerVo);
    }

    /**
        * @description: 查询缓存和数据库
        * @author:  YX
        * @date:    2020/04/04 18:10
        * @param: customerVo
        * @param: currentPage
        * @param: pageSize
        * @return: java.util.List<com.yx.p2p.ds.model.Customer>
        * @throws:
        */
    public List<Customer> search(CustomerVo customerVo, Integer currentPage, Integer pageSize){
        List<Customer> result = null;
        //查询缓存
        result = crmService.getCustomerListByIdCardInCache(customerVo);
        if(result.isEmpty()){
            //查询数据库
            result = crmService.getCustomerListByPagination(customerVo,currentPage,pageSize);
            //存入缓存中
            crmService.addCache(result);
        }
        return result;
    }

    @Override
    public Customer getCustomerById(Integer customerId) {
        Customer customer = crmService.getCustomerByIdInDB(customerId);
        return customer;
    }
}
