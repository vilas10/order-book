package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.PriorityQueue;

/**
 * TopOrdersFinder contains the logic for finding the top orders in a given priority queue.
 * This is the common implementation for both asks and bids.
 */
@Component
public class TopOrdersFinder {
    private static final Logger log = LoggerFactory.getLogger(TopOrdersFinder.class);

    /**
     * findTopOrders performs the core logic for finding top orders in the queue
     * @param ordersQueue - contains the orders from cache
     * @param quotesStartIndex - index at which the ordersQueue is cached
     * @param symbol - contains symbol data
     * @param timestamp - at which the top orders are needed
     * @return PriorityQueue<Quote> - returns queue with the active quotes at timestamp
     */
    public PriorityQueue<Quote> findTopOrders(PriorityQueue<Quote> ordersQueue, int quotesStartIndex, Symbol symbol,
                                     String timestamp) {
        log.info("Finding top orders for symbol: {} at timestamp: {}", symbol.getSymbol(), timestamp);

        // remove inactive quotes from cached queue
        PriorityQueue<Quote> topOrdersQueue = removeInactiveQuotes(ordersQueue, timestamp);

        // load the quotes starting from quotesStartIndex which occurred before timestamp
        updateOrdersQueue(topOrdersQueue, quotesStartIndex, symbol, timestamp);

        return topOrdersQueue;
    }

    /**
     * removeInactiveQuotes removes inactive quotes from cached queue
     * @param ordersQueue - cached priority queue
     * @param timestamp - reference timestamp to find inactive quotes
     * @return PriorityQueue<Quote> - returns queue with only active quotes at timestamp
     */
    private PriorityQueue<Quote> removeInactiveQuotes(PriorityQueue<Quote> ordersQueue, String timestamp) {
        PriorityQueue<Quote> activeOrdersQueue = new PriorityQueue<>(ordersQueue);
        activeOrdersQueue.clear();
        Quote quote;

        while ((quote = ordersQueue.poll()) != null) {
            // checks if quote is active at timestamp
            if (quote.getEndTime().compareTo(timestamp) > 0) {
                activeOrdersQueue.add(quote);
            }
        }

        return activeOrdersQueue;
    }

    /**
     * updateOrdersQueue loads the ordersQueue with additional quotes from quotesStartIndex to timestamp.
     * @param ordersQueue - queue with active quotes at timestamp
     * @param quotesStartIndex - starting index for loading quotes
     * @param symbol - contains symbol data
     * @param timestamp - until which the quotes are loaded to find top orders
     */
    private void updateOrdersQueue(PriorityQueue<Quote> ordersQueue, int quotesStartIndex, Symbol symbol,
                                   String timestamp) {
        List<Quote> quotes = symbol.getQuotes();

        int index = quotesStartIndex;
        for (;index < quotes.size() && quotes.get(index).getStartTime().compareTo(timestamp) <= 0; index++) {
            Quote quote = quotes.get(index);
            // checks if the quote is active for adding to the queue
            if (quote.getEndTime().compareTo(timestamp) > 0) {
                ordersQueue.add(quote);
            }
        }
        // notes the index of for using as quotesStartIndex for future requests
        symbol.getQuotesIndex().put(timestamp, index);
    }
}
