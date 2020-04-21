package com.yx.p2p.ds.helper;

import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.enums.base.BizStateEnum;
import com.yx.p2p.ds.enums.base.LogicStateEnum;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/31/16:28
 */
public class BeanHelper {

    //添加：设置时间，操作人，状态
    public static<T> void setAddDefaultField(T target){
        //1.1设置时间
        BeanHelper.setDefaultTimeField(target,"createTime","updateTime");
        //1.2.设置操作人
        Map<String,Integer> operatorMap = new HashMap<String,Integer>();
        operatorMap.put("creator", SysConstant.operator);
        operatorMap.put("reviser",SysConstant.operator);
        BeanHelper.setDefaultOperatorField(target,operatorMap);
        //1.3.设置状态
        Map<String,String> stateMap = new HashMap<String,String>();
        stateMap.put("logicState", LogicStateEnum.ENABLE.getState());
        stateMap.put("bizState", BizStateEnum.NEW_ADD.getState());
        BeanHelper.setDefaultStateField(target,stateMap);
    }

    //修改设置时间，操作人
    public static<T> void setUpdateDefaultField(T target){
        //1.1设置时间
        BeanHelper.setDefaultTimeField(target,"updateTime");
        //1.2.设置操作人
        Map<String,Integer> operatorMap = new HashMap<String,Integer>();
        operatorMap.put("reviser",SysConstant.operator);
        BeanHelper.setDefaultOperatorField(target,operatorMap);
    }

    //设置时间
    private static<T> void setDefaultTimeField(T target,String ... filedNames){
        for(String filedName : filedNames){
            setField(target,filedName,new Date());
        }
    }

    //设置操作人
    private static<T> void setDefaultOperatorField(T target,Map<String,Integer> map){
        for( String key: map.keySet()){
            setField(target,key,map.get(key));
        }
    }
    //设置状态
    private static<T> void setDefaultStateField(T target,Map<String,String> map){
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
            Field field = null;
            if(clazz.getPackage().getName().endsWith("vo")){
                field = clazz.getSuperclass().getSuperclass().getDeclaredField(filedName);
            }else if(clazz.getPackage().getName().endsWith("model")){
                field = clazz.getSuperclass().getDeclaredField(filedName);
            }else{
                field = clazz.getDeclaredField(filedName);
            }
            field.setAccessible(true);
            field.set(target,filedValue);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



}
