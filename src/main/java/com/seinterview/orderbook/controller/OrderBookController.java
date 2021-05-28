package com.seinterview.orderbook.controller;

import com.seinterview.orderbook.model.OrderBookRequest;
import com.seinterview.orderbook.service.OrderBookService;
import com.seinterview.orderbook.service.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/orderbook")
public class OrderBookController {

    @Autowired
    OrderBookService orderBookService;

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
        return orderBookService.getTopBids(request.getSymbol(), request.getTimestamp())
                + "\n"
                + orderBookService.getTopAsks(request.getSymbol(), request.getTimestamp());
    }
}
