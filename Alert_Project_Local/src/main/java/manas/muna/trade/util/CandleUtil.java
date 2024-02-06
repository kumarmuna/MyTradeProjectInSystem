package manas.muna.trade.util;

import com.opencsv.CSVWriter;
import manas.muna.trade.constants.CandleConstant;
import manas.muna.trade.constants.CandleTypes;
import manas.muna.trade.constants.MarketMovementType;
import manas.muna.trade.patterns.CandlestickBearishPatterns;
import manas.muna.trade.patterns.CandlestickBullishPatterns;
import manas.muna.trade.vo.CandleStick;
import manas.muna.trade.vo.StockDetails;
import manas.muna.trade.vo.StockRules;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CandleUtil {
    private static String SOLIDGREEN = "SolidGreen";
    private static String HALLOWGREEN = "HallowGreen";
    private static String SOLIDRED = "SolidRed";
    private static String HALLOWRED = "HallowRed";
    private static String candleWiseStockLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\candle_stocks";
    public static CandleStick prepareCandleData(String[] prevDayData, String[] todData) {
        String candleType = "";
        if (prevDayData!=null && prevDayData.length!=0) {
            if (Double.parseDouble(todData[4]) < Double.parseDouble(prevDayData[4])) {
                if (Double.parseDouble(todData[1]) < Double.parseDouble(todData[4])) {
                    candleType = HALLOWRED;
                } else if (Double.parseDouble(todData[1]) >= Double.parseDouble(todData[4])) {
                    candleType = SOLIDRED;
                }
            } else if (Double.parseDouble(todData[4]) > Double.parseDouble(prevDayData[4])) {
                if (Double.parseDouble(todData[1]) < Double.parseDouble(todData[4])) {
                    candleType = HALLOWGREEN;
                } else if (Double.parseDouble(todData[1]) >= Double.parseDouble(todData[4])) {
                    candleType = SOLIDGREEN;
                }
            } else {
                System.out.println("Calculate candletype");
                candleType = SOLIDGREEN;
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
            StringBuilder candleTypesOccur = new StringBuilder();
            StringBuilder entryExit = new StringBuilder();
            Map<String, Object> bullishStockDetails = new HashMap<>();
            boolean isBullishHarami = CandlestickBullishPatterns.isBullishHarami(stockName, stockHistoryData);
            if (isBullishHarami) {
                candleTypesOccur.append("BullishHarami|");
                entryExit.append("Buy-Candle High:SL-Prev low-1");
            }
            boolean isHammer = CandlestickBullishPatterns.isHammer(stockName, stockHistoryData);
            if (isHammer) {
                candleTypesOccur.append("Hammer|");
                entryExit.append("Buy-Next Candle High:SL-Hammer Low");
            }
            boolean isInvertedHammer = CandlestickBullishPatterns.isInvertedHammer(stockName, stockHistoryData);
            if (isInvertedHammer) {
                candleTypesOccur.append("InvertedHammer|");
                entryExit.append("Buy-Next Candle High:SL-Hammer low");
            }
            boolean isBullishEngulfingOccurs = CandlestickBullishPatterns.isBullishEngulfingOccurs(stockName, stockHistoryData);
            if (isBullishEngulfingOccurs) {
                candleTypesOccur.append("BullishEngulfingOccurs|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isMoringstar = CandlestickBullishPatterns.isMornigstar(stockName, stockHistoryData);
            if (isMoringstar) {
                candleTypesOccur.append("Moringstar|");
                entryExit.append("Buy-Candle High:SL-Prev low");
            }
            boolean isPiercingLine = CandlestickBullishPatterns.isPiercingLine(stockName, stockHistoryData);
            if (isPiercingLine) {
                candleTypesOccur.append("PiercingLine|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isThreeWhiteSoldiers = CandlestickBullishPatterns.isThreeWhiteSoldiers(stockName, stockHistoryData);
            if (isThreeWhiteSoldiers) {
                candleTypesOccur.append("ThreeWhiteSoldiers|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isTweezerBottoms = CandlestickBullishPatterns.isTweezerBottoms(stockName, stockHistoryData);
            if (isTweezerBottoms) {
                candleTypesOccur.append("TweezerBottoms|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isDojis = CandlestickBullishPatterns.isDojis(stockName, stockHistoryData);
            if (isDojis) {
//                candleTypesOccur.append("Dojis|");
                candleTypesOccur.append(typeOfDojiCandle(stockName, stockHistoryData).get("type"));
                candleTypesOccur.append("|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }
            boolean isBulishRailwayTracks = CandlestickBullishPatterns.isBulishRailwayTracks(stockName, stockHistoryData);
            if (isBulishRailwayTracks) {
                candleTypesOccur.append("BulishRailwayTracks|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }

            boolean isMyFirstCandle = CandlestickBullishPatterns.isMyFirstCandle(stockName, stockHistoryData);
            if (isMyFirstCandle) {
                candleTypesOccur.append("MyFirstCandle|");
                entryExit.append("Buy-Candle High:SL-Candle low-1");
            }

        boolean isMySecondCandle = CandlestickBullishPatterns.isMySecondCandle(stockName, stockHistoryData);
        if (isMySecondCandle) {
            candleTypesOccur.append("MySecondCandle|");
            entryExit.append("Buy-Candle High:SL-Candle low-1");
        }

            if (isBullishHarami || isHammer || isInvertedHammer || isBullishEngulfingOccurs || isMoringstar
                    || isPiercingLine || isThreeWhiteSoldiers || isTweezerBottoms || isDojis || isBulishRailwayTracks
                    || isMyFirstCandle || isMySecondCandle){
                bullishStockDetails.put("candleTypesOccur",candleTypesOccur);
                bullishStockDetails.put("isValidToTrade", true);
                bullishStockDetails.put("entryExit", entryExit);
            }

            return bullishStockDetails;
        }

    public static Map<String, Object> checkBearishStockPatterns(String stockName, List<String[]> stockHistoryData) {
        StringBuilder candleTypesOccur = new StringBuilder();
        StringBuilder entryExit = new StringBuilder();
        Map<String, Object> bearishStockDetails = new HashMap<>();
        boolean isDojis = CandlestickBearishPatterns.isDojis(stockName, stockHistoryData);
        if (isDojis) {
//            candleTypesOccur.append("Dojis|");
            candleTypesOccur.append(typeOfDojiCandle(stockName, stockHistoryData).get("type"));
            candleTypesOccur.append("|");
            entryExit.append("Sell-Candle Low:SL-Candle high+1");
        }
        boolean isHaramiBearish = CandlestickBearishPatterns.isHaramiBearish(stockName, stockHistoryData);
        if (isHaramiBearish) {
            candleTypesOccur.append("HaramiBearish|");
            entryExit.append("Sell-Candle Low:SL-Prev Candle high+1");
        }
        boolean isBearishAbandonedBaby = CandlestickBearishPatterns.isBearishAbandonedBaby(stockName, stockHistoryData);
        if (isBearishAbandonedBaby) {
            candleTypesOccur.append("BearishAbandonedBaby|");
            entryExit.append("Sell-Candle Low:SL-Prev Candle high+1");
        }
        boolean isEngulfingBearish = CandlestickBearishPatterns.isEngulfingBearish(stockName, stockHistoryData);
        if (isEngulfingBearish) {
            candleTypesOccur.append("EngulfingBearish|");
            entryExit.append("Sell-Candle Close:SL-Candle high+1");
        }
        boolean isDarkCloudCover = CandlestickBearishPatterns.isDarkCloudCover(stockName, stockHistoryData);
        if (isDarkCloudCover) {
            candleTypesOccur.append("DarkCloudCover|");
            entryExit.append("Sell-Candle Close:SL-Candle high+1");
        }
        boolean isShootingStar = CandlestickBearishPatterns.isShootingStar(stockName, stockHistoryData);
        if (isShootingStar) {
            candleTypesOccur.append("ShootingStar|");
            entryExit.append("Sell-Candle Low:SL-Candle high+1");
        }
        boolean isEveningStar = CandlestickBearishPatterns.isEveningStar(stockName, stockHistoryData);
        if (isEveningStar) {
            candleTypesOccur.append("EveningStar|");
            entryExit.append("Sell-Candle Low:SL-Prev Candle high+1");
        }
        boolean isBearishRailwayTracks = CandlestickBearishPatterns.isBearishRailwayTracks(stockName, stockHistoryData);
        if (isBearishRailwayTracks) {
            candleTypesOccur.append("BearishRailwayTracks|");
            entryExit.append("Sell-Candle Low:SL-Candle high+1");
        }

        boolean isMyFirstCandle = CandlestickBearishPatterns.isMyFirstCandle(stockName, stockHistoryData);
        if (isMyFirstCandle) {
            candleTypesOccur.append("MyFirstCandle|");
            entryExit.append("Buy-Candle High:SL-Candle low-1");
        }

        boolean isBearishReversal = CandlestickBearishPatterns.isBearishReversal(stockName, stockHistoryData);
        if (isBearishReversal) {
            candleTypesOccur.append("BearishReversal|");
            entryExit.append("Buy-Candle High:SL-Candle low-1");
        }

        boolean isMySecondCandle = CandlestickBearishPatterns.isMySecondCandle(stockName, stockHistoryData);
        if (isMyFirstCandle) {
            candleTypesOccur.append("MySecondCandle|");
            entryExit.append("Buy-Candle High:SL-Candle low-1");
        }

        if (isBearishAbandonedBaby || isHaramiBearish || isEngulfingBearish || isDarkCloudCover || isShootingStar || isDojis
            || isEveningStar || isBearishRailwayTracks || isMyFirstCandle || isBearishReversal || isMySecondCandle){
            bearishStockDetails.put("candleTypesOccur",candleTypesOccur);
            bearishStockDetails.put("isValidToTrade", true);
            bearishStockDetails.put("entryExit", entryExit);
        }

        return bearishStockDetails;
    }

    public static void storeCandleDataOfStocks(List<StockDetails> data) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\stock\\stock_details_"+DateUtil.getTodayDate()+".csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeFirstDayFilterStocks(List<StockDetails> data) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1\\stock_details_"+DateUtil.getTodayDate()+".csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeFirstDayFilterStocksSameDirection(List<StockDetails> data) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\same_direction\\day1\\stock_details_"+DateUtil.getTodayDate()+".csv";
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeSecondDayFilterStocks(List<StockDetails> data, String fileName) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day2\\"+fileName;
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeThirdDayFilterStocks(List<StockDetails> data, String fileName) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day3\\"+fileName;
        List<String> stockData = new ArrayList<>();
        for (StockDetails sd: data){
            stockData.add(sd.toString());
        }
        storeDataToCSVFile(path, stockData);
    }

    public static void storeFourthDayFilterStocks(List<StockDetails> data, String fileName) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day4\\"+fileName;
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
        System.out.println(data[0]);
        return StockDetails.builder()
                .stockName(data[0].split("= ")[1])
                .isGreenRed(data[2].split("= ")[1])
                .volume(Integer.parseInt(data[1].split("= ")[1]))
                .candleTypesOccur(data[3].split("= ").length>1? data[3].split("= ")[1]: "")
                .thisCandleType(data.length>4? data[4].split("= ").length>1?data[4].split("= ")[1]:"":"")
                .entryExit(data.length>5?data[5].split("= ").length>1?data[5].split("= ")[1]:"":"")
                .trendDays(Integer.parseInt(data.length>6?data[6].split("= ")[1]:"0"))
                .highVolumeCompareDays(Integer.parseInt(data.length>7?data[7].split("= ")[1]:"0"))
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
        if (str.length()==0)
            return str;
        return str.substring(0, str.length()-1);
    }

    public static List<StockDetails> checkIfStockInTop(List<StockDetails> stocks) {
        List<StockDetails> filterTopInTrendStocks = new ArrayList<>();
        for (StockDetails sd: stocks){
            if (sd.getIsGreenRed().equals("GREEN")){
                Map<String, String> hgDetails = StockUtil.getHighDetails(sd.getStockName(),sd.getTrendDays());
                Map<String, String> hgCLoseDetails = StockUtil.getHighCloseDetails(sd.getStockName(),sd.getTrendDays());
                int highCheck = hgCLoseDetails.get("highCloseCandles")==null?hgDetails.get("highCandles")==null?
                        9:Integer.parseInt(hgDetails.get("highCandles")):Integer.parseInt(hgCLoseDetails.get("highCloseCandles"));
                if (highCheck<1){
                    filterTopInTrendStocks.add(sd);
                }
            }else if(sd.getIsGreenRed().equals("RED")){
                Map<String, String> lwDetails = StockUtil.getLowDetails(sd.getStockName(),sd.getTrendDays());
                Map<String, String> lwCLoseDetails = StockUtil.getLowCloseDetails(sd.getStockName(),sd.getTrendDays());
                int lowCheck = lwCLoseDetails.get("lowCloseCandles")==null?lwDetails.get("lowCandles")==null?
                        9:Integer.parseInt(lwDetails.get("lowCandles")):Integer.parseInt(lwCLoseDetails.get("lowCloseCandles"));
                if (lowCheck<1){
                    filterTopInTrendStocks.add(sd);
                }
            }
        }
        return filterTopInTrendStocks;
    }

    public static boolean isStockInTop(StockDetails sd) {
        boolean flag = false;
        if (sd.getIsGreenRed().equals("GREEN")){
            Map<String, String> hgDetails = StockUtil.getHighDetails(sd.getStockName(),sd.getTrendDays());
            Map<String, String> hgCLoseDetails = StockUtil.getHighCloseDetails(sd.getStockName(),sd.getTrendDays());
            int highCheck = hgCLoseDetails.get("highCloseCandles")==null?hgDetails.get("highCandles")==null?
                    9:Integer.parseInt(hgDetails.get("highCandles")):Integer.parseInt(hgCLoseDetails.get("highCloseCandles"));
            if (highCheck<1){
                flag = true;
            }
        }else if(sd.getIsGreenRed().equals("RED")){
            Map<String, String> lwDetails = StockUtil.getLowDetails(sd.getStockName(),sd.getTrendDays());
            Map<String, String> lwCLoseDetails = StockUtil.getLowCloseDetails(sd.getStockName(),sd.getTrendDays());
            int lowCheck = lwCLoseDetails.get("lowCloseCandles")==null?lwDetails.get("lowCandles")==null?
                    9:Integer.parseInt(lwDetails.get("lowCandles")):Integer.parseInt(lwCLoseDetails.get("lowCloseCandles"));
            if (lowCheck<1){
                flag = true;
            }
        }
        return flag;
    }

    public static List<StockDetails> validateOptionStock(List<StockDetails> stocks) {
        List<StockDetails> optionStocks = new ArrayList<>();
        for (StockDetails sd: stocks){
            List<String[]> historyData = StockUtil.loadStockData(sd.getStockName());
            if (sd.getIsGreenRed().equals(MarketMovementType.GREEN)){
                if (validateCandleType(sd,historyData, MarketMovementType.GREENTOREDCHECK))
                    optionStocks.add(sd);
            }else if (sd.getIsGreenRed().equals(MarketMovementType.RED)){
                if (validateCandleType(sd,historyData, MarketMovementType.REDTOGREENCHECK))
                    optionStocks.add(sd);
            }
        }
        return optionStocks;
    }


    public static boolean validateCandleType(StockDetails stockDetails,List<String[]> stockHistoryData, String typeCheck) {
        boolean flag = false;
        if (stockHistoryData==null || stockHistoryData.isEmpty()){
            stockHistoryData = StockUtil.loadStockData(stockDetails.getStockName());
        }
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(1), stockHistoryData.get(0));
        CandleStick c2 = CandleUtil.prepareCandleData(stockHistoryData.get(2), stockHistoryData.get(1));
        CandleStick c3 = CandleUtil.prepareCandleData(stockHistoryData.get(3), stockHistoryData.get(2));
        CandleStick c4 = CandleUtil.prepareCandleData(stockHistoryData.get(4), stockHistoryData.get(3));
        //It will check if market already down last 4days then ignore
        if (typeCheck!=null && !typeCheck.isEmpty() && typeCheck.equals(MarketMovementType.GREENTOREDCHECK) && c2.getCandleType().equals(CandleConstant.SOLID_RED)
            && c3.getCandleType().equals(CandleConstant.SOLID_RED) && c4.getCandleType().equals(CandleConstant.SOLID_RED))
            return false;
        String candleType = stockDetails.getCandleTypesOccur();
        String[] candleTypes = candleType.replace("|",",").split(",");
        for (String candle: candleTypes) {
            switch (candle) {
                case CandleTypes.DOJIS:
                    if (!flag)
                        flag = validateDojisCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.DojiTypes.NEUTRALDOJI:
                    if (!flag)
                        flag = validateNeutralDojisCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.DojiTypes.DRAGONFLYDOJI:
                    if (!flag)
                        flag = validateDragonFlyDojisCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.DojiTypes.GRAVESTONEDOJI:
                    if (!flag)
                        flag = validateGravetoneDojisCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.DojiTypes.LONGLEGGEDDOJI:
                    if (!flag)
                        flag = validateLongleggedDojisCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.DojiTypes.PRICEDOJI:
                    if (!flag)
                        flag = validatePriceDojisCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.BULLISHHARAMI:
                    if (!flag)
                        flag = validateBullishHaramiCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.HAMMER:
                    if (!flag)
                        flag = validateHammerCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.INVERTEDHAMMER:
                    if (!flag)
                        flag = validateInvertedHammerCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.BULLISHENGULFINGOCCURS:
                    if (!flag)
                        flag = validateBullishEngulfingOccursCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.MORINGSTAR:
                    if (!flag)
                        flag = validateMoringstarCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.PIERCINGLINE:
                    if (!flag)
                        flag = validatePiercingLineCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.THREEWHITESOLDIERS:
                    if (!flag)
                        flag = validateThreeWhiteSoldiersCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.TWEEZERBOTTOMS:
                    if (!flag)
                        flag = validateTweezerBottomsCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.BULISHRAILWAYTRACKS:
                    if (!flag)
                        flag = validateBulishRailwayTracksCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.MYFIRSTCANDLE:
                    if (!flag)
                        flag = validateMyFirstCandleCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.HARAMIBEARISH:
                    if (!flag)
                        flag = validateHaramiBearishCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.BEARISHABANDONEDBABY:
                    if (!flag)
                        flag = validateBearishAbandonedBabyCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.ENGULFINGBEARISH:
                    if (!flag)
                        flag = validateEngulfingBearishCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.DARKCLOUDCOVER:
                    if (!flag)
                        flag = validateDarkCloudCoverCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.SHOOTINGSTAR:
                    if (!flag)
                        flag = validateShootingStarCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.EVENINGSTAR:
                    if (!flag)
                        flag = validateEveningStarCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                case CandleTypes.BEARISHRAILWAYTRACKS:
                    if (!flag)
                        flag = validateBearishRailwayTracksCandleType(stockDetails,typeCheck, stockHistoryData);
                    break;
                default:
                    System.out.println("not eligible");
                    flag = false;
            }
        }
        return flag;
    }

    public static boolean validatePriceDojisCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateLongleggedDojisCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;
        boolean trendSequenceBreak = CandleUtil.isTrendSequenceBreak(stockDetails.getStockName(), 6, null, stockDetails.getIsGreenRed());
//        if (!trendSequenceBreak)
//            flag = true;
//        else
            flag = checkStatus(stockDetails, stockHistoryData);
        return flag;
    }

    public static boolean validateGravetoneDojisCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;
        boolean trendSequenceBreak = CandleUtil.isTrendSequenceBreak(stockDetails.getStockName(), 6, null, stockDetails.getIsGreenRed());
//        if (!trendSequenceBreak)
//            flag = true;
//        else
            flag = checkStatus(stockDetails, stockHistoryData);
        return flag;
    }

    public static boolean validateDragonFlyDojisCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;
            boolean trendSequenceBreak = CandleUtil.isTrendSequenceBreak(stockDetails.getStockName(), 6, null, stockDetails.getIsGreenRed());
//            if (!trendSequenceBreak)
//                flag = true;
//            else
                flag = checkStatus(stockDetails, stockHistoryData);
            return flag;
    }

    private static boolean checkStatus(StockDetails stockDetails, List<String[]> stockHistoryData) {
        boolean flag = false;
        CandleStick candleStick = CandleUtil.prepareCandleData(stockHistoryData.get(1), stockHistoryData.get(0));
        int i=1;
        if (stockDetails.getIsGreenRed().equals(MarketMovementType.GREEN)){
            boolean runStatus = true;
            while (runStatus && i<=3){
                CandleStick candleStick1 = CandleUtil.prepareCandleData(stockHistoryData.get(i+1),stockHistoryData.get(i));
                Map<String, Object> candlePatternsDetail = CandleUtil.checkBearishStockPatterns(stockDetails.getStockName(), stockHistoryData.subList(i,10));
                String candleType = candlePatternsDetail.get("candleTypesOccur")==null?"": candlePatternsDetail.get("candleTypesOccur").toString();
                if (!candleType.isEmpty() && candleType.contains("Doji") && candleStick.getClose()>candleStick1.getClose()) {
                    i++;
                    continue;
                }else {
                    CandleStick c1 = CandleUtil.prepareCandleData(stockHistoryData.get(i+1),stockHistoryData.get(i));
                    CandleStick c2 = CandleUtil.prepareCandleData(stockHistoryData.get(i+2),stockHistoryData.get(i+1));
                    if ((c1.getCandleType().equals(CandleConstant.SOLID_RED) || c1.getCandleType().equals(CandleConstant.SOLID_GREEN))
                            && (c2.getCandleType().equals(CandleConstant.SOLID_RED) || c2.getCandleType().equals(CandleConstant.SOLID_GREEN))) {
                        flag = true;
                        runStatus = false;
                    }else
                        runStatus = false;
                }
            }
        }else if (stockDetails.getIsGreenRed().equals(MarketMovementType.RED)){
            boolean runStatus = true;
            while (runStatus && i<=3){
                CandleStick candleStick1 = CandleUtil.prepareCandleData(stockHistoryData.get(i+1),stockHistoryData.get(i));
                if (candleStick1.getCandleType().contains("Doji") && candleStick.getClose()>candleStick1.getClose()) {
                    i++;
                    continue;
                }else {
                    CandleStick c1 = CandleUtil.prepareCandleData(stockHistoryData.get(i+1),stockHistoryData.get(i));
                    CandleStick c2 = CandleUtil.prepareCandleData(stockHistoryData.get(i+2),stockHistoryData.get(i+1));
                    if (c1.getCandleType().equals(CandleConstant.HALLOW_GREEN) && c2.getCandleType().equals(CandleConstant.HALLOW_GREEN)) {
                        flag = true;
                        runStatus = false;
                    }else
                        runStatus = false;
                }
            }
        }
        if(i==4)
            flag = true;
        return flag;
    }

    public static boolean validateNeutralDojisCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateHammerCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateInvertedHammerCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateBullishEngulfingOccursCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateMoringstarCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validatePiercingLineCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateTweezerBottomsCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateThreeWhiteSoldiersCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateBulishRailwayTracksCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateMyFirstCandleCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateHaramiBearishCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateBearishAbandonedBabyCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateEngulfingBearishCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = false;
        for (int i=0;i<5;i++){
            CandleStick preCandle = CandleUtil.prepareCandleData(stockHistoryData.get(i+2), stockHistoryData.get(i+1));
            if (CandleTypes.getAllDojiCanldeNames().contains(preCandle.getCandleType().replace("|",",").split(",")[0])){
                flag = true;
                break;
            }
        }

        CandleStick c3 = CandleUtil.prepareCandleData(stockHistoryData.get(3), stockHistoryData.get(2));
        CandleStick c4 = CandleUtil.prepareCandleData(stockHistoryData.get(4), stockHistoryData.get(3));
        return flag;
    }

    public static boolean validateDarkCloudCoverCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;
        String path = candleWiseStockLocation+"\\DarkCloudCover";
        List<String> fileNames = getFileNamesFromDirectory(path);
        int loopCount = fileNames.size()>5? 5:fileNames.size();
//        for (int i=0; i<loopCount; i++){
//
//        }
        return flag;
    }

    public static boolean validateShootingStarCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateEveningStarCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateBearishRailwayTracksCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateBullishHaramiCandleType(StockDetails stockDetails, String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;

        return flag;
    }

    public static boolean validateDojisCandleType(StockDetails stockDetails,String typeCheck, List<String[]> stockHistoryData) {
        boolean flag = true;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(1), stockHistoryData.get(0));
        CandleStick yesterdayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(2), stockHistoryData.get(1));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockHistoryData.get(3), stockHistoryData.get(2));
        StockDetails day2 = CandleUtil.convertStringToStockDetails(stockHistoryData.get(1));
        StockDetails day3 = CandleUtil.convertStringToStockDetails(stockHistoryData.get(2));
        if (typeCheck.equals(MarketMovementType.GREENTOREDCHECK)){
            if (yesterdayCandle.getCandleType().equals(CandleConstant.SOLID_RED) && prevCandle.getCandleType().equals(CandleConstant.SOLID_RED))
                return false;
            if (day2.getCandleTypesOccur().contains(CandleTypes.DOJIS) && day3.getCandleTypesOccur().contains(CandleTypes.DOJIS))
                return false;
            if (todayCandle.getCandleType().equals(CandleConstant.HALLOW_GREEN)) {
                if ((yesterdayCandle.getCandleType().equals(CandleConstant.SOLID_RED) && prevCandle.getCandleType().equals(CandleConstant.SOLID_RED)))
                    return false;
                flag = true;
            }else if (todayCandle.getCandleType().equals(CandleConstant.SOLID_RED)) {
                flag = true;
            }else if (todayCandle.getCandleType().equals(CandleConstant.HALLOW_RED)) {
                flag = true;
            }else if (todayCandle.getCandleType().equals(CandleConstant.SOLID_GREEN)) {
                if ((yesterdayCandle.getCandleType().equals(CandleConstant.SOLID_RED) && prevCandle.getCandleType().equals(CandleConstant.SOLID_RED))
                    || CandlestickBearishPatterns.isDojis(stockDetails.getStockName(), stockHistoryData))
                    return false;
                flag = true;
            }

        }else if (typeCheck.equals(MarketMovementType.REDTOGREENCHECK)){

        }
        return flag;
    }

    public static boolean isTrendSequenceBreak(String stockName, int trendDays, List<String[]> emaData, String checkType) {
        boolean flag = false;
        List<Integer> ema8Data = new ArrayList<>();
        List<Integer> ema3Data = new ArrayList<>();
        trendDays = trendDays<=7?trendDays:7;
        if (emaData==null || emaData.size()==0){
            //read emadata
            emaData = StockUtil.loadEmaData(stockName);
        }
        for (int i=1; i<trendDays; i++){
            String[] data = emaData.get(i);
            ema8Data.add((int) Double.parseDouble(data[0]));
            ema3Data.add((int) Double.parseDouble(data[1]));
        }
        boolean ema8Check = false;
        boolean ema3Check = false;
        if (checkType.equals(CandleConstant.DESCENDING)) {
            ema8Check = arrayDecendingSortedOrNot(ema8Data.stream().mapToInt(Integer::intValue).toArray(), trendDays-1);
            ema3Check = arrayDecendingSortedOrNot(ema3Data.stream().mapToInt(Integer::intValue).toArray(), trendDays-1);
        }else if (checkType.equals(CandleConstant.ACCEDING)){
            ema8Check = arrayAcendingSortedOrNot(ema8Data.stream().mapToInt(Integer::intValue).toArray(), trendDays-1);
            ema3Check = arrayAcendingSortedOrNot(ema3Data.stream().mapToInt(Integer::intValue).toArray(), trendDays-1);
        }
        return ema8Check && ema3Check;
    }

    static boolean arrayDecendingSortedOrNot(int a[], int n)
    {
        if (n == 1 || n == 0)
            return true;
        return a[n - 1] <= a[n - 2]
                && arrayDecendingSortedOrNot(a, n - 1);
    }

    static boolean arrayAcendingSortedOrNot(int a[], int n)
    {
        if (n == 1 || n == 0)
            return true;
        return a[n - 1] >= a[n - 2]
                && arrayAcendingSortedOrNot(a, n - 1);
    }

    public static List<String> getFileNamesFromDirectory(String directoryPath) {
        List<String> names = null;
        try {
            List<String> files = Files.list(Paths.get(directoryPath))
                    .map(path -> path.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            names = files;
        }catch (Exception e){
            System.out.println("failed to fetch file names from directory");
            e.printStackTrace();
        }
        return names;
    }

    public static Map<String, Object> typeOfDojiCandle(String stockName, List<String[]> historyData) {
        Map<String, Object> mp = new HashMap<>();
        String type= "";
        CandleStick todayCandle = CandleUtil.prepareCandleData(historyData.get(1), historyData.get(0));
        double diff = todayCandle.getClose()-todayCandle.getOpen();
        if (diff<0)
            diff = diff*-1;
        double upParts = todayCandle.getOpen() < todayCandle.getClose()?todayCandle.getHigh()-todayCandle.getClose(): todayCandle.getHigh()-todayCandle.getOpen();
        double downParts = todayCandle.getOpen() < todayCandle.getClose()?todayCandle.getOpen()-todayCandle.getLow():todayCandle.getClose()-todayCandle.getLow();
        if (diff<2 || StockUtil.calculatePercantage(diff, todayCandle.getClose())<0.09
                || diff*3<= downParts || diff*3>=upParts){
            if ((upParts==0 || upParts<0.5) && (downParts==0 || downParts<0.5))
                type = CandleTypes.DojiTypes.PRICEDOJI;
            if (downParts>1 && (upParts==downParts || (upParts>diff*2 && downParts>diff*2)))
                type = CandleTypes.DojiTypes.NEUTRALDOJI;
            if (upParts>diff*2 && downParts>diff*2)
                type = CandleTypes.DojiTypes.LONGLEGGEDDOJI;
            if ((downParts==0|| downParts<2) && upParts>diff*2)
                type = CandleTypes.DojiTypes.GRAVESTONEDOJI;
            if ((upParts==0 || upParts<2) && downParts>diff*2)
                type = CandleTypes.DojiTypes.DRAGONFLYDOJI;
        }

        mp.put("type", type);
        mp.put("isDoji", !type.isEmpty());
        return mp;
    }
}
