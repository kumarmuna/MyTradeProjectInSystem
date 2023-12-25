package manas.muna.trade.util;

import com.opencsv.CSVWriter;
import manas.muna.trade.patterns.CandlestickBearishPatterns;
import manas.muna.trade.patterns.CandlestickBullishPatterns;
import manas.muna.trade.vo.CandleStick;
import manas.muna.trade.vo.StockDetails;
import manas.muna.trade.vo.StockRules;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

public class CandleUtil {
    private static String SOLIDGREEN = "SolidGreen";
    private static String HALLOWGREEN = "HallowGreen";
    private static String SOLIDRED = "SolidRed";
    private static String HALLOWRED = "HallowRed";
    public static CandleStick prepareCandleData(String[] prevDayData, String[] todData) {
        String candleType = "";
        if (prevDayData!=null && prevDayData.length!=0) {
            if (Double.parseDouble(todData[4]) < Double.parseDouble(prevDayData[4])) {
                if (Double.parseDouble(todData[1]) < Double.parseDouble(todData[4])) {
                    candleType = HALLOWRED;
                } else if (Double.parseDouble(todData[1]) > Double.parseDouble(todData[4])) {
                    candleType = SOLIDRED;
                }
            } else if (Double.parseDouble(todData[4]) > Double.parseDouble(prevDayData[4])) {
                if (Double.parseDouble(todData[1]) < Double.parseDouble(todData[4])) {
                    candleType = HALLOWGREEN;
                } else if (Double.parseDouble(todData[1]) > Double.parseDouble(todData[4])) {
                    candleType = SOLIDGREEN;
                }
            } else {
                System.out.println("Calculate candletype");
                if (Double.parseDouble(todData[1]) < Double.parseDouble(todData[4])) {
                    candleType = HALLOWGREEN;
                } else if (Double.parseDouble(todData[1]) > Double.parseDouble(todData[4])) {
                    candleType = SOLIDGREEN;
                }
            }
        }
        return CandleStick.builder()
                .open(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todData[1])))
                .close(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todData[4])))
                .low(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todData[3])))
                .high(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todData[2])))
                .candleType(candleType)
                .build();
    }

    public static Map<String, Object> checkBullishStockPatterns(String stockName, List<String[]> stockHistoryData) {
            StringBuilder candelTypesOccur = new StringBuilder();
            StringBuilder entryExit = new StringBuilder();
            Map<String, Object> bullishStockDetails = new HashMap<>();
            boolean isBullishHarami = CandlestickBullishPatterns.isBullishHarami(stockName, stockHistoryData);
            if (isBullishHarami) {
                candelTypesOccur.append("BullishHarami|");
                entryExit.append("Buy-Candle High:SL-Prev low-1");
            }
            boolean isHammer = CandlestickBullishPatterns.isHammer(stockName, stockHistoryData);
            if (isHammer) {
                candelTypesOccur.append("Hammer|");
                entryExit.append("Buy-Next Candle High:SL-Hammer Low");
            }
            boolean isInvertedHammer = CandlestickBullishPatterns.isInvertedHammer(stockName, stockHistoryData);
            if (isInvertedHammer) {
                candelTypesOccur.append("InvertedHammer|");
                entryExit.append("Buy-Next Candle High:SL-Hammer low");
            }
            boolean isBullishEngulfingOccurs = CandlestickBullishPatterns.isBullishEngulfingOccurs(stockName, stockHistoryData);
            if (isBullishEngulfingOccurs) {
                candelTypesOccur.append("BullishEngulfingOccurs|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isMoringstar = CandlestickBullishPatterns.isMornigstar(stockName, stockHistoryData);
            if (isMoringstar) {
                candelTypesOccur.append("Moringstar|");
                entryExit.append("Buy-Candle High:SL-Prev low");
            }
            boolean isPiercingLine = CandlestickBullishPatterns.isPiercingLine(stockName, stockHistoryData);
            if (isPiercingLine) {
                candelTypesOccur.append("PiercingLine|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isThreeWhiteSoldiers = CandlestickBullishPatterns.isThreeWhiteSoldiers(stockName, stockHistoryData);
            if (isThreeWhiteSoldiers) {
                candelTypesOccur.append("ThreeWhiteSoldiers|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isTweezerBottoms = CandlestickBullishPatterns.isTweezerBottoms(stockName, stockHistoryData);
            if (isTweezerBottoms) {
                candelTypesOccur.append("TweezerBottoms|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isDojis = CandlestickBullishPatterns.isDojis(stockName, stockHistoryData);
            if (isDojis) {
                candelTypesOccur.append("Dojis|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isBulishRailwayTracks = CandlestickBullishPatterns.isBulishRailwayTracks(stockName, stockHistoryData);
            if (isBulishRailwayTracks) {
                candelTypesOccur.append("BulishRailwayTracks|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }

            if (isBullishHarami || isHammer || isInvertedHammer || isBullishEngulfingOccurs || isMoringstar
                    || isPiercingLine || isThreeWhiteSoldiers || isTweezerBottoms || isDojis || isBulishRailwayTracks){
                bullishStockDetails.put("candelTypesOccur",candelTypesOccur);
                bullishStockDetails.put("isValidToTrade", true);
                bullishStockDetails.put("entryExit", entryExit);
            }

            return bullishStockDetails;
        }

    public static Map<String, Object> checkBearishStockPatterns(String stockName, List<String[]> stockHistoryData) {
        StringBuilder candelTypesOccur = new StringBuilder();
        StringBuilder entryExit = new StringBuilder();
        Map<String, Object> bearishStockDetails = new HashMap<>();
        boolean isDojis = CandlestickBearishPatterns.isDojis(stockName, stockHistoryData);
        if (isDojis) {
            candelTypesOccur.append("Dojis|");
            entryExit.append("Sell-Candle Low:SL-Candle high+1");
        }
        boolean isHaramiBearish = CandlestickBearishPatterns.isHaramiBearish(stockName, stockHistoryData);
        if (isHaramiBearish) {
            candelTypesOccur.append("HaramiBearish|");
            entryExit.append("Sell-Candle Low:SL-Prev Candle high+1");
        }
        boolean isBearishAbandonedBaby = CandlestickBearishPatterns.isBearishAbandonedBaby(stockName, stockHistoryData);
        if (isBearishAbandonedBaby) {
            candelTypesOccur.append("BearishAbandonedBaby|");
            entryExit.append("Sell-Candle Low:SL-Prev Candle high+1");
        }
        boolean isEngulfingBearish = CandlestickBearishPatterns.isEngulfingBearish(stockName, stockHistoryData);
        if (isEngulfingBearish) {
            candelTypesOccur.append("EngulfingBearish|");
            entryExit.append("Sell-Candle Close:SL-Candle high+1");
        }
        boolean isDarkCloudCover = CandlestickBearishPatterns.isDarkCloudCover(stockName, stockHistoryData);
        if (isDarkCloudCover) {
            candelTypesOccur.append("DarkCloudCover|");
            entryExit.append("Sell-Candle Close:SL-Candle high+1");
        }
        boolean isShootingStar = CandlestickBearishPatterns.isShootingStar(stockName, stockHistoryData);
        if (isShootingStar) {
            candelTypesOccur.append("ShootingStar|");
            entryExit.append("Sell-Candle Low:SL-Candle high+1");
        }
        boolean isEveningStar = CandlestickBearishPatterns.isEveningStar(stockName, stockHistoryData);
        if (isEveningStar) {
            candelTypesOccur.append("EveningStar|");
            entryExit.append("Sell-Candle Low:SL-Prev Candle high+1");
        }
        boolean isBearishRailwayTracks = CandlestickBearishPatterns.isBearishRailwayTracks(stockName, stockHistoryData);
        if (isBearishRailwayTracks) {
            candelTypesOccur.append("BearishRailwayTracks|");
            entryExit.append("Sell-Candle Low:SL-Candle high+1");
        }

        if (isBearishAbandonedBaby || isHaramiBearish || isEngulfingBearish || isDarkCloudCover || isShootingStar || isDojis
            || isEveningStar || isBearishRailwayTracks ){
            bearishStockDetails.put("candelTypesOccur",candelTypesOccur);
            bearishStockDetails.put("isValidToTrade", true);
            bearishStockDetails.put("entryExit", entryExit);
        }

        return bearishStockDetails;
    }

    public static void storeFirstDayFilterStocks(List<StockDetails> data) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1\\stock_details_"+DateUtil.getTodayDate()+".csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeSecondDayFilterStocks(List<StockDetails> data, String fileName) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day2\\"+fileName+"_"+DateUtil.getTodayDate()+".csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeThirdDayFilterStocks(List<StockDetails> data) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day3\\stock_details_"+DateUtil.getTodayDate()+".csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeDataToCSVFile(String path, List<String> data) {
        System.out.println(path);
        try {
            File fl  = new File(path);
            if (!fl.exists()){
                fl.createNewFile();
            }
            FileWriter fw = new FileWriter(path);
            CSVWriter writer = new CSVWriter(fw);
            for (String dt: data){
                writer.writeNext(dt.split(","));
            }
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void storeDataToCSVFileWithArrayData(String path, List<String[]> data) {
        System.out.println(path);
        try {
            File fl  = new File(path);
            if (!fl.exists()){
                fl.createNewFile();
            }
            FileWriter fw = new FileWriter(path);
            CSVWriter writer = new CSVWriter(fw);
            for (String[] dt: data){
                writer.writeNext(dt);
            }
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static StockDetails convertStringToStockDetails(String[] data) {
        return StockDetails.builder()
                .stockName(data[0].split("= ")[1])
                .isGreenRed(data[2].split("= ")[1])
                .volume(Integer.parseInt(data[1].split("= ")[1]))
                .candleTypesOccur(data[3].split("= ")[1])
//                .highVolumeCompareDays(Integer.parseInt(data[3].split("= ")[1]))
                .entryExit(data[4].split("= ")[1])
                .trendDays(Integer.parseInt(data.length>5?data[5]:"0= 0".split("= ")[1]))
                .build();
    }

    public static StockRules prepareStockRulesData(String stockName, List<String[]> stockData) {
        Map<String, Map<String,String>> rules = new HashMap<>();
        Map<String, String> ruleMp = null;
        for (String[] str: stockData) {
            ruleMp = new HashMap<>();
            String[] rule = str[0].split("=");
            if (rule.length>1) {
                String[] candleRule = rule[1].split(":");
                for (String cr : candleRule) {
                    String[] candleTrends = cr.split("-");
                    ruleMp.put(candleTrends[0], candleTrends[1]);
                }
            }
            rules.put(rule[0], ruleMp);
        }
        return StockRules.builder()
                .stockName(stockName)
                .rules(rules)
                .build();
    }

    public static String removeLastCharFromString(String str) {
        return str.substring(0, str.length()-1);
    }
}
