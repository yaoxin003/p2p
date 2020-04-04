package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Pagination;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.Crm;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.vo.CrmVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        return "init";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(Integer page, Integer rows, CrmVo crmVo){
        //查询条件判断
        String idCard = crmVo.getIdCard();
        List<Crm> crmList = null;
        if(StringUtils.isNotBlank(idCard)){
            logger.debug("【search pagination】");
            crmList = crmServer.search(crmVo,page,rows);
            return Pagination.buildMap(crmList.size(),crmList);
        }else{
            logger.debug("【search empty】");
            crmList = new ArrayList<>();
            return Pagination.buildMap(0,crmList);
        }
    }

    /**
     * 添加客户
     * 调用CRM的dubbo服务
     * @param crmVo
     * @return
     * @throws Exception
     */
    @RequestMapping("add")
    @ResponseBody
    public Result add(CrmVo crmVo) throws Exception{
        Result result = Result.error();
        logger.debug("【crmVo=】" + crmVo);
        try{
            Integer res = crmServer.addCrm(crmVo);
            logger.debug("【res=】" + res);
            if(res == 1){
                result = Result.success();
            }
        }catch (Exception e){
            logger.error(e.toString(),e);
        }
        return result;
    }


}

