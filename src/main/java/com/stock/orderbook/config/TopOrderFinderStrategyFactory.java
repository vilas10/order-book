package com.stock.orderbook.config;

import com.stock.orderbook.model.OrderType;
import com.stock.orderbook.service.TopOrdersFinderStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TopOrderFinderStrategyFactory class performs following actions. <br>
 * - Creates the Map of various strategies i.e. a map to two strategies to handle top Bids and top Asks. <br>
 */
@Component
public class TopOrderFinderStrategyFactory {
    private final Map<OrderType, TopOrdersFinderStrategy> topOrdersFinderStrategies;

    @Autowired
    public TopOrderFinderStrategyFactory(Set<TopOrdersFinderStrategy> strategySet) {
        topOrdersFinderStrategies = new HashMap<>();
        strategySet.forEach(
                strategy -> topOrdersFinderStrategies.put(strategy.getStrategyOrderType(), strategy));
    }

    public TopOrdersFinderStrategy findStrategy(OrderType orderType) {
        return topOrdersFinderStrategies.get(orderType);
    }
}
