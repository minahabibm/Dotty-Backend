package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.LoggingConstants.*;
import com.tradingbot.dotty.models.Holding;
import com.tradingbot.dotty.models.dto.HoldingDTO;
import com.tradingbot.dotty.repositories.HoldingRepository;
import com.tradingbot.dotty.service.HoldingService;
import com.tradingbot.dotty.utils.TradeDetails;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HoldingServiceImpl implements HoldingService {

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<HoldingDTO> getHoldings() {
        log.trace(ENTITIES_READ_OPERATION, "Holding");
        return holdingRepository.findAll().stream().map(holding -> modelMapper.map(holding, HoldingDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<HoldingDTO> compareHoldings() {
        log.trace("Comparing Holdings.");
        int correct=0, other=0, incorrect=0;
        List<HoldingDTO> holdingsDTO = getHoldings();
        for(HoldingDTO holdingDTO : holdingsDTO) {
            if(holdingDTO.getTypeOfTrade().equals(TradeDetails.OVERSOLD.orderType))
                if (holdingDTO.getExitPrice() - holdingDTO.getEntryPrice()  > 0)  correct += 1;
                else if (holdingDTO.getExitPrice() - holdingDTO.getEntryPrice()  == 0) other += 1;
                else incorrect +=1;
            if(holdingDTO.getTypeOfTrade().equals(TradeDetails.OVERBOUGHT.orderType))
                if (holdingDTO.getEntryPrice() - holdingDTO.getExitPrice() > 0) correct += 1;
                else if (holdingDTO.getEntryPrice() - holdingDTO.getExitPrice() == 0) other += 1;
                else incorrect +=1;
        }
        log.warn("Correct {} , Incorrect {}, Other {}", correct, incorrect, other);
//        List<OrdersDTO> activeOrdersDTO = ordersService.getOrders().stream().filter(ordersDTO -> ordersDTO.getActive()).collect(Collectors.toList());
//        activeOrdersDTO.stream().forEach(System.out::println);
        return holdingsDTO;
    }

    @Override
    public Long insertHolding(HoldingDTO holdingDTO) {
        log.trace(ENTITY_CREATE_OPERATION, holdingDTO, "Holding");
        Holding holding = holdingRepository.save(modelMapper.map(holdingDTO, Holding.class));
        return holding.getHoldingTickerId();
    }

    @Override
    public Long updateHolding(HoldingDTO holdingDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, holdingDTO.getSymbol(), "Holding");
        if(holdingDTO.getHoldingTickerId() == null)
            throw new RuntimeException();
        Optional<Holding> holding = holdingRepository.findById(holdingDTO.getHoldingTickerId());
        holding.ifPresent(holdingUpdate -> BeanUtils.copyProperties(holdingDTO, holdingUpdate, "Holding"));
        Holding holdingUpdated = holdingRepository.save(holding.get());
        return holdingUpdated.getHoldingTickerId();
    }

    @Override
    public String deleteHolding() {
        return null;
    }
}
