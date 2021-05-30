package com.stock.orderbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the implementation of point-in-time order book.
 * It takes timestamp input for a symbol, and returns the top 5 best bids (sorted by price descending & time ascending)
 * and top 5 best asks (sorted by price ascending & time ascending) with quantities.
 *
 * What is order book?
 * - an order book is a list of buy and sell orders for a security or instrument organized by price & time.
 * - order books are used by almost every exchange for assets like stocks, bonds, currencies, and even cryptocurrencies.
 * - There are three parts to an order book: buy orders, sell orders, and order history.
 * - More information: https://www.investopedia.com/terms/o/order-book.asp
 *
 * Output Format:
 *
 * $symbol (time)
 * Best Bids: price1 (quantity); ... price5 (quantity);
 * Best Asks: price1 (quantity); ... price5 (quantity);
 *
 * For example:
 *
 * $FAKE (2000-01-02T10:20:55.015Z)
 * Best Bids: 128.28 (300); 128.27 (100); 128.27 (50); 128.26 (200); 128.24 (100);
 * Best Asks: 128.25 (100); 128.26 (50); 128.28 (50); 128.28 (200); 128.29 (100);
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
