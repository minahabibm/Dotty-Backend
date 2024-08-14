package com.tradingbot.dotty.Services;

import com.tradingbot.dotty.repositories.ScreenedTickersRepository;
import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.serviceImpls.ScreenedTickersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ScreenedTickersServiceTests {

    @Mock
    ScreenedTickersRepository screenedTickersRepository;

    @InjectMocks
    ScreenedTickersService screenedTickersService = new ScreenedTickersServiceImpl();


    @Test
    public void insertScreenedTickersTest() {

//        ScreenedTickerDTO screenedTickerDTO = new ScreenedTickerDTO().builder().name("Thermo Fisher Scientific Inc.").symbol("TMO").sector("Healthcare").exchangeShortName("NYSE").beta(0.8F).build();
//        ScreenedTickerDTO screenedTickerDTO1 = new ScreenedTickerDTO().builder().name("Chevron Corporation").symbol("CVX").sector("Energy").exchangeShortName("NYSE").beta(1.118F).build();
//        List<ScreenedTickerDTO> screenedTickerDTOList = Arrays.asList(screenedTickerDTO, screenedTickerDTO1);
//
//        given(screenedTickersService.insertScreenedTickers(screenedTickerDTOList)).willReturn(String.valueOf(screenedTickerDTOList.size()));

    }

    @Test
    public void insertScreenedTickerTest() {

    }

    @Test
    public void getScreenedTickersTest() {

    }

    @Test
    public void getTodayScreenedTickers() {

    }

    @Test
    public void updateScreenedTickerTest() {

    }

    @Test
    public void deleteScreenedTickersTest() {

    }

}
