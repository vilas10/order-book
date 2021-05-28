package com.seinterview.orderbook.service;

import org.springframework.stereotype.Service;

@Service
public interface RequestValidator {
    String validateSymbol(String symbol);
    // TODO Add Timestamp validator
}
