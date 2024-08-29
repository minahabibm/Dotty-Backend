package com.tradingbot.dotty.utils.webSockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.websockets.AlpacaWebsocketMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.WEBSOCKET_CONNECTION_ENDED;
import static com.tradingbot.dotty.utils.constants.LoggingConstants.WEBSOCKET_CONNECTION_STARTED;

@Slf4j
@Service
public class AlpacaWebSocket {

    @Value("${alpaca-api.websocket-base-url}")
    private String baseUrlAlpacaWS;
    @Value("${alpaca-api.websocket-paper-base-url}")
    private String baseUrlAlpacaPaperWS;

    private final Map<String, WebSocketSession> webSocketSessions;
    private final ObjectMapper objectMapper;


    public AlpacaWebSocket() {
        this.webSocketSessions = new HashMap<>();
        this.objectMapper = new ObjectMapper();
    }

    public void addAccountWebSocket(String accountId, UserConfigurationDTO userConfigurationDTO) throws ExecutionException, InterruptedException {
        URI alpacaWebSocketURI = URI.create(userConfigurationDTO.getAlpacaPaperAccount() ? baseUrlAlpacaPaperWS : baseUrlAlpacaWS);
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHandler handler = new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.debug(WEBSOCKET_CONNECTION_STARTED, session.getId());

                Map<String, String> messageMap = Map.of(
                        "action", "auth",
                        "key", userConfigurationDTO.getAlpacaApiKey(),
                        "secret", userConfigurationDTO.getAlpacaSecretKey()
                );
                String jsonMessage = objectMapper.writeValueAsString(messageMap);
                session.sendMessage(new TextMessage(jsonMessage));

                webSocketSessions.put(accountId, session);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                try {
                    String payload = extractPayload(message);
                    AlpacaWebsocketMessageDTO alpacaWebsocketMessageDTO = objectMapper.readValue(payload, AlpacaWebsocketMessageDTO.class);
                    handleWebSocketMessage(session, alpacaWebsocketMessageDTO);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.debug(WEBSOCKET_CONNECTION_ENDED, session.getId());
            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };

        // Initialize a session
        client.execute(handler, alpacaWebSocketURI.toString()).get();
    }

    private String extractPayload(WebSocketMessage<?> message) {
        String payload;
        if (message instanceof TextMessage) {
            payload = ((TextMessage) message).getPayload();
        } else if (message instanceof BinaryMessage) {
            ByteBuffer byteBuffer = ((BinaryMessage) message).getPayload();
            payload = new String(byteBuffer.array(), StandardCharsets.UTF_8);
        } else {
            throw new UnsupportedOperationException("Received unknown message type");
        }
        return payload;
    }

    private void handleWebSocketMessage(WebSocketSession session, AlpacaWebsocketMessageDTO alpacaWebsocketMessageDTO) throws IOException {
        if(alpacaWebsocketMessageDTO != null) {
            if("authorization".equals(alpacaWebsocketMessageDTO.getStream())) {
                if("authorized".equals(alpacaWebsocketMessageDTO.getData().getStatus())) {
                    Map<String, Object> messageMap = Map.of(
                            "action", "listen",
                            "data", Map.of("streams", new String[]{"trade_updates"})
                    );

                    String jsonMessage = objectMapper.writeValueAsString(messageMap);
                    session.sendMessage(new TextMessage(jsonMessage));
                } else {
                    throw new RuntimeException("Unauthorized: Invalid key or secret. " + alpacaWebsocketMessageDTO.getData().getStatus());
                }

            } else if("trade_updates".equals(alpacaWebsocketMessageDTO.getStream())) {
                System.out.println("alpaca trade_updates " + alpacaWebsocketMessageDTO);
            } else {
                System.out.println("alpaca WS " + alpacaWebsocketMessageDTO);
            }
        }
    }

    public boolean isWebSessionActive(String accountId) {
        return webSocketSessions.containsKey(accountId);
    }

    public void removeAccount(String accountId) throws IOException {
        WebSocketSession clientSession =  webSocketSessions.get(accountId);
        if (clientSession != null) {
            clientSession.close();
            webSocketSessions.remove(accountId);
        }
    }
}

