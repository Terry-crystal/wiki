package com.example.wiki.service;

import com.example.wiki.websocket.WebSocketServer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/10/17 20:36
 */

@Service    //记得加上注解
public class WsService {

    //这个类就单纯的用于发消息，实现了单一原则
    //之所以要拆开写，是因为使用Async注解会生成代理类，这样子就可以方便外界使用，不会出现要么异步无效要么在统一类内调用方法无效等错误

    @Resource
    public WebSocketServer webSocketServer;

    @Async //异步化注解
    public void sendInfo(String message) {
        webSocketServer.sendInfo(message);
    }

}
