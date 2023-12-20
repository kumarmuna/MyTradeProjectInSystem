package manas.muna.trade.vo;

import com.opencsv.CSVWriter;
import manas.muna.trade.patterns.CandlestickBearishPatterns;
import manas.muna.trade.patterns.CandlestickBullishPatterns;
import manas.muna.trade.util.StockUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CandleUtil {
    private static String SOLIDGREEN = "SolidGreen";
    private static String HALLOWGREEN = "HallowGreen";
    private static String SOLIDRED = "SolidRed";
    private static String HALLOWRED = "HallowRed";
    public static CandleStick prepareCandleData(String[] prevDayData, String[] todData) {
        String candleType = "";
        if (Double.parseDouble(todData[4]) < Double.parseDouble(prevDayData[4])){
            if(Double.parseDouble(todData[1]) < Double.parseDouble(todData[4])){
                candleType = HALLOWRED;
            }else if(Double.parseDouble(todData[1]) > Double.parseDouble(todData[4])){
                candleType = SOLIDRED;
            }
        }else if (Double.parseDouble(todData[4]) > Double.parseDouble(prevDayData[4])){
            if(Double.parseDouble(todData[1]) < Double.parseDouble(todData[4])){
                candleType = HALLOWGREEN;
            }else if(Double.parseDouble(todData[1]) > Double.parseDouble(todData[4])){
                candleType = SOLIDGREEN;
            }
        }else {
            System.out.println("Calculate candletype");
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
            Map<String, Object> bullishStockDetails = new HashMap<>();
            boolean isBullishHarami = CandlestickBullishPatterns.isBullishHarami(stockName, stockHistoryData);
            if (isBullishHarami)
                candelTypesOccur.append("BullishHarami,");
            boolean isHammer = CandlestickBullishPatterns.isHammer(stockName, stockHistoryData);
            if (isHammer)
                candelTypesOccur.append("Hammer,");
            boolean isInvertedHammer = CandlestickBullishPatterns.isInvertedHammer(stockName, stockHistoryData);
            if (isInvertedHammer)
                candelTypesOccur.append("InvertedHammer,");
            boolean isBullishEngulfingOccurs = CandlestickBullishPatterns.isBullishEngulfingOccurs(stockName, stockHistoryData);
            if (isBullishEngulfingOccurs)
                candelTypesOccur.append("BullishEngulfingOccurs,");
            boolean isMoringstar = CandlestickBullishPatterns.isMoringstar(stockName, stockHistoryData);
            if (isMoringstar)
                candelTypesOccur.append("Moringstar,");
            boolean isPiercingLine = CandlestickBullishPatterns.isPiercingLine(stockName, stockHistoryData);
            if (isPiercingLine)
                candelTypesOccur.append("PiercingLine,");
            boolean isThreeWhiteSoldiers = CandlestickBullishPatterns.isThreeWhiteSoldiers(stockName, stockHistoryData);
            if (isThreeWhiteSoldiers)
                candelTypesOccur.append("ThreeWhiteSoldiers,");
            boolean isTweezerBottoms = CandlestickBullishPatterns.isTweezerBottoms(stockName, stockHistoryData);
            if (isTweezerBottoms)
                candelTypesOccur.append("TweezerBottoms,");
            boolean isDojis = CandlestickBullishPatterns.isDojis(stockName, stockHistoryData);
            if (isDojis)
                candelTypesOccur.append("Dojis,");

            if (isBullishHarami || isHammer || isInvertedHammer || isBullishEngulfingOccurs || isMoringstar
                    || isPiercingLine || isThreeWhiteSoldiers || isTweezerBottoms || isDojis){
                bullishStockDetails.put("candelTypesOccur",candelTypesOccur);
                bullishStockDetails.put("isValidToTrade", true);
            }

            return bullishStockDetails;
        }

    public static Map<String, Object> checkBearishStockPatterns(String stockName, List<String[]> stockHistoryData) {
        StringBuilder candelTypesOccur = new StringBuilder();
        Map<String, Object> bearishStockDetails = new HashMap<>();
        boolean isDojis = CandlestickBearishPatterns.isDojis(stockName, stockHistoryData);
        if (isDojis)
            candelTypesOccur.append("Dojis,");
        boolean isHaramiBearish = CandlestickBearishPatterns.isHaramiBearish(stockName, stockHistoryData);
        if (isHaramiBearish)
            candelTypesOccur.append("HaramiBearish,");
        boolean isBearishAbandonedBaby = CandlestickBearishPatterns.isBearishAbandonedBaby(stockName, stockHistoryData);
        if (isBearishAbandonedBaby)
            candelTypesOccur.append("BearishAbandonedBaby,");
        boolean isEngulfingBearish = CandlestickBearishPatterns.isEngulfingBearish(stockName, stockHistoryData);
        if (isEngulfingBearish)
            candelTypesOccur.append("EngulfingBearish,");
        boolean isDarkCloudCover = CandlestickBearishPatterns.isDarkCloudCover(stockName, stockHistoryData);
        if (isDarkCloudCover)
            candelTypesOccur.append("DarkCloudCover,");
        boolean isShootingStar = CandlestickBearishPatterns.isShootingStar(stockName, stockHistoryData);
        if (isShootingStar)
            candelTypesOccur.append("ShootingStar,");

        if (isBearishAbandonedBaby || isHaramiBearish || isEngulfingBearish || isDarkCloudCover || isShootingStar || isDojis){
            bearishStockDetails.put("candelTypesOccur",candelTypesOccur);
            bearishStockDetails.put("isValidToTrade", true);
        }

        return bearishStockDetails;
    }

    public static void storeFirstDayFilterStocks(List<StockDetails> data) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1\\stock_details.csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeSecondDayFilterStocks(List<StockDetails> data) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day2\\stock_details.csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeThirdDayFilterStocks(List<StockDetails> data) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day3\\stock_details.csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeDataToCSVFile(String path, List<String> data) {
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
}
