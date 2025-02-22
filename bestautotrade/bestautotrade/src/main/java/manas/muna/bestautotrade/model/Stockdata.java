package manas.muna.bestautotrade.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Document(collection = "TradeData")
@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stockdata {

    @Id
    StockdataPrimaryKey stockdataPrimaryKey;
    double open;
    double close;
    double high;
    double low;
    String tradePosition; //breakout,high.low
    Expectedmove expectedMove;
    Tradedata tradedata;
    Volumedata volumedata;
    PreviousData previousData;
    String needToCheck;
    String status;
    String statusUpdateDate;
    String stockDirection;
}
