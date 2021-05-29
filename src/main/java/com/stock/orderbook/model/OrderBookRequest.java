package com.stock.orderbook.model;

import lombok.Data;

@Data
public class OrderBookRequest {
    private String symbol;
    private String timestamp;
}
