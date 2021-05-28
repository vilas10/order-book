package com.stock.orderbook.model;

import lombok.Getter;

@Getter
public class OrderBookRequest {
    private String symbol;
    private String timestamp;
}
