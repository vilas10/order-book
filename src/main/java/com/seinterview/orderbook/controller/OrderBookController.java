package com.seinterview.orderbook.controller;

import com.seinterview.orderbook.config.TopOrderFinderStrategyFactory;
import com.seinterview.orderbook.model.OrderBookRequest;
import com.seinterview.orderbook.model.TopOrdersFinderStrategyType;
import com.seinterview.orderbook.service.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/orderbook")
public class OrderBookController {

    @Autowired
    private TopOrderFinderStrategyFactory topOrderFinderStrategyFactory;

    @Autowired
    RequestValidator requestValidator;

    @GetMapping()
    public String welcome() {
        return "Welcome to the restful implementation of Order Book";
    }

    @PostMapping(consumes = "application/json", produces = "text/plain")
    public String createPerson(@RequestBody OrderBookRequest request) {
        String validatorResponse = requestValidator.validateSymbol(request.getSymbol());
        if (!validatorResponse.isEmpty()) {
            return validatorResponse;
        }

        String topBids = topOrderFinderStrategyFactory
                .findStrategy(TopOrdersFinderStrategyType.BIDS)
                .topOrders(request.getSymbol(), request.getTimestamp());

        String topAsks = topOrderFinderStrategyFactory
                .findStrategy(TopOrdersFinderStrategyType.ASKS)
                .topOrders(request.getSymbol(), request.getTimestamp());

        return topBids + "\n" + topAsks;
    }
}
