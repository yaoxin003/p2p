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


    //获得债权和还款价值总和
    @Override
    public Map<String,BigDecimal> getSumDebtAndReturnByBorrowIdList(Date daily, List<Integer> borrowIdList) {
        logger.debug("【获得债权和还款价值总和】入参:daily=" + daily
                + ",borrowIdList=" + borrowIdList);
        Map<String,BigDecimal> sumMap = debtDateValueService.
                getSumDebtAndReturnByBorrowIdList(daily,borrowIdList);
        logger.debug("【获得债权和还款价值总和】结果sumMap=" + sumMap);
        return sumMap;
    }

    public List<DebtDateValue> queryDebtDateValuePageList(Date daily, Integer page, Integer rows){
        return debtDateValueService.queryDebtDateValuePageList(daily, page, rows);
    }

    public Integer queryDebtDateValuePageCount(Date daily){
        return debtDateValueService.queryDebtDateValuePageCount(daily);
    }
}
