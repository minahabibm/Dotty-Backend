package com.tradingbot.dotty.serviceImpls;

import com.tradingbot.dotty.models.Holding;
import com.tradingbot.dotty.models.dto.HoldingDTO;
import com.tradingbot.dotty.repositories.HoldingRepository;
import com.tradingbot.dotty.service.HoldingService;
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
        log.info("Getting Holdings");
        return holdingRepository.findAll().stream().map(holding -> modelMapper.map(holding, HoldingDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Long insertHolding(HoldingDTO holdingDTO) {
        log.info("Inserting Holding for Ticker {} Order",  holdingDTO.getSymbol());
        Holding holding = holdingRepository.save(modelMapper.map(holdingDTO, Holding.class));
        return holding.getHoldingTickerId();
    }

    @Override
    public Long updateHolding(HoldingDTO holdingDTO) {
        log.info("Updating Holding for Ticker {} Order",  holdingDTO.getSymbol());
        if(holdingDTO.getHoldingTickerId() == null)
            throw new RuntimeException();
        Optional<Holding> holding = holdingRepository.findById(holdingDTO.getHoldingTickerId());
        holding.ifPresent(holdingUpdate -> BeanUtils.copyProperties(holdingDTO, holdingUpdate, "updatedAt"));
        Holding holdingUpdated = holdingRepository.save(holding.get());
        return holdingUpdated.getHoldingTickerId();
    }

    @Override
    public String deleteHolding() {
        return null;
    }
}
