# Stock Order Book
This is a Order Book implementation in SpringBoot.

## Development Environment
- SpringBoot 2.5.0
- Java 11.0.11
- IntelliJ IDEA 2021

## Running the Application
```
1. Unzip order-book.zip
2. On one terminal, run following command
mvnw spring-boot:run (for windows)
mvn sping-boot:run (for Mac) 
3. On second terminal, run the following curl command to get the top 5 bids and top 5 asks.
curl -d "{\"symbol\":\"AAPL\",\"timestamp\":\"2021-02-18T10:01:33.522Z\"}" -H "Content-Type: application/json" -X POST http://localhost:8080/orderbook/
```

