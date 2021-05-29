# Stock Order Book

This is the implementation for Stock Order Book given the quotes in SpringBoot.

## Development Environment

- SpringBoot 2.5.0
- Java 11.0.11
- IntelliJ IDEA 2021
- Maven 3.8.1

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

## Assumptions/Considerations

1. The data has information from different market centers. For providing the top bids/asks, all the data is consolidated
   for each symbol.
2. For calculating top bids/asks,
    - the quotes whose start times are less/older than the input timestamp are only considered.
    - the quotes whose end times are less/older than the input timestamp are discarded.

## Other Design Considerations

### 1. On Demand (Lazy) Data Loading

In the current implementation, the provided input file is loaded into memory for faster access. If the data is too
large, then data could be loaded on request from user for top bids/asks. Drawback of this approach is that user might
get a delayed response.

## Runtimes

### Approach #1: Loading Quotes On-Demand - Without Priority Queue Capacity limit
In this approach, ask and bid priority queues does not have a capacity limit.

**Bids Queue** - Max Heap   
**Asks Queue** - Min Heap

| Symbol    | Timestamp                  | Avg. Execution Time (ms)  |
| --------- |:--------------------------:| -------------------------:|
| AAPL      | 2021-02-18T10:10:00.000Z   | 66                        |
| AAPL      | 2021-02-18T10:20:00.000Z   | 122                       |
| AAPL      | 2021-02-18T10:30:00.000Z   | 180                       |

### Approach #2: Loading Quotes On-Demand - With Priority Queue Capacity limit
In this approach, ask and bid priority queues are made sure that size does not
go beyond the limit 5. Limit is 5 because at the end we only need 5 top bids/asks.
This implementation makes sure only 5 best bids/asks are always maintained.
There is little difference in runtimes from Approach #1, possibly becuase there are
few quotes that are open for long time and hence the queues' size does not grow
much to affect the runtimes.

**Bids Queue** - Min Heap (Maintained max capacity of 5)  
**Asks Queue** - Max Heap (Maintained max capacity of 5)

| Symbol    | Timestamp                  | Avg. Execution Time (ms)  |
| --------- |:--------------------------:| -------------------------:|
| AAPL      | 2021-02-18T10:10:00.000Z   | 61                        |
| AAPL      | 2021-02-18T10:20:00.000Z   | 118                       |
| AAPL      | 2021-02-18T10:30:00.000Z   | 184                       |

### Approach #3: Maintain Indexes and Priority Queues for each second
In this approach, instead of having one ask and bid priority queues, 
there are multiple queues created that stores best 5 bids/asks at the end of each second.
Also, indices of input quotes are maintained to only load the data for only the current second
of the input timestamp. 
i.e. if request is made for timestamp 2021-02-18T10:10:05.333Z, the quotes are not loaded for all
the quotes occured before this timestamp. Instead, at the initial load, queues are captured for 
2021-02-18T10:10:0510:10:05 th second. Hence, only the quotes from 2021-02-18T10:10:05.000Z to 2021-02-18T10:10:05.333Z 
is loaded on user request. This increases the runtime by more than 50 times.

**Bids Queue** - Min Heap (Maintained max capacity of 5)  
**Asks Queue** - Max Heap (Maintained max capacity of 5)

| Symbol    | Timestamp                  | Avg. Execution Time (ms)  |
| --------- |:--------------------------:| -------------------------:|
| AAPL      | 2021-02-18T10:10:00.000Z   | ~1                        |
| AAPL      | 2021-02-18T10:20:00.000Z   | ~1                        |
| AAPL      | 2021-02-18T10:30:00.000Z   | ~1                        |

#### Sample Runs
```
curl -d "{\"symbol\":\"AAPL\",\"timestamp\":\"2021-02-18T10:10:00.000Z\"}" -H "Content-Type: application/json" -X POST http://localhost:8080/order
book/
Best Bids: 128.68 (300); 128.68 (100); 128.68 (200); 128.68 (500); 128.68 (100)
Best Asks: 128.69 (1000); 128.70 (100); 128.70 (300); 128.70 (100); 128.71 (100)

curl -d "{\"symbol\":\"AAPL\",\"timestamp\":\"2021-02-18T10:20:00.000Z\"}" -H "Content-Type: application/json" -X POST http://localhost:8080/order
book/
Best Bids: 128.31 (100); 128.31 (700); 128.31 (300); 128.31 (200); 128.31 (300)
Best Asks: 128.33 (100); 128.33 (100); 128.33 (700); 128.33 (300); 128.33 (200)

curl -d "{\"symbol\":\"AAPL\",\"timestamp\":\"2021-02-18T10:30:00.000Z\"}" -H "Content-Type: application/json" -X POST http://localhost:8080/order
book/
Best Bids: 128.10 (100); 128.10 (200); 128.10 (200); 128.10 (500); 128.10 (100)
Best Asks: 128.12 (100); 128.12 (400); 128.12 (200); 128.12 (100); 128.13 (100)
```
