package com.tradingbot.dotty.utils;

import static com.tradingbot.dotty.utils.LoggingConstants.*;

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


@Slf4j
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
        if (session == null) {
            log.info(WEBSOCKET_INITIALIZATION);
            TickerTradesURI = URI.create(baseUrlTickerTradesWS + APIkeyTickerTradesWS);
            client = new StandardWebSocketClient();
            handler = new WebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    log.debug(WEBSOCKET_CONNECTION_STARTED, session.getId());
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    TickersUpdateWSMessage messageContent = objectMapper.readValue(message.getPayload().toString(), TickersUpdateWSMessage.class);

                    log.debug(WEBSOCKET_MESSAGE_RECEIVED, messageContent.getType());
                    if (messageContent.getData() != null)
                        concurrentMarketDataFunnel.processTickerMarketTradeUpdates(messageContent.getData());
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
            session = client.execute(handler, TickerTradesURI.toString()).get();
        } else if (!session.isOpen()) {
            log.info(WEBSOCKET_REINITIALIZATION);
            session = client.execute(handler, TickerTradesURI.toString()).get();
        }
        return session;

    }

    public void subscribeToTickersTradesUpdate(String ticker) {
        log.info(WEBSOCKET_TRADES_TICKER_SUBSCRIBE, ticker);
        try {
            WebSocketSession tickerUpdatesWSSession = getTickerUpdatesWebSocket();
            synchronized (tickerUpdatesWSSession) {
                tickerUpdatesWSSession.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"" + ticker + "\"}"));
            }
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unsubscribeToTickersTradesUpdate(String ticker) {
        log.info(WEBSOCKET_TRADES_TICKER_UNSUBSCRIBE, ticker);
        try {
            WebSocketSession tickerUpdatesWSSession = getTickerUpdatesWebSocket();
            synchronized (tickerUpdatesWSSession) {
                tickerUpdatesWSSession.sendMessage(new TextMessage("{\"type\":\"unsubscribe\",\"symbol\":\"" + ticker + "\"}"));
            }
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeSession() {
        try {
            log.info(WEBSOCKET_ABORTING_SESSION);
            WebSocketSession tickerUpdatesWSSession = getTickerUpdatesWebSocket();
            tickerUpdatesWSSession.close();
//            System.out.println(tickerUpdatesWSSession.isOpen());
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
