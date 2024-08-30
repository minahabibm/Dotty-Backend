package com.tradingbot.dotty.utils.webSockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.UserOrderDTO;
import com.tradingbot.dotty.models.dto.websockets.AlpacaWebsocketMessageDTO;
import com.tradingbot.dotty.service.UserOrdersService;
import com.tradingbot.dotty.utils.ExternalAPi.AlpacaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.tradingbot.dotty.utils.Utils.getAveragePrice;
import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;
import static com.tradingbot.dotty.utils.constants.alpaca.AlpacaConstants.*;

@Slf4j
@Service
public class AlpacaWebSocket {

    @Value("${alpaca-api.websocket-base-url}")
    private String baseUrlAlpacaWS;
    @Value("${alpaca-api.websocket-paper-base-url}")
    private String baseUrlAlpacaPaperWS;

    @Autowired
    private UserOrdersService userOrdersService;

    @Autowired
    private AlpacaUtil alpacaUtil;

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
                log.debug(ALPACA_WEBSOCKET_CONNECTION_STARTED, session.getId());

                Map<String, String> messageMap = Map.of(
                        "action", "auth",
                        "key", userConfigurationDTO.getAlpacaApiKey(),
                        "secret", userConfigurationDTO.getAlpacaSecretKey()
                );
                String jsonMessage = objectMapper.writeValueAsString(messageMap);
                session.sendMessage(new TextMessage(jsonMessage));
                log.debug(ALPACA_WEBSOCKET_SENT_MESSAGE, session.getId(), jsonMessage);

