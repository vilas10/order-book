package com.stock.orderbook.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

@Data
@Builder
public class Symbol {
    private String symbol;
    private List<Quote> quotes;
    private Map<String, Integer> quotesIndex;
    private TreeMap<String, PriorityQueue<Quote>> asksCache;
    private TreeMap<String, PriorityQueue<Quote>> bidsCache;
}
