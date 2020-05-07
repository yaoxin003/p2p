package com.yx.p2p.ds.util;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/29/13:38
 */
public class TestUtil {

    //测试private方法
    public  static <T> Object invokePrivateMethodParms(T target,String methodName,
               Class<?>[] methodParamClass,Object[] methodParamObj){
        Object invoke = null;
        // 获取class
        Class clazz = target.getClass();
        Method method = null;
        try {
            System.out.println("clazz=" + clazz.getName());
            Method[] declaredMethods = clazz.getDeclaredMethods();
            System.out.println("declaredMethods=");
            for(Method m : declaredMethods){
                System.out.print(m.getName() + ";==");
            }

            method = clazz.getDeclaredMethod(methodName, methodParamClass);
            System.out.println("method=" + method==null ? method : method.getName());
            method.setAccessible(true);
            invoke = method.invoke(target, methodParamObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoke;
    }
}
