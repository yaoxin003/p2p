package com.yx.p2p.ds.crm.controller;

import com.yx.p2p.ds.easyui.Pagination;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.Customer;
import com.yx.p2p.ds.service.CrmService;
import com.yx.p2p.ds.vo.CustomerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
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

    @Autowired
    private CrmService crmService;

    @RequestMapping("init")
    public String init(){
        return "init";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(Integer page, Integer rows, CustomerVo customerVo){
        logger.debug("【page=" + page + ",rows=】" + rows + "【,customerVo=】" + customerVo);
        List<Customer> customerList = crmService.getCustomerListByPagination(customerVo, page, rows);
        int total = crmService.queryCustomerCount(customerVo);
        return Pagination.buildMap(total,customerList);
    }

    @RequestMapping("add")
    @ResponseBody
    public Result add(CustomerVo customerVo) throws Exception{
        Result result = null;
        logger.debug("【customerVo=】" + customerVo);
        try{
            crmService.add(customerVo);
            return Result.success();
        }catch (Exception e){
            logger.error(e.toString(),e);
            return Result.error();
        }
    }

    @RequestMapping("getById")
    @ResponseBody
    public CustomerVo getById(Integer id){
        logger.debug("【id=】" + id);
        CustomerVo customerVo = crmService.getCustomerVoById(id);
        return customerVo;
    }

    @RequestMapping("update")
    @ResponseBody
    public Result update(CustomerVo customerVo) throws Exception{
        Result result = Result.error();
        logger.debug("【customerVo=】" + customerVo);
        try{
            int count = crmService.update(customerVo);
            if(count > 0){
                result = Result.success();
            }
        }catch (Exception e){
            logger.error(e.toString(),e);
        }
        return result;
    }

    @RequestMapping("delete")
    @ResponseBody
    public Result delete(Integer[] idArr,String[] idCardArr) throws Exception{
        Result result = null;
        logger.debug("【idArr=】" + Arrays.toString(idArr) +
                "，【idCardArr=】" + Arrays.toString(idCardArr));
        try{
            crmService.deleteBatch(idArr,idCardArr);
            return Result.success();
        }catch (Exception e){
            logger.error(e.toString(),e);
            return Result.error();
        }
    }
}

