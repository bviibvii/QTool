package com.hicore.ReflectUtils;

import com.hicore.Utils.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class MMethod {
    public static <T> T CallMethod(Object obj,Class ReturnType,Class[] ParamTypes,Object... params) throws Exception{
        Method method = FindMethod(obj.getClass(),null,ReturnType,ParamTypes);
        Assert.notNull(ReturnType,"ReturnType can't be null");
        if (method == null){
            StringBuilder builder = new StringBuilder();
            for (Class clzErr : ParamTypes)builder.append(clzErr.getName()).append(";");
            builder.append(")").append(ReturnType.getName());
            throw new NoMethodError("No Such Method "+builder+" in class "+obj.getClass().getName());
        }
        return (T) method.invoke(obj,params);
    }
    public static <T> T CallMethod(Object obj,String MethodName,Class ReturnType,Class[] ParamTypes,Object... params) throws Exception {
        Assert.notNull(obj,"obj can't be null when ignore the clz");
        return CallMethod(obj,obj.getClass(), MethodName, ReturnType, ParamTypes, params);
    }
    public static <T> T CallMethod(Object obj,Class clz,String MethodName,Class ReturnType,Class[] ParamTypes,Object... params) throws Exception {
        Method method = FindMethod(clz,MethodName,ReturnType,ParamTypes);
        Assert.notNull(clz,"clz can't be null");
        Assert.notNull(ReturnType,"ReturnType can't be null");
        if (method == null){
            StringBuilder builder = new StringBuilder();

            builder.append(MethodName).append("(");
            for (Class clzErr : ParamTypes)builder.append(clzErr.getName()).append(";");
            builder.append(")").append(ReturnType.getName());
            throw new NoMethodError("No Such Method "+builder+" in class "+clz.getName());
        }
        return (T) method.invoke(obj,params);
    }
    private static HashMap<String,Method> MethodCache = new HashMap<>();
    public static Method FindMethod(Class FindClass,String MethodName,Class ReturnType,Class[] ParamTypes){
        StringBuilder builder = new StringBuilder();
        builder.append(FindClass.getClass().getName()).append(".").append(MethodName).append("(");
        for (Class clz : ParamTypes)builder.append(clz.getName()).append(";");
        builder.append(")").append(ReturnType.getName());
        String SignText = builder.toString();
        if (MethodCache.containsKey(SignText))return MethodCache.get(SignText);

        Class Current_Find = FindClass;
        while (Current_Find != null){
            Loop:
            for(Method method : Current_Find.getDeclaredMethods()){
                if ((method.getName().equals(MethodName) || MethodName == null) && method.getReturnType().equals(ReturnType)){
                    Class[] params = method.getParameterTypes();

                    if (params.length == ParamTypes.length){
                        for (int i=0;i< params.length;i++){
                            if (!MClass.CheckClass(params[i],ParamTypes[i]))continue Loop;
                        }
                        MethodCache.put(SignText,method);
                        method.setAccessible(true);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();
        }
        return null;
    }
    private static class NoMethodError extends RuntimeException{
        public NoMethodError(String message) {
            super(message);
        }
    }
}