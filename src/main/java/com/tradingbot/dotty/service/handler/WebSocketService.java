package com.tradingbot.dotty.service.handler;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface WebSocketService {

    void addSession(WebSocketSession session);
    void removeSession(WebSocketSession session);
    void subscribe(WebSocketSession session, String channel) throws IOException;
    void unsubscribe(WebSocketSession session, String channel) throws IOException;
    void broadcast(String channel, String message) throws IOException;
    void broadcast(String channel, String message, String userId) throws IOException;
    void sendMessageToSession(WebSocketSession session, String message) throws IOException;

}
