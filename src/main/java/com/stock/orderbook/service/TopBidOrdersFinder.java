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

@Component
public class TopBidOrdersFinder implements TopOrdersFinderStrategy {
    private static final Logger log = LoggerFactory.getLogger(TopBidOrdersFinder.class);

    private final TopOrdersFinder topOrdersFinder;

    private final Map<String, Symbol> symbolMap;

    public TopBidOrdersFinder(TopOrdersFinder topOrdersFinder, Map<String, Symbol> symbolMap) {
        this.topOrdersFinder = topOrdersFinder;
        this.symbolMap = symbolMap;
    }
    @Override
    public String topOrders(String symbolName, String timestamp) {
        log.info("Processing top bids for symbol: {} at timestamp: {}", symbolName, timestamp);

        Symbol symbol = symbolMap.get(symbolName);
        String currentSecond = CommonUtil.getCurrentSecond(timestamp);
        if (!symbol.getBidsPerSecondMap().containsKey(currentSecond)) {
            log.info("No Bids found for input timestamp: {}", timestamp);
            return "No Bids Found";
        }

        Set<String> keySet = symbol.getBidsPerSecondMap().keySet();
        List<String> keyList = new ArrayList<>(keySet);
        int currentSecondMapIndex = keyList.indexOf(currentSecond);
        String previousSecond = keyList.get(currentSecondMapIndex-1);
        PriorityQueue<Quote> bidsQueue = new PriorityQueue<>(symbol.getBidsPerSecondMap().get(previousSecond));
        int quotesStartIndex = symbol.getQuotesIndex().get(previousSecond);
        log.info("Finding top bids with info: currentSecond: {}, currentSecondMapIndex: {}, previousSecond: {}, " +
                "quotesStartIndex: {}", currentSecond, currentSecondMapIndex, previousSecond, quotesStartIndex);
        List<Quote> topBids = topOrdersFinder.findTopOrders(bidsQueue, quotesStartIndex, symbolName, timestamp);

        return OutputFormatter.BIDS_PREFIX + OutputFormatter.toString(topBids, Quote::bidOutputFormat);
    }

    @Override
    public TopOrdersFinderStrategyType getTopOrderFinderStrategyType() {
        return TopOrdersFinderStrategyType.BIDS;
    }

}
