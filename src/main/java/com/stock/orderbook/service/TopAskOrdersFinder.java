package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.TopOrdersFinderStrategyType;
import com.stock.orderbook.utils.OutputFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Asks
 */
@Component
public class TopAskOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopAskOrdersFinder.class);

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private PriorityQueue<Quote> asksQueue;

    private final TopOrdersFinder topOrdersFinder;

    public TopAskOrdersFinder(TopOrdersFinder topOrdersFinder) {
        this.topOrdersFinder = topOrdersFinder;
    }

    @Override
    public String topOrders(String symbol, String timestamp) {
        log.info("Processing top asks for symbol: {} at timestamp: {}", symbol, timestamp);
        if (asksQueue == null) {
            asksQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                    Comparator.comparing(Quote::getAskPrice).thenComparing(Quote::getStartTime));
        }

        List<Quote> topAsks = topOrdersFinder.findTopOrders(asksQueue, symbol, timestamp);

        return "Best Asks: " + OutputFormatter.toString(topAsks, Quote::askOutputFormat);
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.ASKS;
    }
}
