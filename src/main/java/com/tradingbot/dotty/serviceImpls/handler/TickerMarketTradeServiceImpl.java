package com.tradingbot.dotty.serviceImpls.handler;

import com.tradingbot.dotty.models.dto.*;
import com.tradingbot.dotty.models.dto.requests.TechnicalIndicatorResponse;
import com.tradingbot.dotty.service.*;
import com.tradingbot.dotty.service.handler.TickerMarketTradeService;
import com.tradingbot.dotty.utils.constants.Indicators;
import com.tradingbot.dotty.utils.constants.TradeDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;


@Slf4j
@Service
@Transactional
public class TickerMarketTradeServiceImpl implements TickerMarketTradeService {

    @Autowired
    private PositionTrackerService positionTrackerService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private HoldingService holdingService;


    @Override
    public void startTrackingForTrade(String symbol , String tIndicatorName, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails, Indicators indicator) {

        log.debug(TICKER_MARKET_DATA_TRACKING, symbol,  "Start");
        PositionTrackerDTO newPositionTrackerDTO = PositionTrackerDTO.builder()
                .symbol(symbol)
                .typeOfTrade(Float.parseFloat(tiValuesDetails.getRsi()) < TradeDetails.OVERSOLD.value ?  TradeDetails.OVERSOLD.orderType : TradeDetails.OVERBOUGHT.orderType)
                .indicator(indicator.name())
                .active(true)
                .build();
        Optional<PositionTrackerDTO> positionTrackerDTO = positionTrackerService.insertPositionTracker(newPositionTrackerDTO);

        if(positionTrackerDTO.isPresent()){
            log.trace(TICKER_MARKET_DATA_TRACKING_POSITION, symbol);
            PositionDTO positionDTO =  new PositionDTO();
            positionDTO.setSymbol(symbol);
            positionDTO.setTai(tIndicatorName);
            positionDTO.setTaiValue(Float.valueOf(tiValuesDetails.getRsi()));
            positionDTO.setIntervals(LocalDateTime.parse(tiValuesDetails.getDatetime().replaceFirst(" ", "T")));
            positionDTO.setOpen(Float.valueOf(tiValuesDetails.getOpen()));
            positionDTO.setHigh(Float.valueOf(tiValuesDetails.getHigh()));
            positionDTO.setLow(Float.valueOf(tiValuesDetails.getLow()));
            positionDTO.setClose(Float.valueOf(tiValuesDetails.getClose()));
            positionDTO.setVolume(Long.valueOf(tiValuesDetails.getVolume()));
            positionDTO.setPositionTrackerDTO(positionTrackerDTO.get());
            positionService.insertPosition(positionDTO);
        } else {
            throw new RuntimeException("Failed to retrieve or create PositionTracker");
        }
    }

    @Override
    public void updateTrackingForTrade(String symbol , String tIndicatorName, TechnicalIndicatorResponse.ValuesDetails tiValuesDetails) {
        log.debug(TICKER_MARKET_DATA_TRACKING, symbol,  "Update");
        Optional<PositionTrackerDTO> positionTrackerDTO = positionTrackerService.getPositionTracker(symbol);

        if(positionTrackerDTO.isPresent()){
            log.trace(TICKER_MARKET_DATA_TRACKING_POSITION, symbol);
            PositionDTO positionDTO =  new PositionDTO();
            positionDTO.setSymbol(symbol);
            positionDTO.setTai(tIndicatorName);
            positionDTO.setTaiValue(Float.valueOf(tiValuesDetails.getRsi()));
            positionDTO.setIntervals(LocalDateTime.parse(tiValuesDetails.getDatetime().replaceFirst(" ", "T")));
            positionDTO.setOpen(Float.valueOf(tiValuesDetails.getOpen()));
            positionDTO.setHigh(Float.valueOf(tiValuesDetails.getHigh()));
            positionDTO.setLow(Float.valueOf(tiValuesDetails.getLow()));
            positionDTO.setClose(Float.valueOf(tiValuesDetails.getClose()));
            positionDTO.setVolume(Long.valueOf(tiValuesDetails.getVolume()));
            positionDTO.setPositionTrackerDTO(positionTrackerDTO.get());
            positionService.insertPosition(positionDTO);
        } else {
            throw new RuntimeException("Failed to retrieve or create PositionTracker");
        }
    }

    @Override
    public void stopTrackingForTrade(String symbol) {
        log.debug(TICKER_MARKET_DATA_TRACKING, symbol,  "Stop");
        Optional<PositionTrackerDTO> positionTrackerDTO = positionTrackerService.getPositionTracker(symbol);
        if(positionTrackerDTO.isPresent()) {
            positionTrackerDTO.get().setActive(false);
            positionTrackerService.updatePositionTracker(positionTrackerDTO.get());
        } else {
            log.trace(TICKER_MARKET_DATA_NOT_TRACKED);
        }
    }

