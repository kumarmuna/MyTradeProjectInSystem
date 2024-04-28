package manas.muna.bestautotrade.model;

import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockdataPrimaryKey {
    String stockName;
    String candleType;
    String highIndicatorPos;
    String date;
}
