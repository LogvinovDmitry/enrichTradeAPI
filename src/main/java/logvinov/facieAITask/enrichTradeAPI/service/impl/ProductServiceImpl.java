package logvinov.facieAITask.enrichTradeAPI.service.impl;

import jakarta.annotation.PostConstruct;
import logvinov.facieAITask.enrichTradeAPI.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ReactiveStringRedisTemplate reactiveRedisTemplate;

    @PostConstruct
    public void loadProducts() {
        Flux.using(
                () -> new BufferedReader(new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("largeSizeProduct.csv"),
                        StandardCharsets.UTF_8
                )),
                reader -> Flux.fromStream(reader.lines())
                        .skip(1)
                        .map(line -> line.split(","))
                        .filter(parts -> parts.length == 2)
                        .flatMap(parts -> reactiveRedisTemplate.opsForValue()
                                .set("product:" + parts[0].trim(), parts[1].trim()), 8)
                        .doOnComplete(() -> System.out.println("Finished loading products into Redis")),
                reader -> {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).subscribe();
    }

    public Mono<String> getProductName(String productId) {
        return reactiveRedisTemplate.opsForValue()
                .get("product:" + productId)
                .defaultIfEmpty("Missing Product Name")
                .doOnNext(productName -> {
                    if ("Missing Product Name".equals(productName)) {
                        log.info("Product with ID:{} not in the list largeSizeProduct", productId);
                    }
                });
    }
}