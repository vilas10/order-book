package com.seinterview.orderbook.service;

import com.seinterview.orderbook.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Asks
 */
@Component
public class TopAsksFinder {
    private static final Logger log = LoggerFactory.getLogger(TopAsksFinder.class);

    @Autowired
    private Map<String, List<Quote>> symbolToQuotesMap;

    @Value("${top.asks.limit}")
    private Integer TOP_ASKS_LIMIT;

    private PriorityQueue<Quote> asksQueue;

    public List<Quote> findTopAsks(String symbol, String timestamp) {
        log.info("Processing top asks for symbol: {} at timestamp: {}", symbol, timestamp);

        asksQueue = new PriorityQueue<>(TOP_ASKS_LIMIT, Comparator.comparing(Quote::getAskPrice)
                    .thenComparing(Quote::getStartTime));

        List<Quote> symbolQuotes = symbolToQuotesMap.get(symbol);

        for (Quote q: symbolQuotes) {
            if (q.getStartTime().compareTo(timestamp) >= 0) {
                break;
            }

            if (q.getEndTime().compareTo(timestamp) >= 0) {
                asksQueue.add(q);
            }
        }
        log.info("Total asks found requested timestamp: {}", asksQueue.size());

        log.info("Retrieving top {} requests", TOP_ASKS_LIMIT);
        List<Quote> topAsks = new ArrayList<>();
        Quote quote;
        while (topAsks.size() < TOP_ASKS_LIMIT && (quote = asksQueue.poll()) != null)
            topAsks.add(quote);

        log.info("Found Top {} requests", topAsks.size());
        return topAsks;
    }

}
