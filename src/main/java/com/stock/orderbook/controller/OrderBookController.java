package com.stock.orderbook.controller;

import com.stock.orderbook.config.TopOrderFinderStrategyFactory;
import com.stock.orderbook.model.OrderBookRequest;
import com.stock.orderbook.model.OrderType;
import com.stock.orderbook.service.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

/**
 * OrderBookController Rest Controller Class. <br>
 * - Creates endpoints for and directs requests to appropriate strategy implementations <br>
 */
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

    /**
     * GET endpoint to welcome
     * @return
     */
    @GetMapping()
    public String welcome() {
        return "Welcome to the restful implementation of Order Book";
    }

    /**
     * POST /orderbook endpoint to get top bids and top asks for user request for given symbol
     * and timestamp.
     * @param orderBookRequest - An OrderBookRequest class object with symbol and timestamp
     * @return String Response to request as below example.
     *
     * Best Bids: 128.31 (100); 128.31 (700); 128.31 (300); 128.31 (200); 128.31 (300)
     * Best Asks: 128.33 (100); 128.33 (700); 128.33 (300); 128.33 (100); 128.33 (100)
     */
    @PostMapping(path = "/orderbook", consumes = "application/json", produces = "text/plain")
    public String createPerson(@RequestBody OrderBookRequest orderBookRequest) {
        String validatorResponse = requestValidator.validateSymbol(orderBookRequest.getSymbol());
        if (!validatorResponse.isEmpty()) {
            return validatorResponse;
        }
        Instant start = Instant.now();
        String topBids = topOrderFinderStrategyFactory.findStrategy(OrderType.BIDS)
                .topOrders(orderBookRequest.getSymbol(), orderBookRequest.getTimestamp());

        String topAsks = topOrderFinderStrategyFactory.findStrategy(OrderType.ASKS)
                .topOrders(orderBookRequest.getSymbol(), orderBookRequest.getTimestamp());

        Instant finish = Instant.now();
        log.info("Execution Time: {}", Duration.between(start, finish).toMillis());
        log.debug("Result: {}, {}", topBids, topAsks);

        return topBids + "\n" + topAsks;
    }
}
