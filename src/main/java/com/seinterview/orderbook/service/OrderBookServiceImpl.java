package com.seinterview.orderbook.service;

import com.seinterview.orderbook.model.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderBookServiceImpl implements OrderBookService {
    @Autowired
    private TopBidsFinder topBidsFinder;

    @Autowired
    private TopAsksFinder topAsksFinder;

    @Override
    public String getTopBids(String symbol, String timestamp) {
        List<Quote> topBids = topBidsFinder.findTopBids(symbol, timestamp);
        String topBidsString = topBids.stream().map(Quote::bidOutputFormat)
                .collect(Collectors.joining("; "));
        return "Best Bids: " + topBidsString;
    }

    @Override
    public String getTopAsks(String symbol, String timestamp) {
        List<Quote> topAsks = topAsksFinder.findTopAsks(symbol, timestamp);
        String topAsksString = topAsks.stream().map(Quote::askOutputFormat)
                .collect(Collectors.joining("; "));
        return "Best Asks: " + topAsksString;
    }
}
