package com.stock.orderbook.config;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * CsvQuoteFileLoader class is used to perform the following actions.
 * - Read the input CSV file that contains quotes
 * - Group all the quotes into symbolToQuotesMap
 * - Load the Symbol objects for each symbol associated into symbolMap
 */
@Configuration
public class CsvQuoteFileLoader {
    private static final Logger log = LoggerFactory.getLogger(CsvQuoteFileLoader.class);
    public static final String TIMESTAMP_01_JAN_2021 = "2021-01-01T00:00:00";

    /**
     * Input CSV file resource. Filename is pulled from application.properties.
     */
    @Value("classpath:${quotes.input.csv.file}")
    private Resource csvFileResource;
    /**
     * Input CSV file delimiter. Delimiter is pulled from application.properties.
     */
    @Value("${quotes.input.csv.file.delimiter}")
    private String CSV_FILE_DELIMITER;

    /**
     * Required Limit of Top Orders. Limit value is pulled from application.properties.
     */
    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    /**
     * Symbol map is loaded from input CSV file in this method and a bean is created.
     * @return Map<String, Symbol> Map of Symbol objects for each symbol
     * @throws Exception is thrown if file is missing or failure to parse the CSV file.
     */
    @Bean("symbolMap")
    public Map<String, Symbol> symbolMap() throws Exception {
        log.info("Started Building Symbol Map");
        Map<String, List<Quote>> symbolToQuotesMap = buildQuotesMapFromFile();
        Map<String, Symbol> symbolMap = symbolToQuotesMap
                .entrySet()
                .stream()
                .map(mapToSymbol)
                .collect(Collectors.toMap(Symbol::getSymbol, Function.identity()));

        log.info("Completed Building Symbol Map. Map Size: {}", symbolMap.size());
        symbolMap.forEach(this::initializeCache);

        return symbolMap;
    }

    private Map<String, List<Quote>> buildQuotesMapFromFile() throws Exception {
        String csvFilePath = Paths.get(csvFileResource.getURI()).toString();
        log.info("Parsing CSV Quotes File: " + csvFilePath);

        return readCsvFile(csvFilePath);
    }

    /**
     * Method to parse CSV File
     * @param csvFilePath - path to CSV file
     * @return Map<String, List<Quote>> - All quotes associated to a symbol
     * @throws IOException - is thrown if file is missing at csvFilePath or failure to parse the CSV file.
     */
    private Map<String, List<Quote>> readCsvFile(String csvFilePath) throws IOException {
        Map<String, List<Quote>> symbolToQuotesMap;

        try {
            File csvFile = new File(csvFilePath);
            InputStream fileInputStream = new FileInputStream(csvFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            symbolToQuotesMap = bufferedReader
                    .lines()
                    .skip(1)
                    .map(mapCsvLineToQuote)
                    .collect(Collectors.groupingBy(Quote::getSymbol,
                            Collectors.mapping(Function.identity(), Collectors.toList())));

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("Missing input quotes_2021-02-18.csv file. " +
                    "Please copy file to src/main/resources folder and retry. {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to parse CSV File. Please verify the contents and retry. {}", e.getMessage());
            throw e;
        }

        log.info("Successfully parsed and loaded CSV quotes file");
        log.info("Total Symbols Found: {}", symbolToQuotesMap.size());
        log.info("Total Quotes Loaded: {}", sumOfLengthsOfMapOfLists(symbolToQuotesMap));
        return symbolToQuotesMap;
    }

    /**
     * Adds all the lengths of list value objects in a map
     * @param mapOfLists - input map to calculated the sum
     * @return int sum of lengths of list value objects in mapOfLists
     */
    private int sumOfLengthsOfMapOfLists(Map<String, List<Quote>> mapOfLists) {
        return mapOfLists.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Function to build Quote object for each line of the input CSV file
     */
    private final Function<String, Quote> mapCsvLineToQuote = (line) -> {
        String[] cols = line.split(CSV_FILE_DELIMITER);

        return Quote.builder()
                .symbol(cols[0])
                .marketCenter(cols[1])
                .bidQuantity(Integer.parseInt(cols[2]))
                .askQuantity(Integer.parseInt(cols[3]))
                .bidPrice(Double.parseDouble(cols[4]))
                .askPrice(Double.parseDouble(cols[5]))
                .startTime(cols[6])
                .endTime(cols[7])
                .quoteConditions(cols[8])
                .sipfeedSeq(Integer.parseInt(cols[9]))
                .sipfeed(cols[10])
                .build();
    };

    /**
     * Function to build Symbol objects for each symbol
     */
    private final Function<Map.Entry<String, List<Quote>>, Symbol> mapToSymbol = (entry) -> Symbol.builder()
            .symbol(entry.getKey())
            .quotes(entry.getValue())
            .asksCache(new TreeMap<>())
            .bidsCache(new TreeMap<>())
            .quotesIndex(new HashMap<>())
            .build();

    /**
     * Initializes the caches and quotes index for each symbol
     * @param symbolName - name of symbol
     * @param symbol - Symbol object for which caches are to be updated
     */
    private void initializeCache(String symbolName, Symbol symbol) {
        // Negative bid price is used for comparing to simulate MaxHeap because bids should be ordered from high to low.
        // In case of tie, the startTime is used and the older startTime is selected.
        PriorityQueue<Quote> bidsQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                Comparator.comparing(Quote::getNegativeBidPrice).thenComparing(Quote::getStartTime));

        // Ask price is used for comparison to simulate MinHeap followed by startTime in case of duplicates.
        PriorityQueue<Quote> asksQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                Comparator.comparing(Quote::getAskPrice).thenComparing(Quote::getStartTime));

        // Storing a dummy initial quote for random timestamp i.e. TIMESTAMP_01_JAN_2021
        symbol.getBidsCache().put(TIMESTAMP_01_JAN_2021, bidsQueue);
        symbol.getAsksCache().put(TIMESTAMP_01_JAN_2021, asksQueue);
        symbol.getQuotesIndex().put(TIMESTAMP_01_JAN_2021, 0);
    }
}
