package com.yx.p2p.ds.util;

import com.yx.p2p.ds.constant.SysConstant;

import java.math.BigDecimal;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/10/15:51
 */
public class BigDecimalUtil {
    //保留两位四舍五入
    public static BigDecimal round2In45(BigDecimal param){
        return param.setScale(SysConstant.BIGDECIMAL_DIVIDE_ROUNDMODE, BigDecimal.ROUND_HALF_UP);
    }


}
