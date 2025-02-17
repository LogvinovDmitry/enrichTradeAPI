package logvinov.facieAITask.enrichTradeAPI.service.impl;

import logvinov.facieAITask.enrichTradeAPI.dto.TradeData;
import logvinov.facieAITask.enrichTradeAPI.service.ProductService;
import logvinov.facieAITask.enrichTradeAPI.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final ProductService productService;

    @Override
    public Flux<TradeData> processTrades(InputStream csvStream) {
        return Flux.using(
                () -> new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8)),
                reader -> Flux.fromStream(reader.lines())
                        .skip(1)
                        .map(this::parseTradeData)
                        .filter(this::isValidDate)
                        .flatMap(this::enrichTradeDataWithProductName, 8),
                reader -> {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        log.error("Error closing BufferedReader", e);
                    }
                }
        );
    }

    private TradeData parseTradeData(String line) {
        String[] columns = line.split(",");
        String date = columns[0];
        String productName = columns[1];
        String currency = columns[2];
        double price = Double.parseDouble(columns[3]);

        return new TradeData(date, productName, currency, price);
    }

    private boolean isValidDate(TradeData tradeData) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            formatter.parse(tradeData.getDate());
            return true;
        } catch (Exception e) {
            log.info("Invalid date format: {}, ID: {}", tradeData.getDate(), tradeData.getProductName());
            return false;
        }
    }

    private Mono<TradeData> enrichTradeDataWithProductName(TradeData tradeData) {
        return productService.getProductName(tradeData.getProductName())
                .map(productName -> {
                    tradeData.setProductName(productName);
                    return tradeData;
                });
    }
}