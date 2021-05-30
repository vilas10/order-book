package com.stock.orderbook.controller;

import com.stock.orderbook.config.TopOrderFinderStrategyFactory;
import com.stock.orderbook.service.RequestValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class OrderBookControllerTest {
    @MockBean
    private TopOrderFinderStrategyFactory topOrderFinderStrategyFactory;

    @MockBean
    private RequestValidator requestValidator;

    @Test
    public void testOrderBookControllerWelcome() {
        OrderBookController orderBookController = new OrderBookController(topOrderFinderStrategyFactory,
                requestValidator);
        String welcomeResponse = orderBookController.welcome();
        Assertions.assertEquals("Welcome to the restful implementation of Order Book", welcomeResponse);
    }


}
