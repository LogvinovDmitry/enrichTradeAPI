package logvinov.facieAITask.enrichTradeAPI.service;

import logvinov.facieAITask.enrichTradeAPI.dto.TradeData;
import reactor.core.publisher.Flux;

import java.io.InputStream;

public interface TradeService {
    Flux<TradeData> processTrades(InputStream csvStream);
}