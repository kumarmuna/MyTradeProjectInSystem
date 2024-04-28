package manas.muna.trade.api.model;

import lombok.*;

import java.util.Date;

//@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockdataPrimaryKey {
    String stockName;
    String candleType;
    String highIndicatorPos;
    String date;
}
