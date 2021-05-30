package com.stock.orderbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the implementation of point-in-time order book. <br>
 * It takes timestamp input for a symbol, and returns the top 5 best bids  <br>
 * (sorted by price descending & time ascending) and top 5 best asks  <br>
 * (sorted by price ascending & time ascending) with quantities. <br>
 * <br>
 * What is order book? <br>
 * - an order book is a list of buy and sell orders for a security or instrument organized by price & time. <br>
 * - order books are used by almost every exchange for assets like stocks, bonds, currencies, and cryptocurrencies.<br>
 * - There are three parts to an order book: buy orders, sell orders, and order history. <br>
 * - More information: https://www.investopedia.com/terms/o/order-book.asp  <br>
 * <br>
 * Output Format: <br>
 * <br>
 * $symbol (time)  <br>
 * Best Bids: price1 (quantity); ... price5 (quantity); <br>
 * Best Asks: price1 (quantity); ... price5 (quantity); <br>
 * <br>
 * For example: <br>
 *
 * $FAKE (2000-01-02T10:20:55.015Z) <br>
 * Best Bids: 128.28 (300); 128.27 (100); 128.27 (50); 128.26 (200); 128.24 (100); <br>
 * Best Asks: 128.25 (100); 128.26 (50); 128.28 (50); 128.28 (200); 128.29 (100); <br>
 *
 * @author Vilas
 *
 */

@SpringBootApplication
public class OrderBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBookApplication.class, args);
    }

}
