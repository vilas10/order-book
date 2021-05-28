package com.seinterview.orderbook.service;

import com.seinterview.orderbook.model.Quote;
import com.seinterview.orderbook.model.TopOrdersFinderStrategyType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TopOrdersFinderStrategy {
    String topOrders(String symbol, String timestamp);
    TopOrdersFinderStrategyType getTopOrderFinderStrategyType();
}
