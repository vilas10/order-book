package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.stock.orderbook.utils.CommonUtil.TIMESTAMP_000_MILLIS_SUFFIX;

@Component
public class TopOrdersFinder {
    private static final Logger log = LoggerFactory.getLogger(TopOrdersFinder.class);

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private final Map<String, Symbol> symbolMap;

    public TopOrdersFinder(Map<String, Symbol> symbolMap) {
        this.symbolMap = symbolMap;
    }

    public List<Quote> findTopOrders(PriorityQueue<Quote> ordersQueue, int quotesStartIndex, String symbol, String timestamp) {
        log.info("Finding top orders for symbol: {} at timestamp: {}", symbol, timestamp);

        PriorityQueue<Quote> refreshedOrdersQueue = removeEndedQuotes(ordersQueue, timestamp);
        updateOrdersQueue(refreshedOrdersQueue, quotesStartIndex, symbol, timestamp);

        return getTopOrders(refreshedOrdersQueue);
    }

    private PriorityQueue<Quote> removeEndedQuotes(PriorityQueue<Quote> ordersQueue, String timestamp) {
        if (timestamp.contains(TIMESTAMP_000_MILLIS_SUFFIX)) {
            return ordersQueue;
        }
        PriorityQueue<Quote> refreshedOrdersQueue = new PriorityQueue<>(ordersQueue);
        refreshedOrdersQueue.clear();
        Quote quote;

        while ((quote = ordersQueue.poll()) != null) {
            if (quote.getEndTime().compareTo(timestamp) > 0) {
                refreshedOrdersQueue.add(quote);
            }
        }

        return refreshedOrdersQueue;
    }

    private void updateOrdersQueue(PriorityQueue<Quote> ordersQueue, int quotesStartIndex, String symbol, String timestamp) {
        List<Quote> symbolQuotes = symbolMap.get(symbol).getQuotes();

        for (int index = quotesStartIndex; index < symbolQuotes.size() && symbolQuotes.get(index).getStartTime().compareTo(timestamp) <= 0; index++) {
            Quote quote = symbolQuotes.get(index);
            if (quote.getEndTime().compareTo(timestamp) > 0) {
                ordersQueue.add(quote);
                if (ordersQueue.size() > TOP_ORDERS_LIMIT) {
                    ordersQueue.poll();
                }
            }
        }
    }

    private List<Quote> getTopOrders(PriorityQueue<Quote> ordersQueue) {
        Deque<Quote> orders = new ArrayDeque<>();
        Quote quote;
        while ((quote = ordersQueue.poll()) != null)
            orders.addFirst(quote);

        return new ArrayList<>(orders);
    }
}
