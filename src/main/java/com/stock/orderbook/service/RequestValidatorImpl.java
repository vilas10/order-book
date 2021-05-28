package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RequestValidatorImpl implements RequestValidator {
    private static final Logger log = LoggerFactory.getLogger(RequestValidatorImpl.class);

    private final Map<String, List<Quote>> symbolToQuotesMap;

    public RequestValidatorImpl( Map<String, List<Quote>> symbolToQuotesMap) {
        this.symbolToQuotesMap = symbolToQuotesMap;
    }

    @Override
    public String validateSymbol(String symbol) {
        if (!symbolToQuotesMap.containsKey(symbol)) {
            String invalidSymbolResponse = String.format("Invalid Request: \"%s\" is not a valid symbol", symbol);
            log.error(invalidSymbolResponse);
            return invalidSymbolResponse;
        }
        return "";
    }
}
