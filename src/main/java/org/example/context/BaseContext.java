package org.example.context;


public class BaseContext {
    private static ThreadLocal<Integer> threadLocal=new ThreadLocal<>();

    public static Integer get(){
        return threadLocal.get();
    }

    public static void set(Integer userId){
        threadLocal.set(userId);
    }

    public static void remove(){
        threadLocal.remove();
    }

}
