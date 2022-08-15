package com.mkn.common;

/**
 * @author ：mkn
 * @date ：Created in 2022/8/8 9:08
 * @description：基于ThreadLocal封装的工具类,用于保存和获取当前用户的id
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
