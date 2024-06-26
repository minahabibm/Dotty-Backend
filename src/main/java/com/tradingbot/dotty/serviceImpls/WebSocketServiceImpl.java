package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.dto.WebsocketMessageDTO;
import com.tradingbot.dotty.service.WebSocketService;
import com.tradingbot.dotty.utils.WebSockets;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.tradingbot.dotty.utils.Constants.*;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<WebSocketSession>> publicChannelSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, Set<Map<String, String>>> privateChannelSubscriptions = new ConcurrentHashMap<>();

    @Override
    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
        for (Set<WebSocketSession> sessions : publicChannelSubscriptions.values())
            sessions.remove(session);
        privateChannelSubscriptions.values().forEach(userSessions -> userSessions.removeIf(map -> map.get(WEBSOCKET_SUBSCRIPTION_SESSION_ID).equals(session.getId())));
    }

    @Override
    public void subscribe(WebSocketSession session, String channel) throws IOException {
         if (channel.startsWith(WebSockets.SUBSCRIBE.topic)) {
            publicChannelSubscriptions.computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>()).add(session);
             WebsocketMessageDTO message = WebsocketMessageDTO.builder().type("system").message("Subscribed to " + channel).build();
            sendMessageToSession(session, message.toString());
        } else if (channel.startsWith(WebSockets.SUBSCRIBE.queue)) {
            String userId = (String) session.getAttributes().get(WEBSOCKET_SESSION_ATTRIBUTE_USER);
            if (userId != null) {
                Map<String, String> userSessionMap = Map.of(WEBSOCKET_SUBSCRIPTION_USER_ID, userId, WEBSOCKET_SUBSCRIPTION_SESSION_ID, session.getId());
                privateChannelSubscriptions.computeIfAbsent(channel, k -> new CopyOnWriteArraySet<>()).add(userSessionMap);
                WebsocketMessageDTO message = WebsocketMessageDTO.builder().type("system").message("Subscribed to " + channel ).build();
                sendMessageToSession(session, message.toString());
            } else {
                WebsocketMessageDTO message = WebsocketMessageDTO.builder().type("error").message("Authentication required for private channels").build();
                sendMessageToSession(session, message.toString());
            }
        }
    }

    @Override
    public void unsubscribe(WebSocketSession session, String channel) throws IOException {
        if (channel.startsWith(WebSockets.UNSUBSCRIBE.topic)) {
            Set<WebSocketSession> sessions = publicChannelSubscriptions.get(channel);
            if (sessions != null) {
                sessions.remove(session);
                WebsocketMessageDTO message = WebsocketMessageDTO.builder().type("system").message("Unsubscribed from " + channel).build();
                sendMessageToSession(session, message.toString());
            }
        } else if (channel.startsWith(WebSockets.UNSUBSCRIBE.queue)) {
            Set<Map<String, String>> userSessions = privateChannelSubscriptions.get(channel);
            if (userSessions != null) {
                String userId = (String) session.getAttributes().get(WEBSOCKET_SESSION_ATTRIBUTE_USER);
                if (userId != null) {
                    userSessions.removeIf(map -> map.get("userId").equals(userId));
                    WebsocketMessageDTO message = WebsocketMessageDTO.builder().type("system").message("Unsubscribed from " + channel).build();
                    sendMessageToSession(session, message.toString());
                }
            }
        }
    }

    // onClose sessions  remove all subscriptions
    @Override
    public void broadcast(String channel, String message) throws IOException {
        Set<WebSocketSession> sessions = publicChannelSubscriptions.get(channel);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                WebsocketMessageDTO resMessage = WebsocketMessageDTO.builder().type("broadcast").topic(channel).message(message).build();
                sendMessageToSession(session, resMessage.toString());
            }
        }
    }

    @Override
    public void broadcast(String channel, String userId, String message) throws IOException {
        Set<Map<String, String>> userSessions = privateChannelSubscriptions.get(channel);
        if (userSessions != null) {
            for (Map<String, String> userSession : userSessions) {
                String sessionId = userSession.get(WEBSOCKET_SUBSCRIPTION_SESSION_ID);
                String userID = userSession.get(WEBSOCKET_SUBSCRIPTION_USER_ID);
                WebSocketSession session = sessions.get(sessionId);
                if (session != null && session.isOpen() && userID.equals(userId)) {
                    WebsocketMessageDTO resMessage = WebsocketMessageDTO.builder().type("broadcast").topic(channel).message(message).build();
                    sendMessageToSession(session, resMessage.toString());
                }
            }
        }
    }

    @Override
    public void sendMessageToSession(WebSocketSession session, String message) throws IOException {
        if (session.isOpen())
            session.sendMessage(new TextMessage(message));
    }

}