package com.seinterview.orderbook.config;

import com.seinterview.orderbook.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Bean
    public Map<String, List<Quote>> symbolToQuotesMap() throws Exception {
        String csvFilePath = Paths.get(csvFileResource.getURI()).toString();
        log.info("Parsing CSV Quotes File: " + csvFilePath);
        return readCsvFile(csvFilePath);
    }

    private Map<String, List<Quote>> readCsvFile(String csvFilePath) {
        Map<String, List<Quote>> symbolToQuotesMap = new HashMap<>();

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
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Exception: Issue while parsing CSV File {}", e.getMessage());
        }

        log.info("Successfully parsed and loaded CSV quotes file");
        log.info("Total Symbols Found: {}", symbolToQuotesMap.size());
        log.info("Total Quotes Loaded: {}", symbolToQuotesMap.values().stream().mapToInt(List::size).sum());
        return symbolToQuotesMap;
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
}
