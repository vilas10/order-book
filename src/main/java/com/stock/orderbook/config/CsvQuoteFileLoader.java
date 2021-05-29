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

@Configuration
public class CsvQuoteFileLoader {
    private static final Logger log = LoggerFactory.getLogger(CsvQuoteFileLoader.class);
    public static final String TIMESTAMP_01_JAN_2021 = "2021-01-01T00:00:00";

    @Value("classpath:${quotes.input.csv.file}")
    private Resource csvFileResource;

    @Value("${quotes.input.csv.file.delimiter}")
    private String CSV_FILE_DELIMITER;

    @Value("${top.orders.limit}")
    private Integer TOP_ORDERS_LIMIT;

    public Map<String, List<Quote>> symbolToQuotesMap() throws Exception {
        String csvFilePath = Paths.get(csvFileResource.getURI()).toString();
        log.info("Parsing CSV Quotes File: " + csvFilePath);

        return readCsvFile(csvFilePath);
    }

    @Bean("symbolMap")
    public Map<String, Symbol> symbolMap() throws Exception {
        log.info("Building Symbol map");
        Map<String, List<Quote>> symbolToQuotesMap = symbolToQuotesMap();
        log.info("symbolToQuotesMap size: {}", symbolToQuotesMap.size());
        Map<String, Symbol> symbolMap = symbolToQuotesMap
                .entrySet()
                .stream()
                .map(mapToSymbol)
                .collect(Collectors.toMap(Symbol::getSymbol, Function.identity()));

        log.info("Symbol map size: {}", symbolMap.size());
        symbolMap.forEach(this::initializeCache);

        return symbolMap;
    }

    private Map<String, List<Quote>> readCsvFile(String csvFilePath) throws Exception {
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
            log.error("Exception: CSV File not found {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Exception: Issue while parsing CSV File {}", e.getMessage());
            throw e;
        }

        log.info("Successfully parsed and loaded CSV quotes file");
        log.info("Total Symbols Found: {}", symbolToQuotesMap.size());
        log.info("Total Quotes Loaded: {}", sumOfLengthsOfMapOfLists(symbolToQuotesMap));
        return symbolToQuotesMap;
    }

    private int sumOfLengthsOfMapOfLists(Map<String, List<Quote>> mapOfLists) {
        return mapOfLists.values().stream().mapToInt(List::size).sum();
    }

    private final Function<String, Quote> mapCsvLineToQuote = (line) -> {
        String[] cols = line.split(CSV_FILE_DELIMITER);
        // TODO Handle the case if number of columns is not 11
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

    private final Function<Map.Entry<String, List<Quote>>, Symbol> mapToSymbol = (entry) -> Symbol.builder()
            .symbol(entry.getKey())
            .quotes(entry.getValue())
            .asksCache(new TreeMap<>())
            .bidsCache(new TreeMap<>())
            .quotesIndex(new HashMap<>())
            .build();

    private void initializeCache(String symbolName, Symbol symbol) {
        PriorityQueue<Quote> bidsQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                Comparator.comparing(Quote::getNegativeBidPrice).thenComparing(Quote::getStartTime));

        PriorityQueue<Quote> asksQueue = new PriorityQueue<>(TOP_ORDERS_LIMIT,
                Comparator.comparing(Quote::getAskPrice).thenComparing(Quote::getStartTime));

        symbol.getBidsCache().put(TIMESTAMP_01_JAN_2021, bidsQueue);
        symbol.getAsksCache().put(TIMESTAMP_01_JAN_2021, asksQueue);
        symbol.getQuotesIndex().put(TIMESTAMP_01_JAN_2021, 0);
    }
}
