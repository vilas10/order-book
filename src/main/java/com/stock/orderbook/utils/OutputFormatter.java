package com.stock.orderbook.utils;

import com.stock.orderbook.model.Quote;
import lombok.experimental.UtilityClass;

import java.util.List;
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
     * @param quotes
     * @param outputFormat
     * @return
     */
    public String toString(List<Quote> quotes, Function<Quote, String> outputFormat) {
        return quotes
                .stream()
                .map(outputFormat)
                .collect(Collectors.joining(DELIMITER));
    }

    /**
     * Formats Double price variable values into 2 decimal values
     * @param price
     * @return price in string format with 2 decimals
     */
    public String formatPrice(Double price) {
        return String.format("%.2f", price);
    }
}
