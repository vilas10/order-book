package com.seinterview.orderbook.service;

import org.springframework.stereotype.Service;

@Service
public interface OrderBookService {
    String getTopBids(String symbol, String timestamp);
    String getTopAsks(String symbol, String timestamp);
}
