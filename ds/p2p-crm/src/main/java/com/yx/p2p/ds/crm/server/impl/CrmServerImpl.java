package com.yx.p2p.ds.crm.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.server.CrmServer;
import com.yx.p2p.ds.service.CrmService;
import com.yx.p2p.ds.vo.CrmVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/03/18:18
 */
//dubbo注解，暴露服务
@Service
@Component//dubbo需要@Component注解，否则无法识别该服务
public class CrmServerImpl implements CrmServer{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CrmService crmService;

    public Integer addCrm(CrmVo crmVo){
        Result result = null;
        logger.debug("【crmVo=】" + crmVo);
        try{
            return crmService.add(crmVo);
        }catch (Exception e){
            logger.error(e.toString(),e);
            return 0;
        }
    }
}