package manas.muna.trade.vo;

import lombok.*;
import manas.muna.trade.util.StockUtil;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Stock {
    String name;
    String date;
    double high;
    double low;
    double open;
    double close;
    String expctDirct;
    double prevHigh;
    double prevLow;
    double prevLowOpen;
    double prevHighClose;
    double expct7DayData;
    double expct10DayData;

    public static Stock convertToStock(String s, String date) {
        String[] data = s.split(",");
        String[] nameD = data[0].replace("|",",").split(",");
        return Stock.builder().name(nameD[0])
                .expctDirct(nameD[1])
                .date(date)
                .high(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1].split("high-")[1])))
                .low(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[2].split("low-")[1])))
                .open(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[3].split("open-")[1])))
                .close(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[4].split("close-")[1])))
                .prevHigh(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[5].split("prevHigh-")[1])))
                .prevLow(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[6].split("prevLow-")[1])))
                .prevHighClose(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data.length>7 ? data[7].split("prevHighClose-")[1]:"0.0")))
                .prevLowOpen(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data.length>8 ? data[8].split("prevLowOpen-")[1]:"0.0")))
                .expct10DayData(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data.length>9 ? data[9].split("expct10DayData-")[1]:"0.0")))
                .expct7DayData(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data.length>10 ? data[10].split("expct7DayData-")[1]:"0.0")))
                .build();
     }
}
