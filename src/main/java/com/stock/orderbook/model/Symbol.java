package com.stock.orderbook.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Symbol Class
 * Stores the data associated with each symbol for calculating the top bids/asks.
 */
@Data
@Builder
public class Symbol {
    /**
     * symbol - Name of the symbol <br>
     */
    private String symbol;
    /**
     * quotes - List of quotes associated with symbol <br>
     */
    private List<Quote> quotes;
    /**
     * quotesIndex - Hashmap <br>
     * key - timestamp <br>
     * Integer - Index of timestamp equalling the start time in csv file <br>
     */
    private Map<String, Integer> quotesIndex;
    /**
     * asksCache - A treemap to store the timestamps and priority queues <br>
     * key - String timestamp <br>
     * value - Priority Queue of asks at that timestamp <br>
     */
    private TreeMap<String, PriorityQueue<Quote>> asksCache;
    /**
     * bidsCache - A treemap to store the timestamps and priority queues <br>
     * key - String timestamp <br>
     * value - Priority Queue of bids at that timestamp <br>
     */
    private TreeMap<String, PriorityQueue<Quote>> bidsCache;
}
