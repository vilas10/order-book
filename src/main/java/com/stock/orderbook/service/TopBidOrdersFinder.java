package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.TopOrdersFinderStrategyType;
import com.stock.orderbook.utils.OutputFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class TopBidOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopBidOrdersFinder.class);
    private static final String BIDS_PREFIX = "Best Bids: ";

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    private final TopOrdersFinder topOrdersFinder;

    public TopBidOrdersFinder(TopOrdersFinder topOrdersFinder) {
        this.topOrdersFinder = topOrdersFinder;
    }

    @Override
    public String topOrders(String symbol, String timestamp) {
        log.info("Processing top bids for symbol: {} at timestamp: {}", symbol, timestamp);

        PriorityQueue<Quote> bidsQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                    Comparator.comparing(Quote::getBidPrice).thenComparing(Quote::getStartTime));

        List<Quote> topBids = topOrdersFinder.findTopOrders(bidsQueue, symbol, timestamp);

        return BIDS_PREFIX + OutputFormatter.toString(topBids, Quote::bidOutputFormat);
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.BIDS;
    }

}
