package logvinov.facieAITask.enrichTradeAPI.service;

import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<String> getProductName(String productId);
}
