package com.tradingbot.dotty.ScreenedTickers;

import com.tradingbot.dotty.repositories.ScreenedTickersRepository;
import com.tradingbot.dotty.service.ScreenedTickersService;
import com.tradingbot.dotty.serviceImpls.ScreenedTickersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// TODO Screened Ticker Service
@ExtendWith(MockitoExtension.class)
public class ScreenedTickersServiceTests {

    @Mock
    ScreenedTickersRepository screenedTickersRepository;

    @InjectMocks
    ScreenedTickersService screenedTickersService = new ScreenedTickersServiceImpl();

    @Test
    public void getScreenedTickersTest() {

    }

    @Test
    public void insertScreenedTickersTest() {

    }

    @Test
    public void insertScreenedTickerTest() {

    }

    @Test
    public void updateScreenedTickerTest() {

    }

    @Test
    public void deleteScreenedTickersTest() {

    }

}
