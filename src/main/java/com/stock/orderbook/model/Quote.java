package com.stock.orderbook.model;

import com.stock.orderbook.utils.OutputFormatter;
import lombok.Builder;
import lombok.Data;

/**
 * Quote Pojo  <br>
 * The fields in the class are present in input CSV quotes file. <br>
 * Each Quote object is created of each line in CSV quotes file. <br>
 */
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
        return OutputFormatter.formatPrice(this.askPrice) + " (" + this.askQuantity + ")";
    }

    public String bidOutputFormat() {
        return OutputFormatter.formatPrice(this.bidPrice) + " (" + this.bidQuantity + ")";
    }

    public Double getNegativeBidPrice() {
        return -this.bidPrice;
    }
}
