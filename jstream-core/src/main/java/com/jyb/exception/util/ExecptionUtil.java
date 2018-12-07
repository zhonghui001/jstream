package com.jyb.exception.util;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

public class ExecptionUtil {


    public static <T> T requireNonNull(T obj, String message) {
        return requireNonNull(obj,new RuntimeException(message));
    }

    public static <T> T requireNonNull(T obj, RuntimeException ex) {
        return require(obj, t -> {
            if (t == null)
                return true;
            else
                return false;
        }, ex);
    }

    public static <T> T requireNonNull(T obj) {
        return requireNonNull(obj, new NullPointerException());
    }

    public static String requireNotBlank(String obj,String msg){
        return requireNotBlank(obj,new RuntimeException(msg));
    }

    public static String requireNotBlank(String obj,RuntimeException ex){
        return require(obj,t->{
            return StringUtils.isBlank(t);
        },ex);
    }

    public static <T> T require(T obj, Predicate<T> pre, RuntimeException ex) {
        if (pre.test(obj)) {
            throw ex;
        }
        return obj;
    }
}
