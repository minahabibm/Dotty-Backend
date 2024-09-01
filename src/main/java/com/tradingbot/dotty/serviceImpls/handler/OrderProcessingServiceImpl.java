package com.tradingbot.dotty.serviceImpls.handler;

import com.tradingbot.dotty.models.dto.*;
import com.tradingbot.dotty.models.dto.requests.Alpaca.OrderRequest;
import com.tradingbot.dotty.models.dto.requests.Alpaca.OrderResponse;
import com.tradingbot.dotty.models.dto.requests.Alpaca.PositionResponse;
import com.tradingbot.dotty.service.*;
import com.tradingbot.dotty.service.handler.TickerMarketTradeService;
import com.tradingbot.dotty.service.handler.OrderProcessingService;
import com.tradingbot.dotty.utils.ExternalAPi.AlpacaUtil;
import com.tradingbot.dotty.utils.webSockets.AlpacaWebSocket;
import com.tradingbot.dotty.utils.webSockets.TickerUpdatesWebSocket;
import com.tradingbot.dotty.utils.constants.alpaca.TradeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;

//  TODO add message error details

@Slf4j
@Service
public class OrderProcessingServiceImpl implements OrderProcessingService {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UserConfigurationService userConfigurationService;

    @Autowired
    private UserOrdersService userOrdersService;

    @Autowired
    private UserHoldingService userHoldingService;

    @Autowired
    private TickerMarketTradeService tickerMarketTradeService;

    @Autowired
    private TickerUpdatesWebSocket tickerUpdatesWebSocket;

    @Autowired
    private AlpacaUtil alpacaUtil;

    @Autowired
    private AlpacaWebSocket alpacaWebSocket;


    @Override
    public Double getAvailableToTrade(UserConfigurationDTO userConfigurationDTO) {
        return Double.valueOf(alpacaUtil.getAccountDetails(userConfigurationDTO).getBuying_power());
    }

    @Override
    public Double getCurrentPrice() {
        return 0.0;
    }

    @Override
    public void enterPosition(String order) {
        String[] arr =  order.split(", ");
        String symbol = arr[1];
        String price = arr[2];
        String time = arr[3];

        log.info(PROCESSING_ORDER_TO_ENTER, symbol);
        Optional<OrdersDTO> ordersDTO = tickerMarketTradeService.enterPosition(symbol, Float.valueOf(price), time);
        tickerUpdatesWebSocket.subscribeToTickersTradesUpdate(symbol);

        if(ordersDTO.isPresent()) {
            userConfigurationService.getUsersConfigurationsWithActiveTradingAccounts().forEach(userConfigurationDTO -> {
                try {
                    Optional<UsersDTO> usersDTO = usersService.getUserByUserConfigurationId(userConfigurationDTO.getUserConfigurationId());
                    if(usersDTO.isPresent()) {
                        if(!alpacaWebSocket.isWebSessionActive(usersDTO.get().getLoginUid()))
                            alpacaWebSocket.addAccountWebSocket(usersDTO.get().getLoginUid(), userConfigurationDTO);

                        Double availableToTrade = getAvailableToTrade(userConfigurationDTO);
                        OrderRequest orderRequest = OrderRequest.builder()
                                .symbol(symbol)
                                .notional(String.valueOf(availableToTrade * 0.1))
                                .side(TradeConstants.fromValue(tickerMarketTradeService.getOrderType(true, ordersDTO.get().getPositionTrackerDTO().getTypeOfTrade())))
                                .type(TradeConstants.MARKET)
                                .time_in_force(TradeConstants.DAY)
                                .build();
                        OrderResponse orderResponse = alpacaUtil.createOrder(orderRequest, userConfigurationDTO);

                        UserOrderDTO userOrderDTO = new UserOrderDTO();
                        userOrderDTO.setAlpacaOrderId(orderResponse.getId());
                        userOrderDTO.setUsersDTO(usersDTO.get());
                        userOrderDTO.setOrdersDTO(ordersDTO.get());
                        userOrdersService.insertUserOrder(userOrderDTO);
                    }
                } catch (Exception e) {
                    log.error(PROCESSING_ORDER_ERROR,"to Open", userConfigurationDTO.getUserConfigurationId(), symbol, e.getMessage());
                }
            });
        }

    }

    // TODO Closed Order confirmation
    @Override
    public void exitPosition(String order) {
        String[] arr =  order.split(", ");
        String symbol = arr[1];
        String price = arr[2];
        String time = arr[3];

        log.info(PROCESSING_ORDER_TO_EXIT, symbol);

        Optional<OrdersDTO> ordersDTO = tickerMarketTradeService.closePosition(symbol, Float.valueOf(price), time);
        Optional<HoldingDTO> holdingDTO = tickerMarketTradeService.addToHolding(ordersDTO.get().getPositionTrackerDTO().getPositionTrackerId());
        tickerUpdatesWebSocket.unsubscribeToTickersTradesUpdate(symbol);

        if(holdingDTO.isPresent())  {
            userConfigurationService.getUsersConfigurationsWithActiveTradingAccounts().forEach(userConfigurationDTO -> {
                try {
                    // fetch user position for symbol if exists
                    PositionResponse positionResponse  = alpacaUtil.getAnOpenPosition(symbol, userConfigurationDTO);
                    if(positionResponse != null) {
                        OrderResponse orderResponse = alpacaUtil.closePosition(symbol, null, userConfigurationDTO);
                        System.out.println(orderResponse);

                        // Add to user Orders
                        Optional<UsersDTO> usersDTO = usersService.getUserByUserConfigurationId(userConfigurationDTO.getUserConfigurationId());
                        if(usersDTO.isPresent()) {
                            UserOrderDTO userOrderDTO = new UserOrderDTO();
                            userOrderDTO.setAlpacaOrderId(orderResponse.getId());
                            userOrderDTO.setUsersDTO(usersDTO.get());
                            userOrderDTO.setOrdersDTO(ordersDTO.get());
                            userOrdersService.insertUserOrder(userOrderDTO);

                            // add to user holding
                            UserHoldingDTO userHoldingDTO = new UserHoldingDTO();
                            userHoldingDTO.setUsersDTO(usersDTO.get());
                            userHoldingDTO.setHoldingDTO(holdingDTO.get());
                            userHoldingService.insertUserHolding(userHoldingDTO);
                        }
                    }
                } catch (Exception e) {
                    log.error(PROCESSING_ORDER_ERROR,"to Exit", userConfigurationDTO.getUserConfigurationId(), symbol, e.getMessage());
                }
            });
        }
    }
}
