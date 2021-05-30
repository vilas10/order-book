package com.stock.orderbook.service;

import com.stock.orderbook.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * RequestValidatorImpl for validating the input request for valid parameters <br>
 */
@Service
public class RequestValidatorImpl implements RequestValidator {
    private static final Logger log = LoggerFactory.getLogger(RequestValidatorImpl.class);

    private final Map<String, Symbol> symbolMap;

    public RequestValidatorImpl(Map<String, Symbol> symbolMap) {
        this.symbolMap = symbolMap;
    }

    /**
     * Validates the symbol passed in request
     * @param symbol - input symbol
     * @return String - Returns empty string if symbol is valid else returns invalidSymbolResponse.
     */
    @Override
    public String validateSymbol(String symbol) {
        if (symbolMap.containsKey(symbol)) {
            return "";
        }

        String invalidSymbolResponse = String.format("Request validation failed: '%s' symbol is not valid", symbol);
        log.error(invalidSymbolResponse);
        return invalidSymbolResponse;
    }
}
