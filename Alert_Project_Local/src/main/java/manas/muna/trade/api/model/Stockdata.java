package manas.muna.trade.api.model;

import lombok.*;

import java.util.Date;


//@Document(collection = "TradeData")
//@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stockdata {
//    @Id
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
}
