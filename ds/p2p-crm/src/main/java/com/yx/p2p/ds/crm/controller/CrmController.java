package com.yx.p2p.ds.crm.controller;

import com.yx.p2p.ds.easyui.Pagination;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.Crm;
import com.yx.p2p.ds.service.CrmService;
import com.yx.p2p.ds.vo.CrmVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping("init1")
    public String init1(){
        return "init1";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> list(Integer page, Integer rows, CrmVo crmVo){
        logger.debug("【page=" + page + ",rows=】" + rows + "【,crmVo=】" + crmVo);
        List<Crm> crmList = crmService.getCrmListByPagination(crmVo, page, rows);
        int total = crmService.queryCrmCount(crmVo);
        return Pagination.buildMap(total,crmList);
    }

    @RequestMapping("add")
    @ResponseBody
    public Result add(CrmVo crmVo) throws Exception{
        Result result = null;
        logger.debug("【crmVo=】" + crmVo);
        try{
            crmService.add(crmVo);
            return Result.success();
        }catch (Exception e){
            logger.error(e.toString(),e);
            return Result.error();
        }
    }

    @RequestMapping("getById")
    @ResponseBody
    public Crm getById(Integer id){
        logger.debug("【id=】" + id);
        CrmVo crmVo = crmService.getCrmVoById(id);
        return crmVo;
    }
}

