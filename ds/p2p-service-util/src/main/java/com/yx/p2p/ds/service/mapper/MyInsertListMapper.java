package com.yx.p2p.ds.service.mapper;


import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.provider.SpecialProvider;

import java.util.List;

/**
 * @description:批量插入可以返回主键值
 * tk.mybatis.mapper.additional.insert.InsertListMapper包下的insertList()方法：
该方法不支持主键策略，需要在实体类通过指定主键。该方法执行后不会回写实体类的主键值
自定义新接口：MyInsertListMapper，
使用@InsertProvider(type = SpecialProvider.class, method = "dynamicSQL")可以返回主键值

 * @author: yx
 * @date: 2020/05/12/15:31
 */
public interface MyInsertListMapper<T> extends InsertListMapper<T> {

    @Options(keyProperty = "id",useGeneratedKeys = true)
    @InsertProvider(type = SpecialProvider.class, method = "dynamicSQL")
    int insertList(List<? extends T> recordList);
}
