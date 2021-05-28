# Stock Order Book
This is an Order Book implementation in SpringBoot.

## Development Environment
- SpringBoot 2.5.0
- Java 11.0.11
- IntelliJ IDEA 2021
- Maven

## Running the Application
```
1. Unzip order-book.zip
2a. Download the quotes zip file from https://github.com/Nasdaq/hack/blob/master/software-engineering-interview/quotes_2021-02-18.csv.zip
2b. Unzip and copy the CSV Quotes file into src/main/resources folder
2c. Make sure the filename is quotes_2021-02-18.csv
3. On one terminal, run following command
mvnw spring-boot:run (for windows)
mvn sping-boot:run (for Mac) 
4. On second terminal, run the following curl command to get the top 5 bids and top 5 asks.
Example:

Request:
curl -d "{\"symbol\":\"AAPL\",\"timestamp\":\"2021-02-18T10:10:10.522Z\"}" -H "Content-Type: application/json" -X POST http://localhost:8080/orderbook/

Response:
Best Bids: 129.13 (200); 128.74 (200); 128.73 (100); 128.73 (100); 128.73 (400)
Best Asks: 128.61 (200); 128.61 (5100); 128.61 (300); 128.62 (500); 128.62 (600)
```

