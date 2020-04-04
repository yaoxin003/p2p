package com.yx.p2p.ds.helper;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/31/16:28
 */
public class BeanHelper {

    public static<T> void setDefaultTimeField(T target,String ... filedNames){
        for(String filedName : filedNames){
            setField(target,filedName,new Date());
        }
    }

    public static<T> void setDefaultOperatorField(T target,Map<String,Integer> map){
        for( String key: map.keySet()){
            setField(target,key,map.get(key));
        }
    }

    /**
     * 使用反射给对象(父类的父类)赋值，例如：给crmVO.CRM.BaseModel的updateTime赋值
     */
    private static<T> void setField(T target,String filedName,Object filedValue){
        try {
            Class<? extends Object> clazz = target.getClass();
            Field field = clazz.getSuperclass().getSuperclass().getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(target,filedValue);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
