package manas.muna.trade.vo;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StockRules {
    String stockName;
    Map<String, Map<String,String>> rules;

    @Override
    public String toString() {
        return "StockName="+stockName+" Rules="+rules;
    }
}
