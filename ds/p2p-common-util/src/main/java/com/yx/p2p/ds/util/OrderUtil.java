package com.yx.p2p.ds.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/12/9:37
 */
public class OrderUtil {

    static Logger logger = LoggerFactory.getLogger(OrderUtil.class);

    public static String genOrderSn(String systemSource){
        String dateStr = DateUtil.datetimeMS2Str(new Date());
        long time = System.currentTimeMillis();
        String orderSn =  systemSource + dateStr +time;
        logger.debug("【orderSn=】"+ orderSn);
        return orderSn;
    }

    public static final String ORDERSN_PREFIX_DEBTDATEVALUE_DEBT_ADD = "debtDateValAdd";

    public static final String ORDERSN_PREFIX_DEBTDATEVALUE_RETURN_ARR = "debtDateValReturnArr";

    public static final String ORDERSN_PREFIX_LENDING = "lending";

    public static final String ORDERSN_PREFIX_INVEST_DEBT_VAL_ADD = "investDebtValAdd";

    public static final String ORDERSN_PREFIX_INVEST_DEBT_VAL_RETURN = "investDebtValReturn";

    public static final String ORDERSN_PREFIX_INVEST_RETURN = "investReturn";


    public static Integer getLendingId(String orderSn){
        Integer bizId = null;
        if(orderSn.startsWith(OrderUtil.ORDERSN_PREFIX_LENDING)){
            bizId = Integer.valueOf(orderSn.replace(OrderUtil.ORDERSN_PREFIX_LENDING,""));
        }else{
            bizId = Integer.valueOf(orderSn);
        }
        return bizId;
    }
}
