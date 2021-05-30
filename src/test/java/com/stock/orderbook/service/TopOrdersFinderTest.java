package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TopOrdersFinderTest {
    PriorityQueue<Quote> ordersQueue;
    Symbol symbol;
    Map<String, Integer> quotesIndex;
    TopOrdersFinder topOrdersFinder = new TopOrdersFinder();

    public void initialize(int initialQuoteCount) {
        List<String> startTimes = List.of(
                "2021-02-18T10:10:10.001Z",
                "2021-02-18T10:10:10.002Z",
                "2021-02-18T10:10:10.103Z",
                "2021-02-18T10:10:10.304Z");
        List<String> endTimes = List.of(
                "2021-02-18T10:10:10.101Z",
                "2021-02-18T10:10:10.202Z",
                "2021-02-18T10:10:10.303Z",
                "2021-02-18T10:10:10.404Z");

        List<Double> askPrices = List.of(100.10, 100.20, 100.30, 100.40);
        List<Quote> quotes = IntStream.range(0, startTimes.size())
                .mapToObj(i -> Quote.builder()
                        .startTime(startTimes.get(i))
                        .endTime(endTimes.get(i))
                        .askPrice(askPrices.get(i))
                        .build())
                .collect(Collectors.toList());

        ordersQueue = new PriorityQueue<>(Comparator.comparing(Quote::getAskPrice));
        ordersQueue.addAll(quotes.subList(0, initialQuoteCount));

        quotesIndex = new HashMap<>();

        symbol = Symbol.builder()
                .symbol("TEST")
                .quotes(quotes)
                .quotesIndex(quotesIndex)
                .build();
    }

    @Test
    public void testRemoveInactiveQuotesWhenAllQuotesAreInactive() {
        initialize(4);
        int quotesStartIndex = 4;

        String timestamp = "2021-02-18T10:10:11.000Z";

        PriorityQueue<Quote> outputQueue = topOrdersFinder.findTopOrders(ordersQueue, quotesStartIndex, symbol, timestamp);

        Assertions.assertEquals(0, outputQueue.size());
    }

    @Test
    public void testRemoveInactiveQuotesWhenAllQuotesAreActive() {
        initialize(4);
        int quotesStartIndex = 4;
        String timestamp = "2021-02-18T10:10:09.000Z";

        PriorityQueue<Quote> outputQueue = topOrdersFinder.findTopOrders(ordersQueue, quotesStartIndex, symbol, timestamp);

        Assertions.assertEquals(4, outputQueue.size());
    }

    @Test
    public void testUpdateOrdersQueueToAddNewQuote() {
        initialize(2);
        int quotesStartIndex = 3;
        String timestamp = "2021-02-18T10:10:10.103Z";

        PriorityQueue<Quote> outputQueue = topOrdersFinder.findTopOrders(ordersQueue, quotesStartIndex, symbol, timestamp);

        Assertions.assertEquals(1, outputQueue.size());
        Assertions.assertEquals(3, symbol.getQuotesIndex().get(timestamp));
    }
}
