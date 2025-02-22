package manas.muna.trade.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import manas.muna.trade.api.model.Stockdata;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;

import java.util.Date;
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
    double high;
    double low;
    String candleOccur;
    String date;
    String status;

    public static FutureStock prepareFutureFromStockData(Stockdata sData) {
        return FutureStock.builder()
                .stockName(sData.getStockdataPrimaryKey().getStockName())
                .selectType(sData.getStockdataPrimaryKey().getHighIndicatorPos())
//                .exctMrktDirection()
//                .entryPoint(Double.parseDouble(dt[2].split("entry:")[1].trim()))
//                .entryDesc(dt[3].split("entryDesc:")[1].trim())
//                .monLow(Double.parseDouble(dt[4].split("monLow:")[1].trim()))
//                .monHigh(Double.parseDouble(dt[5].split("monHigh:")[1].trim()))
//                .wkHigh(Double.parseDouble(dt[6].split("wkHigh:")[1].trim()))
//                .wkLow(Double.parseDouble(dt[7].split("wkLow:")[1].trim()))
//                .selectType(dt[8].split("selectType:")[1].trim())
//                .rsiVal(Double.parseDouble(dt[9].split("rsiVal:")[1].trim()))
//                .expHighLowDiff(Double.parseDouble(dt[10].split("expHighLowDiff:")[1].trim()))
//                .movePerDayLow(Double.parseDouble(dt[11].split("movePerDayLow:")[1].trim()))
//                .movePerDayHigh(sData.)
//                .exctHigh(sData.getExpectedMove().getTodayHigh())
//                .exctLow(sData.getExpectedMove().getTodayLow())
                .open(sData.getOpen())
                .close(sData.getClose())
                .candleOccur(sData.getStockdataPrimaryKey().getCandleType())
                .date(sData.getStockdataPrimaryKey().getDate())
                .status(sData.getStatus())
                .build();
    }

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
                    FutureStock fs = prepareFutureStockData(dt, "");
                    futureStockMap.put(fs.stockName, fs);
                }
            }
        }
        return futureStockMap;
    }

    public static FutureStock prepareFutureStockData(String[] dt, String runDate) {
        FutureStock futureStock = null;
        if (dt[0].contains("Name:")) {
            String name = dt[0].split("Name:")[1].trim();
             List<String[]> historyData = StockUtil.loadStockData(name);
             String[] todayData = historyData.get(0);
             futureStock = FutureStock.builder()
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
                    .candleOccur(filterCandleOccurs(dt[17]))
                     .date(runDate)
                     .high(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[2])))
                     .low(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[3])))
                    .build();
        }else if(dt[0].contains("StockName=")){
            String name = dt[0].split("StockName=")[1].trim();
            List<String[]> historyData = StockUtil.loadStockData(name);
            String[] todayData = historyData.get(0);
            futureStock = FutureStock.builder()
                    .stockName(dt[0].split("StockName=")[1].trim())
                    .exctMrktDirection(dt[4].split("mrkDirection=")[1].trim().equalsIgnoreCase("UP")? "DOWN": "UP")
//                    .entryPoint(Double.parseDouble(dt[2].split("entry:")[1].trim()))
//                    .entryDesc(dt[3].split("entryDesc:")[1].trim())
//                    .monLow(Double.parseDouble(dt[4].split("monLow:")[1].trim()))
//                    .monHigh(Double.parseDouble(dt[5].split("monHigh:")[1].trim()))
//                    .wkHigh(Double.parseDouble(dt[6].split("wkHigh:")[1].trim()))
//                    .wkLow(Double.parseDouble(dt[7].split("wkLow:")[1].trim()))
//                    .selectType(dt[8].split("selectType:")[1].trim())
//                    .rsiVal(Double.parseDouble(dt[9].split("rsiVal:")[1].trim()))
//                    .expHighLowDiff(Double.parseDouble(dt[10].split("expHighLowDiff:")[1].trim()))
//                    .movePerDayLow(Double.parseDouble(dt[11].split("movePerDayLow:")[1].trim()))
//                    .movePerDayHigh(Double.parseDouble(dt[12].split("movePerDayHigh:")[1].trim()))
//                    .exctHigh(Double.parseDouble(dt[13].split("exctHigh:")[1].trim()))
//                    .exctLow(Double.parseDouble(dt[14].split("exctLow:")[1].trim()))
//                    .open(Double.parseDouble(dt[15].split("open:")[1].trim()))
//                    .close(Double.parseDouble(dt[16].split("close:")[1].trim()))
                    .candleOccur(filterCandleOccurs(dt[3]))
                    .date(runDate)
                    .high(Double.parseDouble(todayData[2]))
                    .low(Double.parseDouble(todayData[3]))
                    .build();
        }
        return futureStock;
    }

    private static String filterCandleOccurs(String candleOccur) {
        String[] candleTypes = new String[0];
        if(candleOccur.contains("candleOccur"))
            candleTypes = candleOccur.split(("candleOccur:"))[1].replace("|", ":").split(":");
        if (candleOccur.contains("candleType"))
            candleTypes = candleOccur.split(("candleType="))[1].replace("|", ":").split(":");
        if (candleTypes.length==1)
            return candleTypes[0];
        else
            for (String candle : candleTypes){
                if (candle.contains("Doji"))
                    return candle;
            }
        return candleTypes[0];
    }
}
