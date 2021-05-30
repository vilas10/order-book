package com.stock.orderbook.service;

import com.stock.orderbook.model.OrderType;
import org.springframework.stereotype.Service;

/**
 * TopOrdersFinderStrategy Interface
 */
@Service
public interface TopOrdersFinderStrategy {
    /**
     * topOrders method to be implemented for getting top orders for each strategy <br>
     * @param symbolName - name of the symbol for which the top orders to be calculated
     * @param timestamp - time at which the top orders to be calculated
     * @return String - Returns string with top orders in required format
     */
    String topOrders(String symbolName, String timestamp);

    /**
     * getStrategyOrderType returns the OrderType enum of the corresponding strategy <br>
     * @return OrderType - returns the OrderType enum of the corresponding strategy <br>
     */
    OrderType getStrategyOrderType();
}
