package com.stock.orderbook.service;

import org.springframework.stereotype.Service;

/**
 * RequestValidator interface for input request validation <br>
 */
@Service
public interface RequestValidator {
    String validateSymbol(String symbol);
    // TODO Add Timestamp validator
}
