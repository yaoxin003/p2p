package com.yx.p2p.ds.easyui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: Easyui的分页工具栏
 * @author: yx
 * @date: 2020/04/01/6:44
 */
public class Pagination<T> {

    /*
        * @description: 数量，列表内容
        * @author:  YX
        * @date:    2020/04/01 7:02
        * @param: total 数量
        * @param: list 列表内容
        * @return: java.util.Map<java.lang.String,java.lang.Object>
        */
    public static <T>  Map<String,Object> buildMap(int total, List<T> list){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("total",total);
        map.put("rows",list);
        return map;
    }

}
