package com.yx.p2p.ds.investsale.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Pagination;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.Crm;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.vo.CrmVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
            Integer res = crmServer.add(crmVo);
            logger.debug("【res=】" + res);
            if(res == 1){
                result = Result.success();
            }
        }catch (Exception e){
            logger.error(e.toString(),e);
        }
        return result;
    }

    /**
     * 修改显示页面
     * @param idCard
     * @return
     */
    @RequestMapping("getByIdCard")
    @ResponseBody
    public CrmVo getByIdCard(String idCard){
        logger.debug("【idCard=】" + idCard);
        CrmVo crmVo = new CrmVo();
        crmVo.setIdCard(idCard);
        //临时方案---begin
        List<Crm> crmList = crmServer.search(crmVo,1,1);
        Crm resCrm = crmList.get(0);
        //3.封装前台对象
        //3.1拷贝属性
        CrmVo resCrmVo = new CrmVo();
        try {
            BeanUtils.copyProperties(resCrmVo,resCrm);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.2特殊值设置
        String birthdayStr = DateUtil.date2Str(resCrm.getBirthday());
        resCrmVo.setBirthdayStr(birthdayStr);
        resCrmVo.setIdCardOld(resCrm.getIdCard());
        //临时方案---end
        return resCrmVo;
    }

    @RequestMapping("update")
    @ResponseBody
    public Result update(CrmVo crmVo) throws Exception{
        Result result = null;
        logger.debug("【crmVo=】" + crmVo);
        try{
            crmServer.update(crmVo);
            return Result.success();
        }catch (Exception e){
            logger.error(e.toString(),e);
            return Result.error();
        }
    }
}

