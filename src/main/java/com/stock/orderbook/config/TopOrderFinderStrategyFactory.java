package com.stock.orderbook.config;

import com.stock.orderbook.model.TopOrdersFinderStrategyType;
import com.stock.orderbook.service.TopOrdersFinderStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class TopOrderFinderStrategyFactory {
    private final Map<TopOrdersFinderStrategyType, TopOrdersFinderStrategy> topOrdersFinderStrategies;

    @Autowired
    public TopOrderFinderStrategyFactory(Set<TopOrdersFinderStrategy> strategySet) {
        topOrdersFinderStrategies = new HashMap<>();
        strategySet.forEach(
                strategy -> topOrdersFinderStrategies.put(strategy.getTopOrderFinderStrategyType(), strategy));
    }

    public TopOrdersFinderStrategy findStrategy(TopOrdersFinderStrategyType strategyName) {
        return topOrdersFinderStrategies.get(strategyName);
    }
}
