package com.yx.p2p.ds.util;

import com.yx.p2p.ds.constant.SysConstant;

import java.math.BigDecimal;
import java.text.Bidi;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/10/15:51
 */
public class BigDecimalUtil {
    //保留2位四舍五入
    public static BigDecimal round2In45(BigDecimal param){
        return param.setScale(SysConstant.BIGDECIMAL_DIVIDE_ROUNDMODE_2, BigDecimal.ROUND_HALF_UP);
    }

    //保留4位四舍五入
    public static BigDecimal round4In45(BigDecimal param){
        return param.setScale(SysConstant.BIGDECIMAL_DIVIDE_ROUNDMODE_4, BigDecimal.ROUND_HALF_UP);
    }

    //保留13位四舍五入
    public static BigDecimal round13In45(BigDecimal param){
        return param.setScale(SysConstant.BIGDECIMAL_DIVIDE_ROUNDMODE_13, BigDecimal.ROUND_HALF_UP);
    }

    //除法保留2位四舍五入
    public static BigDecimal divide2(BigDecimal data1,BigDecimal data2){
        return data1.divide(data2,
                SysConstant.BIGDECIMAL_DIVIDE_ROUNDMODE_2,BigDecimal.ROUND_HALF_UP);
    }

    //除法保留4位四舍五入
    public static BigDecimal divide4(BigDecimal data1,BigDecimal data2){
        return data1.divide(data2,
                SysConstant.BIGDECIMAL_DIVIDE_ROUNDMODE_4,BigDecimal.ROUND_HALF_UP);
    }
}
