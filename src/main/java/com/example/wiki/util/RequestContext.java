package com.example.wiki.util;

import java.io.Serializable;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/10/17 13:49
 */
public class RequestContext implements Serializable {

    private static ThreadLocal<String> remoteAddr = new ThreadLocal<>();

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }

}