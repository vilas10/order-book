package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.PriorityQueue;

@Component
public class TopOrdersFinder {
    private static final Logger log = LoggerFactory.getLogger(TopOrdersFinder.class);

    public PriorityQueue<Quote> findTopOrders(PriorityQueue<Quote> ordersQueue, int quotesStartIndex, Symbol symbol,
                                     String timestamp) {
        log.info("Finding top orders for symbol: {} at timestamp: {}", symbol.getSymbol(), timestamp);

        PriorityQueue<Quote> topOrdersQueue = removeInactiveQuotes(ordersQueue, timestamp);
        updateOrdersQueue(topOrdersQueue, quotesStartIndex, symbol, timestamp);

        return topOrdersQueue;
    }

    private PriorityQueue<Quote> removeInactiveQuotes(PriorityQueue<Quote> ordersQueue, String timestamp) {
        PriorityQueue<Quote> activeOrdersQueue = new PriorityQueue<>(ordersQueue);
        activeOrdersQueue.clear();
        Quote quote;

        while ((quote = ordersQueue.poll()) != null) {
            if (quote.getEndTime().compareTo(timestamp) > 0) {
                activeOrdersQueue.add(quote);
            }
        }

        return activeOrdersQueue;
    }

    private void updateOrdersQueue(PriorityQueue<Quote> ordersQueue, int quotesStartIndex, Symbol symbol, String timestamp) {
        List<Quote> symbolQuotes = symbol.getQuotes();

        int index = quotesStartIndex;
        for (;index < symbolQuotes.size() && symbolQuotes.get(index).getStartTime().compareTo(timestamp) <= 0; index++) {
            Quote quote = symbolQuotes.get(index);
            if (quote.getEndTime().compareTo(timestamp) > 0) {
                ordersQueue.add(quote);
            }
        }
        symbol.getQuotesIndex().put(timestamp, index);
    }
}
