package com.stock.orderbook.service;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import com.stock.orderbook.model.TopOrdersFinderStrategyType;
import com.stock.orderbook.utils.CommonUtil;
import com.stock.orderbook.utils.OutputFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Asks
 */
@Component
public class TopAskOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopAskOrdersFinder.class);

    private final TopOrdersFinder topOrdersFinder;

    private final Map<String, Symbol> symbolMap;

    public TopAskOrdersFinder(TopOrdersFinder topOrdersFinder, Map<String, Symbol> symbolMap) {
        this.topOrdersFinder = topOrdersFinder;
        this.symbolMap = symbolMap;
    }

    @Override
    public String topOrders(String symbolName, String timestamp) {
        log.info("Processing top asks for symbol: {} at timestamp: {}", symbolName, timestamp);

        Symbol symbol = symbolMap.get(symbolName);
        String currentSecond = CommonUtil.getCurrentSecond(timestamp);
        if (!symbol.getAsksPerSecondMap().containsKey(currentSecond)) {
            log.info("No Asks found for input timestamp: {}", timestamp);
            return "No Asks Found";
        }

        Set<String> keySet = symbol.getAsksPerSecondMap().keySet();
        List<String> keyList = new ArrayList<>(keySet);
        int currentSecondMapIndex = keyList.indexOf(currentSecond);
        String previousSecond = keyList.get(currentSecondMapIndex-1);
        PriorityQueue<Quote> asksQueue = new PriorityQueue<>(symbol.getAsksPerSecondMap().get(previousSecond));
        int quotesStartIndex = symbol.getQuotesIndex().get(previousSecond);

        log.info("Finding asks with info: currentSecond: {}, currentSecondMapIndex: {}, previousSecond: {}, " +
                "quotesStartIndex: {}", currentSecond, currentSecondMapIndex, previousSecond, quotesStartIndex);

        List<Quote> topAsks = topOrdersFinder.findTopOrders(asksQueue, quotesStartIndex, symbolName, timestamp);

        return OutputFormatter.ASKS_PREFIX + OutputFormatter.toString(topAsks, Quote::askOutputFormat);
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.ASKS;
    }
}
