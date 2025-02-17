package logvinov.facieAITask.enrichTradeAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TradeData {
    private String date;
    private String productName;
    private String currency;
    private Double price;
}