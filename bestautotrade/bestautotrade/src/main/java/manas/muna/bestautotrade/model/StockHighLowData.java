package manas.muna.bestautotrade.model;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHighLowData {
    String date;
    Double open;
    Double close;
    Double high;
    Double low;
    Long volume;
}
