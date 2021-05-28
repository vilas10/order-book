package com.stock.orderbook.service;

import com.stock.orderbook.model.TopOrdersFinderStrategyType;
import org.springframework.stereotype.Service;

@Service
public interface TopOrdersFinderStrategy {
    String topOrders(String symbol, String timestamp);
    TopOrdersFinderStrategyType getTopOrderFinderStrategyType();
}
