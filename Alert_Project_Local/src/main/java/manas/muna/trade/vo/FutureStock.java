package manas.muna.trade.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@ToString
public class FutureStock {
    String stockName;
    double monHigh;
    double monLow;
    double wkHigh;
    double wkLow;
    String exctMrktDirection;
    double entryPoint;
    String entryDesc;
    String selectType;
    double rsiVal;
    double expHighLowDiff;
    double movePerDayLow;
    double movePerDayHigh;
    double exctHigh;
    double exctLow;
    double open;
    double close;
    String candleOccur;
    @Override
    public String toString() {
        return "Name: "+stockName+" ,expctMrkDirction: "+exctMrktDirection+" ,entry: "+entryPoint+" ,entryDesc: "+entryDesc
                +" ,monLow: "+monLow+" ,monHigh: "+monHigh+" ,wkHigh: "+wkHigh+" ,wkLow: "+wkLow+" ,selectType: "+selectType
                +" ,rsiVal: "+rsiVal+" ,expHighLowDiff: "+expHighLowDiff+" ,movePerDayLow: "+movePerDayLow+" ,movePerDayHigh: "+movePerDayHigh
                +" ,exctHigh: "+exctHigh+" ,exctLow: "+exctLow+" ,open: "+open+" ,close: "+close+" ,candleOccur:"+candleOccur;
    }

    public static Map<String, FutureStock> loadAllData(List<String[]> stockData) {
        Map<String, FutureStock> futureStockMap = new HashMap<>();
        if (stockData != null || stockData.size()!=0){
            for (String[] dt : stockData){
                if (!futureStockMap.containsKey(dt[0])){
                    FutureStock fs = prepareFutureStockData(dt);
                    futureStockMap.put(fs.stockName, fs);
                }
            }
        }
        return futureStockMap;
    }

    private static FutureStock prepareFutureStockData(String[] dt) {
        return FutureStock.builder()
                .stockName(dt[0].split("Name:")[1].trim())
                .exctMrktDirection(dt[1].split("expctMrkDirction:")[1].trim())
                .entryPoint(Double.parseDouble(dt[2].split("entry:")[1].trim()))
                .entryDesc(dt[3].split("entryDesc:")[1].trim())
                .monLow(Double.parseDouble(dt[4].split("monLow:")[1].trim()))
                .monHigh(Double.parseDouble(dt[5].split("monHigh:")[1].trim()))
                .wkHigh(Double.parseDouble(dt[6].split("wkHigh:")[1].trim()))
                .wkLow(Double.parseDouble(dt[7].split("wkLow:")[1].trim()))
                .selectType(dt[8].split("selectType:")[1].trim())
                .rsiVal(Double.parseDouble(dt[9].split("rsiVal:")[1].trim()))
                .expHighLowDiff(Double.parseDouble(dt[10].split("expHighLowDiff:")[1].trim()))
                .movePerDayLow(Double.parseDouble(dt[11].split("movePerDayLow:")[1].trim()))
                .movePerDayHigh(Double.parseDouble(dt[12].split("movePerDayHigh:")[1].trim()))
                .exctHigh(Double.parseDouble(dt[13].split("exctHigh:")[1].trim()))
                .exctLow(Double.parseDouble(dt[14].split("exctLow:")[1].trim()))
                .open(Double.parseDouble(dt[15].split("open:")[1].trim()))
                .close(Double.parseDouble(dt[16].split("close:")[1].trim()))
                .build();
    }
}
