package com.tradingbot.dotty.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.dotty.models.dto.TickersUpdateWSMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

@Slf4j(topic = "Dotty_Ticker_Trades_WebSockets")
@Service
public class TickerUpdatesWebSocket {

    @Lazy
    @Autowired
    private ConcurrentMarketDataFunnel concurrentMarketDataFunnel;

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
                    log.info("WebSocket Connection Established :: {}", session.getId());
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    TickersUpdateWSMessage messageContent = objectMapper.readValue(message.getPayload().toString(), TickersUpdateWSMessage.class);

                    log.info("WebSocket Message Received :: type: {}", messageContent.getType());
                    if(messageContent.getData() != null)
                        concurrentMarketDataFunnel.processTickerMarketTradeUpdates(messageContent.getData());
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                    log.info("WebSocket Connection Aborted :: {}", session.getId());
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

    public void subscribeToTickersTradesUpdate(String ticker) {
        try {
            log.info("Trades Update ::Subscribe Ticker:: {}", ticker);
            WebSocketSession tickerUpdatesWSSession = getTickerUpdatesWebSocket();
            tickerUpdatesWSSession.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"" + ticker + "\"}"));
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unsubscribeToTickersTradesUpdate(String ticker) {
        try {
            log.info("Trades Update ::Unsubscribe Ticker:: {}", ticker);
            WebSocketSession tickerUpdatesWSSession = getTickerUpdatesWebSocket();
            tickerUpdatesWSSession.sendMessage(new TextMessage("{\"type\":\"unsubscribe\",\"symbol\":\"" + ticker + "\"}"));
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeSession() {
        try {
            log.info("Closing  WebSocket session.");
            WebSocketSession tickerUpdatesWSSession = getTickerUpdatesWebSocket();
            tickerUpdatesWSSession.close();
            System.out.println(tickerUpdatesWSSession.isOpen());
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
