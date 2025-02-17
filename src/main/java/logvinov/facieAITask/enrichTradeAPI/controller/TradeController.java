package logvinov.facieAITask.enrichTradeAPI.controller;

import logvinov.facieAITask.enrichTradeAPI.dto.TradeData;
import logvinov.facieAITask.enrichTradeAPI.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping(value = "/enrich",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_NDJSON_VALUE)
    public ResponseEntity<Flux<TradeData>> enrichTradeData(
            @RequestParam("file") MultipartFile file) throws IOException {

        InputStream csvStream = file.getInputStream();
        Flux<TradeData> enrichedTrades = tradeService.processTrades(csvStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(enrichedTrades);
    }
}