package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TopOrdersFinder {
    private static final Logger log = LoggerFactory.getLogger(TopOrdersFinder.class);

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private final Map<String, List<Quote>> symbolToQuotesMap;

    public TopOrdersFinder(Map<String, List<Quote>> symbolToQuotesMap) {
        this.symbolToQuotesMap = symbolToQuotesMap;
    }

    @Bean
    public Map<String, PriorityQueue<Quote>> asksPerMinuteMap() {
        log.info("Building Asks Per Minute Map");

        return new HashMap<>();
    }

    public List<Quote> findTopOrders(PriorityQueue<Quote> ordersQueue, String symbol, String timestamp) {
        log.info("Finding top orders for symbol: {} at timestamp: {}", symbol, timestamp);
        updateOrdersQueue(ordersQueue, symbol, timestamp);

        return getTopOrders(ordersQueue);
    }

    private List<Quote> getTopOrders(PriorityQueue<Quote> ordersQueue) {
        Deque<Quote> orders = new ArrayDeque<>();
        Quote quote;
        while (orders.size() < TOP_ORDERS_LIMIT && (quote = ordersQueue.poll()) != null)
            orders.addFirst(quote);

        return new ArrayList<>(orders);
    }

    private void updateOrdersQueue(PriorityQueue<Quote> ordersQueue, String symbol, String timestamp) {
        List<Quote> symbolQuotes = symbolToQuotesMap.get(symbol);

        for (Quote q: symbolQuotes) {
            if (q.getStartTime().compareTo(timestamp) >= 0) {
                break;
            }

            if (q.getEndTime().compareTo(timestamp) >= 0) {
                ordersQueue.add(q);
                if (ordersQueue.size() > TOP_ORDERS_LIMIT) {
                    ordersQueue.poll();
                }
            }
        }
    }
}
