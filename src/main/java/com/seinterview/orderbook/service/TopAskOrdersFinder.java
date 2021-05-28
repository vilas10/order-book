package com.seinterview.orderbook.service;

import com.seinterview.orderbook.model.Quote;
import com.seinterview.orderbook.model.TopOrdersFinderStrategyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Asks
 */
@Component
public class TopAskOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopAskOrdersFinder.class);

    @Autowired
    private TopOrdersFinder topOrdersFinder;

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private PriorityQueue<Quote> asksQueue;

    @Override
    public List<Quote> topOrders(String symbol, String timestamp) {
        log.info("Processing top asks for symbol: {} at timestamp: {}", symbol, timestamp);
        if (asksQueue == null) {
            asksQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                    Comparator.comparing(Quote::getAskPrice).thenComparing(Quote::getStartTime));
        }
        return topOrdersFinder.findTopOrders(asksQueue, symbol, timestamp);
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.ASKS;
    }
}
