package com.tradingbot.dotty.serviceImpls;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;

import com.tradingbot.dotty.models.Holding;
import com.tradingbot.dotty.models.dto.HoldingDTO;
import com.tradingbot.dotty.repositories.HoldingRepository;
import com.tradingbot.dotty.service.HoldingService;
import com.tradingbot.dotty.utils.constants.TradeDetails;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    public Optional<HoldingDTO> insertHolding(HoldingDTO holdingDTO) {
        log.trace(ENTITY_CREATE_OPERATION, holdingDTO, "Holding");
        Holding holding = holdingRepository.save(modelMapper.map(holdingDTO, Holding.class));
        return Optional.of(modelMapper.map(holding, HoldingDTO.class));
    }

    @Override
    public Optional<HoldingDTO> updateHolding(HoldingDTO holdingDTO) {
        log.trace(ENTITY_UPDATE_OPERATION, holdingDTO.getSymbol(), "Holding");
        if(holdingDTO.getHoldingTickerId() == null)
            throw new RuntimeException();
        return holdingRepository.findById(holdingDTO.getHoldingTickerId())
                .map(existingHolding -> {
                    BeanUtils.copyProperties(holdingDTO, existingHolding, "updatedAt");
                    Holding updatedHolding = holdingRepository.save(existingHolding);
                    return modelMapper.map(updatedHolding, HoldingDTO.class);
                });
    }

    @Override
    public void deleteHolding() {

    }


    @Override
    public Map<String, Integer> compareHoldings() {
        log.trace("Comparing Holdings.");
        int correct=0, other=0, incorrect=0;

        try {
            List<HoldingDTO> holdingsDTO = getHoldings();
            for(HoldingDTO holdingDTO : holdingsDTO) {
                if(holdingDTO.getTypeOfTrade() == null || holdingDTO.getEntryPrice() == null || holdingDTO.getExitPrice() == null) {
                    log.error("Type of trade, exit price, or entry price is null for holding ID: {}", holdingDTO.getHoldingTickerId());
                    continue;                                                                                           // Skip this iteration and continue with the next one
                }
                if(holdingDTO.getTypeOfTrade().equals(TradeDetails.OVERSOLD.orderType))
                    if (holdingDTO.getExitPrice() - holdingDTO.getEntryPrice()  > 0)  correct += 1;
                    else if (holdingDTO.getExitPrice() - holdingDTO.getEntryPrice()  == 0) other += 1;
                    else incorrect +=1;
                if(holdingDTO.getTypeOfTrade().equals(TradeDetails.OVERBOUGHT.orderType))
                    if (holdingDTO.getEntryPrice() - holdingDTO.getExitPrice() > 0) correct += 1;
                    else if (holdingDTO.getEntryPrice() - holdingDTO.getExitPrice() == 0) other += 1;
                    else incorrect +=1;
            }
            log.info("Correct {} , Incorrect {}, Other {}", correct, incorrect, other);
            return Map.of(
                    "Correct: ", correct,
                    "Incorrect: ", incorrect,
                    "Other: ", other
            );
        } catch (Exception e) {
            log.error("Unexpected error during holdings comparison: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<HoldingDTO> getHoldingsStatistics() {
        log.trace("Holdings Statistics.");

        int total=0;
        List<HoldingDTO> holdingsDTO = getHoldings();
        for(HoldingDTO holdingDTO : holdingsDTO) {
            if(holdingDTO.getTypeOfTrade().equals(TradeDetails.OVERSOLD.orderType))
                total += ((holdingDTO.getExitPrice() - holdingDTO.getEntryPrice()) * 10);
            if(holdingDTO.getTypeOfTrade().equals(TradeDetails.OVERBOUGHT.orderType))
                total += ((holdingDTO.getEntryPrice() - holdingDTO.getExitPrice()) * 10);
        }
        log.warn("total {}", total);
        return holdingsDTO;
    }

}
