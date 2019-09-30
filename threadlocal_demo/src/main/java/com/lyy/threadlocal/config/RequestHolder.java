package com.lyy.threadlocal.config;

public class RequestHolder {

    private final static ThreadLocal<Long> requestHolder = new ThreadLocal<>();//

    //提供方法传递数据
    public static void add(Long id){
        requestHolder.set(id);

    }

    public static Long getId(){
        //传入了当前线程的ID，到底层Map里面去取
        return requestHolder.get();
    }

    //移除变量信息，否则会造成逸出，导致内容永远不会释放掉
    public static void remove(){
        requestHolder.remove();
    }
}
