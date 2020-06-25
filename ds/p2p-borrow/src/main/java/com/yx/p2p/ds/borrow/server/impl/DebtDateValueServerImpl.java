package com.yx.p2p.ds.borrow.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.model.borrow.DebtDateValue;
import com.yx.p2p.ds.server.DebtDateValueServer;
import com.yx.p2p.ds.service.borrow.DebtDateValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/31/17:15
 */
@Service//dubbo注解，暴露服务
@Component//dubbo需要@Component注解，否则无法识别该服务
public class DebtDateValueServerImpl implements DebtDateValueServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DebtDateValueService debtDateValueService;


    public List<DebtDateValue> getDebtDateValuePageList(Date daily, Integer page, Integer rows){
        return debtDateValueService.queryDebtDateValuePageList(daily, page, rows);
    }

    public Integer getDebtDateValueCount(Date daily){
        return debtDateValueService.queryDebtDateValueCount(daily);
    }
}
