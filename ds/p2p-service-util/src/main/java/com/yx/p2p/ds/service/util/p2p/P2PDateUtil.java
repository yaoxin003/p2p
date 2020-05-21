package com.yx.p2p.ds.service.util.p2p;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/16/18:41
 */
public class P2PDateUtil {


    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final int SCALE = 20;//精度


    public static int diff(Date date) throws Exception {
        return diff(new Date(), date);
    }

    /**
     * 日期差天数(按照时间比较,如果不足一天会自动补足)
     * @param date1 开始日期
     * @param date2 结束日期
     * @return 两日期差天数
     */
    public static int diff(Date date1, Date date2) throws Exception {
        long day = 24L * 60L * 60L * 1000L;
        String str1=date2Str(date1, "yyyy-MM-dd");
        date1=str2Date(str1, "yyyy-MM-dd");
        String str2=date2Str(date2, "yyyy-MM-dd");
        date2=str2Date(str2, "yyyy-MM-dd");

        return (int) (((date2.getTime() - date1.getTime()) /day));
        //return (int) Math.ceil((((date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000d))));
    }


    public static BigDecimal round(double src, int scale){
        BigDecimal rtn = new BigDecimal(
                new Double(
                        Arith.round(src, scale)
                ).toString()
        );
        return rtn;
    }


    public static Date str2Date(String dateStr, String pattern) throws Exception{
        if(null==dateStr) {
            return null;
        }
        if(null==pattern) {
            pattern = DEFAULT_DATE_FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(pattern);
        return format.parse(dateStr);
    }

    public static String date2Str(Date date, String pattern){
        if(null==date) {
            return null;
        }
        if(null==pattern) {
            pattern = DEFAULT_DATE_FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern(pattern);
        return format.format(date);
    }

    public static Date addDays(Date date,int addDay){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, addDay);
        return cal.getTime();
    }
}
