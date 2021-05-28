package com.seinterview.orderbook.service;

import com.seinterview.orderbook.model.Quote;
import com.seinterview.orderbook.model.TopOrdersFinderStrategyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Asks
 */
@Service
public class TopAskOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopAskOrdersFinder.class);

    @Autowired
    private TopOrdersFinder topOrdersFinder;

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private PriorityQueue<Quote> asksQueue;

    @Override
    public String topOrders(String symbol, String timestamp) {
        log.info("Processing top asks for symbol: {} at timestamp: {}", symbol, timestamp);
        if (asksQueue == null) {
            asksQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                    Comparator.comparing(Quote::getAskPrice).thenComparing(Quote::getStartTime));
        }

        List<Quote> topAsks = topOrdersFinder.findTopOrders(asksQueue, symbol, timestamp);

        String topAsksString = topAsks.stream().map(Quote::askOutputFormat)
                .collect(Collectors.joining("; "));

        return "Best Asks: " + topAsksString;
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.ASKS;
    }
}
