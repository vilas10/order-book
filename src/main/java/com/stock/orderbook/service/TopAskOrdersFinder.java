package com.stock.orderbook.service;

import com.stock.orderbook.model.OrderType;
import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import com.stock.orderbook.utils.OutputFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * TopAskOrdersFinder implements the TopOrdersFinderStrategy for Asks strategy orderType. <br>
 * - Implements topOrders method to find top ask orders <br>
 * - Implements getStrategyOrderType method to return asks order type <br>
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

    /**
     * topOrders method performs following steps in finding top ask orders <br>
     * 1. Checks if the timestamp is already in asksCache. If so, returns already calculated top asks <br>
     * 2. If not, finds the floorKey timestamp (i.e. greatest timestamp less than input timestamp) and calculates the
     * top asks <br>
     * 3. Caches the top asks result in asksCache for the input timestamp <br>
     * 4. Returns the top asks in required output format using OutputFormatter <br>
     * @param symbolName - name of the symbol for which the top orders to be calculated
     * @param timestamp - time at which the top orders to be calculated
     * @return String - returns formatted string with top 5 asks
     */
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
        if (nearestTimestamp == null) {
            log.info(symbolName + "@" + timestamp + ": " + OutputFormatter.TOO_OLD_TIMESTAMP_FOR_ASKS);
            return OutputFormatter.ASKS_PREFIX + OutputFormatter.TOO_OLD_TIMESTAMP_FOR_ASKS;
        }
        PriorityQueue<Quote> asksQueue = new PriorityQueue<>(symbol.getAsksCache().get(nearestTimestamp));
        int quotesStartIndex = symbol.getQuotesIndex().get(nearestTimestamp);

        log.info("Finding asks with info: timestamp: {}, nearestTimestamp: {}, " +
                "quotesStartIndex: {}", timestamp, nearestTimestamp, quotesStartIndex);

        PriorityQueue<Quote> topOrdersQueue = topOrdersFinder.findTopOrders(asksQueue, quotesStartIndex, symbol, timestamp);
        if (topOrdersQueue.isEmpty()) {
            log.info(symbolName + "@" + timestamp + ": " + OutputFormatter.NO_ASKS_FOUND);
            return OutputFormatter.ASKS_PREFIX + OutputFormatter.NO_ASKS_FOUND;
        }

        asksCache.put(timestamp, topOrdersQueue);
        return OutputFormatter.topAsksFormat(OutputFormatter.getTopOrdersFromQueue(topOrdersQueue, TOP_ORDERS_LIMIT));
    }

    /**
     * getStrategyOrderType method is implemented to return ASKS enum
     * @return OrderType Enum corresponding to ASKS
     */
    @Override
    public OrderType getStrategyOrderType() {
        return OrderType.ASKS;
    }
}
