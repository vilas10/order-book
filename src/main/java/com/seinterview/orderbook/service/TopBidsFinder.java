package com.seinterview.orderbook.service;

import com.seinterview.orderbook.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TopBidsFinder {
    private static final Logger log = LoggerFactory.getLogger(TopAsksFinder.class);

    @Autowired
    private Map<String, List<Quote>> symbolToQuotesMap;

    @Value("${top.bids.limit}")
    private Integer TOP_BIDS_LIMIT;

    private PriorityQueue<Quote> bidsQueue;

    public List<Quote> findTopBids(String symbol, String timestamp) {
        log.info("Processing top bids for symbol: {} at timestamp: {}", symbol, timestamp);
        bidsQueue = new PriorityQueue<>(TOP_BIDS_LIMIT, Comparator.comparing(Quote::getNegativeBidPrice)
                    .thenComparing(Quote::getStartTime));

        List<Quote> symbolQuotes = symbolToQuotesMap.get(symbol);
        List<Quote> bids = new ArrayList<>();

        for (Quote q: symbolQuotes) {
            if (q.getStartTime().compareTo(timestamp) >= 0) {
                break;
            }

            if (q.getEndTime().compareTo(timestamp) >= 0) {
                bidsQueue.add(q);
            }
        }

        Quote quote;
        while (bids.size() < TOP_BIDS_LIMIT && (quote = bidsQueue.poll()) != null)
            bids.add(quote);

        return bids;
    }
}
