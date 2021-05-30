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
 * TopBidOrdersFinder implements the TopOrdersFinderStrategy for BIDS strategy orderType.
 * - Implements topOrders method to find top ask orders
 * - Implements getStrategyOrderType method to return asks order type
 */
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

    /**
     * topOrders method performs following steps in finding top bid orders
     * 1. Checks if the timestamp is already in bidsCache. If so, returns already calculated top bids
     * 2. If not, finds the floorKey timestamp (i.e. greatest timestamp less than input timestamp) and calculates the
     * top bids
     * 3. Caches the top bids result in bidsCache for the input timestamp
     * 4. Returns the top bids in required output format using OutputFormatter
     * @param symbolName - name of the symbol for which the top orders to be calculated
     * @param timestamp - time at which the top orders to be calculated
     * @return String - returns formatted string with top 5 bids
     */
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
        if (nearestTimestamp == null) {
            log.info(symbolName + "@" + timestamp + ": " + OutputFormatter.TOO_OLD_TIMESTAMP_FOR_BIDS);
            return OutputFormatter.BIDS_PREFIX + OutputFormatter.TOO_OLD_TIMESTAMP_FOR_BIDS;
        }

        PriorityQueue<Quote> bidsQueue = new PriorityQueue<>(symbol.getBidsCache().get(nearestTimestamp));
        int quotesStartIndex = symbol.getQuotesIndex().get(nearestTimestamp);

        log.info("Finding bids with info: timestamp: {}, nearestTimestamp: {}, " +
                "quotesStartIndex: {}", timestamp, nearestTimestamp, quotesStartIndex);
        PriorityQueue<Quote> topOrdersQueue = topOrdersFinder.findTopOrders(bidsQueue, quotesStartIndex, symbol, timestamp);
        log.debug("Previous Cache Record: {}, New record: {}", new PriorityQueue<>(bidsQueue),
                new PriorityQueue<>(topOrdersQueue));

        if (topOrdersQueue.isEmpty()) {
            log.info(symbolName + "@" + timestamp + ": " + OutputFormatter.NO_BIDS_FOUND);
            return OutputFormatter.BIDS_PREFIX + OutputFormatter.NO_BIDS_FOUND;
        }
        bidsCache.put(timestamp, topOrdersQueue);
        return OutputFormatter.topBidsFormat(OutputFormatter.getTopOrdersFromQueue(topOrdersQueue, TOP_ORDERS_LIMIT));
    }

    /**
     * getStrategyOrderType method is implemented to return BIDS enum
     * @return OrderType Enum corresponding to BIDS
     */
    @Override
    public OrderType getStrategyOrderType() {
        return OrderType.BIDS;
    }

}
