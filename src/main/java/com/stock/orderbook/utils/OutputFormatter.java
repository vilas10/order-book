package com.stock.orderbook.utils;

import com.stock.orderbook.model.Quote;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class OutputFormatter {
    public static final String DELIMITER = "; ";

    public String toString(List<Quote> quotes, Function<Quote, String> outputFormat) {
        return quotes.stream().map(outputFormat)
                .collect(Collectors.joining(DELIMITER));
    }
}
