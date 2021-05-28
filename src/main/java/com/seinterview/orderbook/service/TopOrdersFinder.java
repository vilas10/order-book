package com.seinterview.orderbook.service;

import com.seinterview.orderbook.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TopOrdersFinder {
    private static final Logger log = LoggerFactory.getLogger(TopOrdersFinder.class);

    @Autowired
    private Map<String, List<Quote>> symbolToQuotesMap;

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    public List<Quote> findTopOrders(PriorityQueue<Quote> ordersQueue, String symbol, String timestamp) {
        log.info("Processing top orders for symbol: {} at timestamp: {}", symbol, timestamp);

        updateOrdersQueue(ordersQueue, symbol, timestamp);

        return getTopOrders(ordersQueue);
    }

    private List<Quote> getTopOrders(PriorityQueue<Quote> ordersQueue) {
        List<Quote> orders = new ArrayList<>();
        Quote quote;
        while (orders.size() < TOP_ORDERS_LIMIT && (quote = ordersQueue.poll()) != null)
            orders.add(quote);

        return orders;
    }

    private void updateOrdersQueue(PriorityQueue<Quote> ordersQueue, String symbol, String timestamp) {
        List<Quote> symbolQuotes = symbolToQuotesMap.get(symbol);

        for (Quote q: symbolQuotes) {
            if (q.getStartTime().compareTo(timestamp) >= 0) {
                break;
            }

            if (q.getEndTime().compareTo(timestamp) >= 0) {
                ordersQueue.add(q);
            }
        }
    }
}