                webSocketSessions.put(accountId, session);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                try {
                    String payload = extractPayload(message, session.getId());
                    AlpacaWebsocketMessageDTO alpacaWebsocketMessageDTO = objectMapper.readValue(payload, AlpacaWebsocketMessageDTO.class);
                    handleWebSocketMessage(session, alpacaWebsocketMessageDTO, accountId, userConfigurationDTO);
                } catch (Exception e) {
                    log.warn(ALPACA_WEBSOCKET_MESSAGE_ERROR, session.getId(), e.getMessage());
                }
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.debug(ALPACA_WEBSOCKET_CONNECTION_ENDED, session.getId());
            }

            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };

        log.info(ALPACA_WEBSOCKET_INITIALIZATION);
        client.execute(handler, alpacaWebSocketURI.toString()).get();
    }

    private String extractPayload(WebSocketMessage<?> message, String sessionId) {
        String payload;
        if (message instanceof TextMessage) {
            payload = ((TextMessage) message).getPayload();
            log.debug(ALPACA_WEBSOCKET_TEXT_MESSAGE, sessionId, payload);
        } else if (message instanceof BinaryMessage) {
            ByteBuffer byteBuffer = ((BinaryMessage) message).getPayload();
            payload = new String(byteBuffer.array(), StandardCharsets.UTF_8);
            log.debug(ALPACA_WEBSOCKET_BINARY_MESSAGE, sessionId, payload);
        } else {
            throw new UnsupportedOperationException("Received unknown message type");
        }
        return payload;
    }

    private void handleWebSocketMessage(WebSocketSession session, AlpacaWebsocketMessageDTO alpacaWebsocketMessageDTO, String accountId, UserConfigurationDTO userConfigurationDTO) throws IOException {
        if(alpacaWebsocketMessageDTO != null) {
            log.info(ALPACA_WEBSOCKET_RECEIVED_MESSAGE, alpacaWebsocketMessageDTO.getStream(), session.getId(), alpacaWebsocketMessageDTO);
            if(ALPACA_AUTHORIZATION.equals(alpacaWebsocketMessageDTO.getStream())) {
                if(ALPACA_AUTHORIZED.equals(alpacaWebsocketMessageDTO.getData().getStatus())) {
                    Map<String, Object> messageMap = Map.of(
                            "action", "listen",
                            "data", Map.of("streams", new String[]{"trade_updates"})
                    );

                    String jsonMessage = objectMapper.writeValueAsString(messageMap);
                    session.sendMessage(new TextMessage(jsonMessage));
                    log.debug(ALPACA_WEBSOCKET_SENT_MESSAGE, session.getId(), jsonMessage);
                } else {
                    throw new RuntimeException("Unauthorized: Invalid key or secret. " + alpacaWebsocketMessageDTO.getData().getStatus());
                }
            } else if(ALPACA_TRADE_UPDATES.equals(alpacaWebsocketMessageDTO.getStream())) {
                String event = alpacaWebsocketMessageDTO.getData().getEvent();
                AlpacaWebsocketMessageDTO.TradeUpdatesData data = alpacaWebsocketMessageDTO.getData();
                switch (event) {
                    case ALPACA_NEW:
                        log.trace(ALPACA_WEBSOCKET_RECEIVED_MESSAGE_EVENT, session.getId(), event, data);
                        break;
                    case ALPACA_FILL:
                    case ALPACA_PARTIAL_FILL:
                    case ALPACA_CANCELED:
                    case ALPACA_EXPIRED:
                    case ALPACA_DONE_FOR_DAY:
                        log.trace(ALPACA_WEBSOCKET_RECEIVED_MESSAGE_EVENT, session.getId(), event, data);
                        handleOrderUpdates(accountId, userConfigurationDTO, data, event);
                        break;
                    case ALPACA_REPLACED:
                        log.trace(ALPACA_WEBSOCKET_RECEIVED_MESSAGE_EVENT, session.getId(), event, data);
                        break;
                    default:
                        log.trace(ALPACA_WEBSOCKET_RECEIVED_MESSAGE_EVENT, session.getId(), "Unhandled event: " + event, data);
                        break;
                }
            }
        }
    }

    private void handleOrderUpdates(String accountId, UserConfigurationDTO userConfigurationDTO, AlpacaWebsocketMessageDTO.TradeUpdatesData data, String event) throws IOException {
        log.trace(ALPACA_WEBSOCKET_ORDER_UPDATED, accountId, event, data.getOrder().getId());
        Optional<UserOrderDTO> userOrderDTO = userOrdersService.getUserOrder(data.getOrder().getId());
        userOrderDTO.ifPresent(userOrderDto -> {
            double price = Double.parseDouble(data.getPrice());
            double position_qty = Double.parseDouble(data.getPosition_qty());
            if(ALPACA_FILL.equals(event) || (position_qty <= 0 || price <= 0)) {
                userOrderDto.setFilledAvgPrice(price);
                userOrderDto.setFilledQty(Math.abs(position_qty));
            } else  if(ALPACA_PARTIAL_FILL.equals(event)) {
                double totalCost = (userOrderDto.getFilledQty() * userOrderDto.getFilledAvgPrice()) + (Math.abs(position_qty - userOrderDto.getFilledQty()) * price);
                userOrderDto.setFilledAvgPrice(getAveragePrice(totalCost, position_qty));
                userOrderDto.setFilledQty(Math.abs(position_qty));
            }
            userOrderDto.setFilled(event);
            userOrdersService.updateUserOrder(userOrderDto);
        });

        if(alpacaUtil.getAllPositions(userConfigurationDTO).length == 0)
            removeAccount(accountId);
    }

    public boolean isWebSessionActive(String accountId) {
        boolean isActive = webSocketSessions.containsKey(accountId);
        log.debug(ALPACA_WEBSOCKET_ACTIVE_SESSION, accountId, isActive);
        return isActive;
    }

    public void removeAccount(String accountId) throws IOException {
        WebSocketSession clientSession =  webSocketSessions.get(accountId);
        if (clientSession != null) {
            try {
                log.debug(ALPACA_WEBSOCKET_SESSION_HALT, accountId);
                clientSession.close();
                webSocketSessions.remove(accountId);
                log.trace(ALPACA_WEBSOCKET_SESSION_ACCOUNT_UPDATE, accountId);
            } catch (Exception e) {
                log.error(ALPACA_WEBSOCKET_SESSION_HALT_ERROR, accountId, e);
                throw new IOException(e);
            }
        }
    }
}

