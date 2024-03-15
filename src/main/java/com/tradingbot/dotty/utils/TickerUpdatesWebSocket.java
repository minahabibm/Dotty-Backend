package com.tradingbot.dotty.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.dotty.models.dto.TickersUpdateWSMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;

@Slf4j(topic = "Dotty_Ticker_Trades_WebSockets")
@Service
public class TickerUpdatesWebSocket {

    @Value("${tickers-trades-api.websocket-base-url}")
    private String baseUrlTickerTradesWS;
    @Value("${tickers-trades-api.APIkey}")
    private String APIkeyTickerTradesWS;

    private URI TickerTradesURI;
    private  WebSocketClient client;
    private  WebSocketHandler handler;
    private WebSocketSession session;

    public WebSocketSession getTickerUpdatesWebSocket() throws ExecutionException, InterruptedException {
        if(session == null) {
            log.info("WebSocket Session initialization");
            TickerTradesURI = URI.create(baseUrlTickerTradesWS+APIkeyTickerTradesWS);
            client = new StandardWebSocketClient();
            handler = new WebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    log.info("Connection Established.");
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    TickersUpdateWSMessage messageContent = objectMapper.readValue(message.getPayload().toString(), TickersUpdateWSMessage.class);

                    log.info("type: {}", messageContent.getType());
                    if(messageContent.getData() != null)
                        messageContent.getData().stream().forEach(x -> log.info("{} {} {} {}", x.getS(), x.getP(), x.getV(), Instant.ofEpochMilli(x.getT()).atZone(ZoneId.systemDefault()).toLocalDateTime())); // ZoneId.of("America/New_York")
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                    log.info("Connection Aborted");
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            };
            session = client.execute(handler, TickerTradesURI.toString()).get();
        } else if(!session.isOpen()) {
            log.info("WebSocket Session Re-initialization");
            session = client.execute(handler, TickerTradesURI.toString()).get();
        }
        return session;
    }

}
