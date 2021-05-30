package com.stock.orderbook.model;

import lombok.Data;

/**
 * OrderBookRequest - The input request object used for /orderbook post call
 * - symbol: symbol for which the top bids/asks are required
 * - timestamp: timestamp at which the top bids/asks are calculated for associated symbol
 */
@Data
public class OrderBookRequest {
    private String symbol;
    private String timestamp;
}
