package com.yx.p2p.ds.util;

import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/29/16:57
 */
public class DateUtilTest {

    public static void main(String[] args) {
        Date d = DateUtil.str2Date("2020-04-30");
        d =DateUtil.addMonth(d,34);
        System.out.print(DateUtil.dateYMD2Str(d));
    }
}
