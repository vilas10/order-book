package com.stock.orderbook.model;

import lombok.Data;

/**
 * OrderBookRequest - The input request object used for /orderbook post call <br>
 * - symbol: symbol for which the top bids/asks are required <br>
 * - timestamp: timestamp at which the top bids/asks are calculated for associated symbol <br>
 */
@Data
public class OrderBookRequest {
    private String symbol;
    private String timestamp;
}
