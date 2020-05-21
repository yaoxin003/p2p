package com.yx.p2p.ds.util;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/20/12:18
 */
public class PageUtil {

    /*
        * @description: 获得页数
        * @author:  YX
        * @date:    2020/05/20 12:19
        * @param: total 记录总数
        * @param: pageSize 每页数量
        * @return: int 页数
        */
    public static int getPageCount(int total,int pageSize){
        int pageCount = total/pageSize;
        pageCount = total%pageSize != 0  ? ++pageCount : pageCount;
        return pageCount;
    }

}
