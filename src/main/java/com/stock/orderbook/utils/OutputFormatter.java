package com.stock.orderbook.utils;

import com.stock.orderbook.model.Quote;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/***
 * Utility Class for Formatting Output per requirements
 *
 */
@UtilityClass
public class OutputFormatter {
    public static final String DELIMITER = "; ";
    public static final String ASKS_PREFIX = "Best Asks: ";
    public static final String BIDS_PREFIX = "Best Bids: ";

    /**
     * Converts list of quotes to string according to outputFormat provided
     * @param quotes - list of quotes
     * @param outputFormat - formatting function
     * @return String concatenated using delimiter
     */
    public String formatQuotesToString(List<Quote> quotes, Function<Quote, String> outputFormat) {
        return quotes
                .stream()
                .map(outputFormat)
                .collect(Collectors.joining(DELIMITER));
    }

    /**
     * Formats Double price variable values into 2 decimal values
     * @param price - double value
     * @return price in string format with 2 decimals
     */
    public String formatPrice(Double price) {
        return String.format("%.2f", price);
    }

    public String topAsksFormat(List<Quote> topAsks) {
        return OutputFormatter.ASKS_PREFIX + OutputFormatter.formatQuotesToString(topAsks, Quote::askOutputFormat);
    }

    public String topBidsFormat(List<Quote> topBids) {
        return OutputFormatter.BIDS_PREFIX + OutputFormatter.formatQuotesToString(topBids, Quote::bidOutputFormat);
    }

    public static List<Quote> getTopOrdersFromQueue(PriorityQueue<Quote> ordersQueue, Integer queueLimit) {
        List<Quote> orders = new ArrayList<>();
        Quote quote;
        while (orders.size() < queueLimit && (quote = ordersQueue.poll()) != null)
            orders.add(quote);

        ordersQueue.addAll(orders);
        return orders;
    }
}
