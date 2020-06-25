package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Pagination;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.vo.CustomerVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/01/9:45
 */
@Controller
@RequestMapping("crm")
public class CrmController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private CrmServer crmServer;

    @RequestMapping("init")
    public String init(){
        logger.debug("【投资销售总页面】");
        return "init";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(Integer page, Integer rows, CustomerVo customerVo){
        logger.debug("【客户列表页面】");
        //查询条件判断
        String idCard = customerVo.getIdCard();
        List<Customer> crmList = null;
        if(StringUtils.isNotBlank(idCard)){
            logger.debug("【search pagination】");
            crmList = crmServer.search(customerVo,page,rows);
            logger.debug("【crmList=】" + crmList);
            return Pagination.buildMap(crmList.size(),crmList);
        }else{
            logger.debug("【客户列表页面：无查询条件】");
            crmList = new ArrayList<>();
            return Pagination.buildMap(0,crmList);
        }
    }

    /**
     * 添加客户
     * 调用CRM的dubbo服务
     * @param customerVo
     * @return
     * @throws Exception
     */
    @RequestMapping("add")
    @ResponseBody
    public Result add(CustomerVo customerVo) throws Exception{
        logger.debug("【添加客户】" + customerVo);
        return crmServer.add(customerVo);
    }

    @RequestMapping("update")
    @ResponseBody
    public Result update(CustomerVo customerVo) throws Exception{
        logger.debug("【修改客户】" + customerVo);
        return crmServer.update(customerVo);
    }

    /**
     * 修改显示页面
     * @param idCard
     * @return
     */
    @RequestMapping("getByIdCard")
    @ResponseBody
    public CustomerVo getByIdCard(String idCard){
        logger.debug("【idCard=】" + idCard);
        CustomerVo customerVo = new CustomerVo();
        customerVo.setIdCard(idCard);
        //临时方案---begin
        List<Customer> crmList = crmServer.search(customerVo,1,1);
        Customer resCrm = crmList.get(0);
        //3.封装前台对象
        //3.1拷贝属性
        CustomerVo resCrmVo = new CustomerVo();
        try {
            BeanUtils.copyProperties(resCrmVo,resCrm);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        resCrmVo.setIdCardOld(resCrm.getIdCard());
        //临时方案---end
        return resCrmVo;
    }

    @RequestMapping("getById")
    @ResponseBody
    public Customer getById(Integer crmId){
        logger.debug("【crmId=】" + crmId);
        Customer customer = crmServer.getCustomerById(crmId);
        logger.debug("【customer=】" + customer);
        return customer;
    }

    @RequestMapping("test")
    public String test(){
        return "test";
    }

}

