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

@Component
public class TopBidOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopBidOrdersFinder.class);

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private final TopOrdersFinder topOrdersFinder;

    private final Map<String, Symbol> symbolMap;

    public TopBidOrdersFinder(TopOrdersFinder topOrdersFinder, Map<String, Symbol> symbolMap) {
        this.topOrdersFinder = topOrdersFinder;
        this.symbolMap = symbolMap;
    }
    @Override
    public String topOrders(String symbolName, String timestamp) {
        log.info("Processing top bids for symbol: {} at timestamp: {}", symbolName, timestamp);

        Symbol symbol = symbolMap.get(symbolName);

        TreeMap<String, PriorityQueue<Quote>> bidsCache = symbol.getBidsCache();
        if (bidsCache.containsKey(timestamp)) {
            log.info("Request found in cache - returning result from cache");
            return OutputFormatter.topBidsFormat(OutputFormatter.getTopOrdersFromQueue(
                    bidsCache.get(timestamp), TOP_ORDERS_LIMIT));
        }

        log.info("Request not found in cache - finding top orders for request");
        String nearestTimestamp = bidsCache.floorKey(timestamp);
        PriorityQueue<Quote> bidsQueue = new PriorityQueue<>(symbol.getBidsCache().get(nearestTimestamp));
        int quotesStartIndex = symbol.getQuotesIndex().get(nearestTimestamp);

        log.info("Finding bids with info: timestamp: {}, nearestTimestamp: {}, " +
                "quotesStartIndex: {}", timestamp, nearestTimestamp, quotesStartIndex);
        PriorityQueue<Quote> topOrdersQueue = topOrdersFinder.findTopOrders(bidsQueue, quotesStartIndex, symbol, timestamp);
        log.debug("Previous Cache Record: {}, New record: {}", new PriorityQueue<>(bidsQueue),
                new PriorityQueue<>(topOrdersQueue));
        bidsCache.put(timestamp, topOrdersQueue);
        return OutputFormatter.topBidsFormat(OutputFormatter.getTopOrdersFromQueue(topOrdersQueue, TOP_ORDERS_LIMIT));
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.BIDS;
    }

}
