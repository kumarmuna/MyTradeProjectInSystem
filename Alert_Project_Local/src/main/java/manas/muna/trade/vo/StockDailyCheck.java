package manas.muna.trade.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class StockDailyCheck {
    String name;
    Map<String, String> requestDetails;
}
