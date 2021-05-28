package com.seinterview.orderbook.service;

import com.seinterview.orderbook.config.TopOrderFinderStrategyFactory;
import com.seinterview.orderbook.model.Quote;
import com.seinterview.orderbook.model.TopOrdersFinderStrategyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderBookServiceImpl implements OrderBookService {
    @Autowired
    private TopOrderFinderStrategyFactory topOrderFinderStrategyFactory;

    @Override
    public String getTopBids(String symbol, String timestamp) {
        List<Quote> topBids = topOrderFinderStrategyFactory
                .findStrategy(TopOrdersFinderStrategyType.BIDS)
                .topOrders(symbol, timestamp);
        String topBidsString = topBids.stream().map(Quote::bidOutputFormat)
                .collect(Collectors.joining("; "));
        return "Best Bids: " + topBidsString;
    }

    @Override
    public String getTopAsks(String symbol, String timestamp) {
        List<Quote> topAsks = topOrderFinderStrategyFactory
                .findStrategy(TopOrdersFinderStrategyType.ASKS)
                .topOrders(symbol, timestamp);
        String topAsksString = topAsks.stream().map(Quote::askOutputFormat)
                .collect(Collectors.joining("; "));
        return "Best Asks: " + topAsksString;
    }
}
