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
}
