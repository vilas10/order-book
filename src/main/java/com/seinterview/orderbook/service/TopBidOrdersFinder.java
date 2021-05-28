package com.seinterview.orderbook.service;

import com.seinterview.orderbook.model.Quote;
import com.seinterview.orderbook.model.TopOrdersFinderStrategyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopBidOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopBidOrdersFinder.class);

    @Autowired
    private TopOrdersFinder topOrdersFinder;

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private PriorityQueue<Quote> bidsQueue;

    @Override
    public String topOrders(String symbol, String timestamp) {
        log.info("Processing top bids for symbol: {} at timestamp: {}", symbol, timestamp);

        if (bidsQueue == null) {
            bidsQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                    Comparator.comparing(Quote::getNegativeBidPrice).thenComparing(Quote::getStartTime));
        }
        List<Quote> topBids = topOrdersFinder.findTopOrders(bidsQueue, symbol, timestamp);

        String topBidsString = topBids.stream().map(Quote::bidOutputFormat)
                .collect(Collectors.joining("; "));

        return "Best Bids: " + topBidsString;
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.BIDS;
    }

}
