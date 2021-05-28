package com.stock.orderbook.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Data
@Builder
public class Symbol {
    private String symbol;
    private List<Quote> quotes;
    private Map<String, Integer> quotesIndex;
    private Map<String, PriorityQueue<Quote>> asksPerMinuteMap;
    private Map<String, PriorityQueue<Quote>> bidsPerMinuteMap;
}
