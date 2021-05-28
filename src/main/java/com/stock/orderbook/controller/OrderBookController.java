package com.stock.orderbook.controller;

import com.stock.orderbook.config.TopOrderFinderStrategyFactory;
import com.stock.orderbook.model.OrderBookRequest;
import com.stock.orderbook.model.TopOrdersFinderStrategyType;
import com.stock.orderbook.service.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
public class OrderBookController {
    private static final Logger log = LoggerFactory.getLogger(OrderBookController.class);

    private final TopOrderFinderStrategyFactory topOrderFinderStrategyFactory;

    private final RequestValidator requestValidator;

    public OrderBookController(TopOrderFinderStrategyFactory topOrderFinderStrategyFactory,
                               RequestValidator requestValidator) {
        this.topOrderFinderStrategyFactory = topOrderFinderStrategyFactory;
        this.requestValidator = requestValidator;
    }

    @GetMapping()
    public String welcome() {
        return "Welcome to the restful implementation of Order Book";
    }

    @PostMapping(path = "/orderbook", consumes = "application/json", produces = "text/plain")
    public String createPerson(@RequestBody OrderBookRequest request) {
        String validatorResponse = requestValidator.validateSymbol(request.getSymbol());
        if (!validatorResponse.isEmpty()) {
            return validatorResponse;
        }
        Instant start = Instant.now();
        String topBids = topOrderFinderStrategyFactory.findStrategy(TopOrdersFinderStrategyType.BIDS)
                .topOrders(request.getSymbol(), request.getTimestamp());

        String topAsks = topOrderFinderStrategyFactory.findStrategy(TopOrdersFinderStrategyType.ASKS)
                .topOrders(request.getSymbol(), request.getTimestamp());

        Instant finish = Instant.now();
        log.info("Execution Time: {}", Duration.between(start, finish).toMillis());

        return topBids + "\n" + topAsks;
    }
}
