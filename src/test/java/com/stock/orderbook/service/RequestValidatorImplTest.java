package com.stock.orderbook.service;

import com.stock.orderbook.model.Symbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class RequestValidatorImplTest {
    Map<String, Symbol> symbolMap;

    @BeforeEach
    public void initialize() {
        symbolMap = new HashMap<>();
        Symbol symbol = Symbol.builder().build();
        symbolMap.put("VALIDKEY", symbol);
    }

    @Test
    public void testValidateSymbolWithValidSymbol() {
        RequestValidatorImpl requestValidator = new RequestValidatorImpl(symbolMap);
        Assertions.assertEquals("", requestValidator.validateSymbol("VALIDKEY"));
    }

    @Test
    public void testValidateSymbolWithInvalidSymbol() {
        RequestValidatorImpl requestValidator = new RequestValidatorImpl(symbolMap);
        String invalidSymbolResponse = String.format("Request validation failed: '%s' symbol is not valid", "INVALIDKEY");
        Assertions.assertEquals(invalidSymbolResponse, requestValidator.validateSymbol("INVALIDKEY"));
    }
}
