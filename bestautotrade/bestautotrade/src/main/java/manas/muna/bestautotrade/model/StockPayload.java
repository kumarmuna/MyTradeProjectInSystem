package manas.muna.bestautotrade.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPayload {
    String name;
    Map<String, String> requestDetails;
}