    @Override
    public boolean isTickerTracked(String symbol) {
        log.debug(TICKER_MARKET_DATA_TICKER_TRACKER, symbol);
        return positionTrackerService.getPositionTracker(symbol).isPresent();
    }

    @Override
    public boolean isInTrade(String symbol) {
        log.debug(TICKER_MARKET_TRADE_IN_POSITION, symbol);
        return ordersService.getActiveTickerOrder(symbol).isPresent();
    }

    @Override
    public String getOrderType(boolean toOpen, String typeOfTrade) {
        if(toOpen)
            return TradeDetails.OVERSOLD.orderType.equals(typeOfTrade) ? TradeDetails.OVERSOLD.order : TradeDetails.OVERBOUGHT.order;
        else
            return TradeDetails.OVERSOLD.orderType.equals(typeOfTrade) ? TradeDetails.OVERBOUGHT.order : TradeDetails.OVERSOLD.order;
    }

    @Override
    public Optional<OrdersDTO> enterPosition(String symbol, Float entryPrice, String entryTime) {
        log.info(TICKER_MARKET_TRADE_OPEN_POSITION, symbol, entryPrice, entryTime);
        Optional<PositionTrackerDTO> positionTrackerDTO =  positionTrackerService.getPositionTracker(symbol);
        Optional<OrdersDTO> orderDTO = Optional.empty();
        if(positionTrackerDTO.isPresent() && ordersService.getActiveTickerOrder(symbol).isEmpty()) {
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setSymbol(symbol);
            ordersDTO.setTradeType(getOrderType(true, positionTrackerDTO.get().getTypeOfTrade()));
            ordersDTO.setQuantity(10L);
            ordersDTO.setEntryPrice(entryPrice);
            ordersDTO.setEntryTime(LocalDateTime.parse(entryTime.replaceFirst(" ","T")));
            ordersDTO.setActive(true);
            ordersDTO.setPositionTrackerDTO(positionTrackerDTO.get());
            orderDTO = ordersService.insertOrder(ordersDTO);
        }
        return orderDTO;
    }

    @Override
    public Optional<OrdersDTO> closePosition(String symbol, Float exitPrice, String exitTime) {
        log.info(TICKER_MARKET_TRADE_EXIT_POSITION, symbol, exitPrice, exitTime);
        Optional<PositionTrackerDTO> positionTrackerDTO =  positionTrackerService.getPositionTracker(symbol);
        Optional<OrdersDTO> openOrderDTO = ordersService.getActiveTickerOrder(symbol);
        Optional<OrdersDTO> orderDTO = Optional.empty();
        if(positionTrackerDTO.isPresent() && openOrderDTO.isPresent() ){
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setSymbol(symbol);
            ordersDTO.setTradeType(getOrderType(false, positionTrackerDTO.get().getTypeOfTrade()));
            ordersDTO.setQuantity(10L);
            ordersDTO.setEntryPrice(exitPrice);
            ordersDTO.setEntryTime(LocalDateTime.parse(exitTime.replaceFirst(" ", "T")));
            ordersDTO.setActive(false);
            ordersDTO.setPositionTrackerDTO(positionTrackerDTO.get());
            orderDTO =  ordersService.insertOrder(ordersDTO);

            openOrderDTO.get().setActive(false);
            ordersService.updateOrder(openOrderDTO.get());

            positionTrackerDTO.get().setActive(false);
            positionTrackerService.updatePositionTracker(positionTrackerDTO.get());
        }
        return orderDTO;
    }

    @Override
    public Optional<HoldingDTO> addToHolding(Long positionTrackerId) {
        List<OrdersDTO> ordersDTOList = ordersService.getOrdersByPositionTracker(positionTrackerId);
        Optional<HoldingDTO> holdingsDTO = Optional.empty();
        if(ordersDTOList.size() == 2) {
            log.info(TICKER_MARKET_TRADE_ADD_TO_HOLDING, ordersDTOList.get(0).getSymbol());
            OrdersDTO openedOrder = ordersDTOList.get(0);
            OrdersDTO closedOrder = ordersDTOList.get(1);

            HoldingDTO holdingDTO = new HoldingDTO();
            holdingDTO.setSymbol(openedOrder.getSymbol());
            holdingDTO.setTypeOfTrade(openedOrder.getTradeType().equals(TradeDetails.OVERSOLD.order) ? TradeDetails.OVERSOLD.orderType : TradeDetails.OVERBOUGHT.orderType);
            holdingDTO.setEntryPrice(openedOrder.getEntryPrice());
            holdingDTO.setExitPrice(closedOrder.getEntryPrice());
            holdingDTO.setQuantity(closedOrder.getQuantity());
            holdingDTO.setStartAt(openedOrder.getEntryTime());
            holdingDTO.setEndAt(closedOrder.getEntryTime());
            holdingsDTO = holdingService.insertHolding(holdingDTO);
        }
        return holdingsDTO;
    }

}
