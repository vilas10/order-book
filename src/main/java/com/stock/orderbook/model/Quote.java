package com.stock.orderbook.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Quote {
    private final String symbol;
    private final String marketCenter;
    private final Integer bidQuantity;
    private final Integer askQuantity;
    private final Double bidPrice;
    private final Double askPrice;
    private final String startTime;
    private final String endTime;
    private final String quoteConditions;
    private final Integer sipfeedSeq;
    private final String sipfeed;

    public String askOutputFormat() {
        return this.askPrice + " (" + this.askQuantity + ")";
    }

    public String bidOutputFormat() {
        return this.bidPrice + " (" + this.bidQuantity + ")";
    }

    public Double getNegativeBidPrice() {
        return -this.bidPrice;
    }
}
