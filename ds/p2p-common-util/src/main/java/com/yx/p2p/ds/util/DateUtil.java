package com.yx.p2p.ds.util;

import org.apache.commons.lang3.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/31/12:28
 */
public class DateUtil {
    private static final String ymdPattern = "yyyy-MM-dd";

    private static final String datetimeMSPattern = "yyyyMMddHHmmssSSS";

    public static Date date2DateYMD(Date date){
        String dateStr = dateYMD2Str(date);
        return str2Date(dateStr);
    }

    public static Date str2Date(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat(ymdPattern);
        Date date = null;
        if(StringUtils.isNotBlank(dateStr)){
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static String dateYMD2Str(Date date){
        return date2String(date,ymdPattern);
    }

    public static String datetimeMS2Str(Date date) {
        return date2String(date,datetimeMSPattern);
    }

    private static String date2String(Date date,String pattern){
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static Date addDay(Date date,int dayCount){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH,dayCount);
        return c.getTime();
    }

    public static Date addMonth(Date date, int monthCount){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONDAY,monthCount);
        return c.getTime();
    }

    //获得日
    public static int getDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        return day;
    }

    //设置日
    public static Date setDayOfDate(Date date,int day){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH,day);
        return c.getTime();
    }

}
