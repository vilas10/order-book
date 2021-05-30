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
    public static final String TOO_OLD_TIMESTAMP = "Too old timestamp provided. ";

    /* Constants for BIDS */
    public static final String BIDS_PREFIX = "Best Bids: ";
    public static final String NO_BIDS_FOUND = "No bids found. ";
    public static final String TOO_OLD_TIMESTAMP_FOR_BIDS = TOO_OLD_TIMESTAMP + NO_BIDS_FOUND;

    /* Constants for ASKS */
    public static final String ASKS_PREFIX = "Best Asks: ";
    public static final String NO_ASKS_FOUND = "No asks found. ";
    public static final String TOO_OLD_TIMESTAMP_FOR_ASKS = TOO_OLD_TIMESTAMP + NO_ASKS_FOUND;

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

    /**
     * Output format for top asks
     * @param topAsks - list of top asks
     * @return String - formatted string with top asks
     */
    public String topAsksFormat(List<Quote> topAsks) {
        return OutputFormatter.ASKS_PREFIX + OutputFormatter.formatQuotesToString(topAsks, Quote::askOutputFormat);
    }

    /**
     * Output format for top bids
     * @param topBids - list of top bids
     * @return String - formatted string with top bids
     */
    public String topBidsFormat(List<Quote> topBids) {
        return OutputFormatter.BIDS_PREFIX + OutputFormatter.formatQuotesToString(topBids, Quote::bidOutputFormat);
    }

    /**
     * Retrieves upto topNLimit orders from priority queues
     * @param ordersQueue - queue with processed top orders
     * @param topNLimit - number of top orders to be returned
     * @return List<Quote> - Returns list of top N orders
     */
    public static List<Quote> getTopOrdersFromQueue(PriorityQueue<Quote> ordersQueue, Integer topNLimit) {
        List<Quote> orders = new ArrayList<>();
        Quote quote;
        while (orders.size() < topNLimit && (quote = ordersQueue.poll()) != null)
            orders.add(quote);

        ordersQueue.addAll(orders);
        return orders;
    }
}
