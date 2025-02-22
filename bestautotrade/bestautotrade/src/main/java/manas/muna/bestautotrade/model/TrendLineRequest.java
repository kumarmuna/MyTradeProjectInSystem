package manas.muna.bestautotrade.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendLineRequest {
    String stockName;
    String checkPos;
    String[] data;
}
