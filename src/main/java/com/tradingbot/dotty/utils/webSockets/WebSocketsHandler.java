package com.tradingbot.dotty.utils.webSockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.dotty.models.dto.websockets.WebsocketMessageDTO;
import com.tradingbot.dotty.service.handler.WebSocketService;
import com.tradingbot.dotty.utils.constants.WebSockets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

import static com.tradingbot.dotty.utils.constants.Constants.*;

@Service
public class WebSocketsHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebSocketService webSocketService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        webSocketService.addSession(session);
        System.out.println("Websocket connection opened, with session id "  + session.getId());

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map messageContent = objectMapper.readValue(message.getPayload(), Map.class);
            String type = messageContent.get(WEBSOCKET_SUBSCRIPTION_TYPE).toString();
            String topic = (String) messageContent.computeIfPresent(WEBSOCKET_SUBSCRIPTION_TOPIC, (k, v) -> v.toString());
            String msg = (String) messageContent.computeIfPresent(WEBSOCKET_SUBSCRIPTION_MESSAGE, (k, v) -> v.toString());

            WebSockets command = WebSockets.fromString(type);
            switch (command) {
                case SUBSCRIBE:
                    webSocketService.subscribe(session, topic);
                    break;
                case UNSUBSCRIBE:
                    webSocketService.unsubscribe(session, topic);
                    break;
                default:
                    WebsocketMessageDTO errorMessage = WebsocketMessageDTO.builder().type("error").message("Unknown command").build();
                    webSocketService.sendMessageToSession(session, errorMessage.toString());
            }
        } catch (Exception e) {
            WebsocketMessageDTO errorMessage = WebsocketMessageDTO.builder().type("error").message("Error processing message").build();
            webSocketService.sendMessageToSession(session, errorMessage.toString());
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        webSocketService.removeSession(session);
        System.out.println("Websocket connection closed, for session id" + session.getId());
    }


}

//                    case "broadcast":
//                        webSocketService.broadcast(topic, msg, session);
//                        break;
//                    case "ping":
//                        handlePing(session);
//                        break;

/* @Service
public class WebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public WSMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String subscriptionId = accessor.getSubscriptionId();
        String destination = accessor.getDestination();
        // Perform actions when a subscription is initiated
        System.out.println("Subscription event - Subscription ID: " + subscriptionId + ", Destination: " + destination);
        messagingTemplate.convertAndSend(destination, "Welcome to the public channel!");
    }

    public void sendMessageToPublicChannel(String message) {
        // Send message to public channel
        System.out.println("Sending a message.");
        messagingTemplate.convertAndSend("/topic/public", message);
    }

    public void sendMessageToUser(String username, String message) {
        // Send message to specific user's private channel
        messagingTemplate.convertAndSendToUser(username, "/queue/private-messages", message);
    }

}

 */