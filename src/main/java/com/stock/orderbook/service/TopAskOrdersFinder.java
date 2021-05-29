package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import com.stock.orderbook.model.TopOrdersFinderStrategyType;
import com.stock.orderbook.utils.OutputFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Asks
 */
@Component
public class TopAskOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopAskOrdersFinder.class);

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private final TopOrdersFinder topOrdersFinder;

    private final Map<String, Symbol> symbolMap;

    public TopAskOrdersFinder(TopOrdersFinder topOrdersFinder, Map<String, Symbol> symbolMap) {
        this.topOrdersFinder = topOrdersFinder;
        this.symbolMap = symbolMap;
    }

    @Override
    public String topOrders(String symbolName, String timestamp) {
        log.info("Processing top asks for symbol: {} at timestamp: {}", symbolName, timestamp);
        Symbol symbol = symbolMap.get(symbolName);

        TreeMap<String, PriorityQueue<Quote>> asksCache = symbol.getAsksCache();
        if (asksCache.containsKey(timestamp)) {
            return OutputFormatter.topAsksFormat(OutputFormatter.getTopOrdersFromQueue(asksCache.get(timestamp),
                    TOP_ORDERS_LIMIT));
        }

        String nearestTimestamp = asksCache.floorKey(timestamp);
        PriorityQueue<Quote> asksQueue = new PriorityQueue<>(symbol.getAsksCache().get(nearestTimestamp));
        int quotesStartIndex = symbol.getQuotesIndex().get(nearestTimestamp);

        log.info("Finding asks with info: timestamp: {}, nearestTimestamp: {}, " +
                "quotesStartIndex: {}", timestamp, nearestTimestamp, quotesStartIndex);

        PriorityQueue<Quote> topOrdersQueue = topOrdersFinder.findTopOrders(asksQueue, quotesStartIndex, symbol, timestamp);
        asksCache.put(timestamp, topOrdersQueue);
        return OutputFormatter.topAsksFormat(OutputFormatter.getTopOrdersFromQueue(topOrdersQueue, TOP_ORDERS_LIMIT));
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.ASKS;
    }
}
