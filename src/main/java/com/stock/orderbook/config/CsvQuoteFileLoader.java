package com.stock.orderbook.config;

import com.stock.orderbook.model.Quote;
import com.stock.orderbook.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class CsvQuoteFileLoader {
    private static final Logger log = LoggerFactory.getLogger(CsvQuoteFileLoader.class);

    @Value("classpath:${quotes.input.csv.file}")
    private Resource csvFileResource;

    @Value("${quotes.input.csv.file.delimiter}")
    private String CSV_FILE_DELIMITER;

    private final Map<String, List<Quote>> symbolToQuotesMap;

    public CsvQuoteFileLoader(Map<String, List<Quote>> symbolToQuotesMap) {
        this.symbolToQuotesMap = symbolToQuotesMap;
    }

    @Bean
    public Map<String, List<Quote>> symbolToQuotesMap() throws Exception {
        String csvFilePath = Paths.get(csvFileResource.getURI()).toString();
        log.info("Parsing CSV Quotes File: " + csvFilePath);

        return readCsvFile(csvFilePath);
    }

    @Bean
    public Map<String, Symbol> symbolMap() {
        Map<String, Symbol> symbolMap = this.symbolToQuotesMap
                .entrySet()
                .stream()
                .map(mapToSymbol)
                .collect(Collectors.toMap(Symbol::getSymbol, Function.identity()));

//        symbolMap.forEach(this::buildAsksPerMinuteMap);
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
            .build();

//    private void buildAsksPerMinuteMap(String symbolName, Symbol symbol) {
//        List<Quote> quotes = symbol.getQuotes();
//        Map<String, PriorityQueue<Quote>> asksPerMinuteMap = new LinkedHashMap<>();
//
//    }
}
