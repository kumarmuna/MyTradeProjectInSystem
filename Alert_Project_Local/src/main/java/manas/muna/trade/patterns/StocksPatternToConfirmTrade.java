package manas.muna.trade.patterns;

import manas.muna.trade.constants.CandleConstant;
import manas.muna.trade.stocksRule.StocksRuleCreateUpdateJob;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockPropertiesUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;
import manas.muna.trade.vo.ExpectedCandle;
import manas.muna.trade.vo.StockDetails;
import org.jsoup.internal.StringUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class StocksPatternToConfirmTrade {
    public static void validateStocksToConfirm() {
        String fileName = "";
//        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1\\stock_details_"+ DateUtil.getYesterdayDate()+".csv";
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        try{
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            path = path+"\\"+files.get(0);
            fileName = files.get(0);
        }catch (Exception e){
            System.out.println("StocksPatternToConfirmTrade extract file failed....");
            System.exit(0);
        }
        List<String[]> stockData = StockUtil.readFileData(path);
        List<StockDetails> filteredStocks = new ArrayList<>();
        List<StockDetails> filteredStocksNotClear = new ArrayList<>();
//        String testStockName = "COFFEEDAY.NS";
//        List<String[]> testStockData = new ArrayList<>();
//        for (String[] str: stockData){
//            if (str[0].split("= ")[1].equals(testStockName)){
//                testStockData.add(str);
//            }
//        }
//        if (!testStockData.isEmpty() && testStockData.size()!=0) {
//            for (String[] sd : testStockData) {
        if (!stockData.isEmpty() && stockData.size()!=0) {
            for (String[] sd : stockData) {
                StockDetails stockDetails = CandleUtil.convertStringToStockDetails(sd);
                List<String[]> stockHistoryData = StockUtil.loadStockData(stockDetails.getStockName());
                CandleStick todayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(1), stockHistoryData.get(0));
                CandleStick yesdayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(2), stockHistoryData.get(1));

                if (stockDetails.getIsGreenRed().equals("GREEN")) {
                    if (todayCandle.getCandleType().equals("SolidRed") && todayCandle.getClose() < yesdayCandle.getClose()) {
                        filteredStocks.add(stockDetails);
                    }else
                        filteredStocksNotClear.add(stockDetails);
                } else if (stockDetails.getIsGreenRed().equals("RED")) {
                    if (todayCandle.getCandleType().equals("HallowGreen") && todayCandle.getClose() > yesdayCandle.getClose()) {
                        filteredStocks.add(stockDetails);
                    }else
                        filteredStocksNotClear.add(stockDetails);
                }
            }

            //Store filtered stocks to day2
            filteredStocks = StockUtil.separateGreenAndRedStockThenSortBasedOnTrenddays(filteredStocks);
            CandleUtil.storeSecondDayFilterStocks(filteredStocks, fileName);

            filteredStocksNotClear = StockUtil.separateGreenAndRedStockThenSortBasedOnTrenddays(filteredStocksNotClear);
            CandleUtil.storeSecondDayFilterStocks(filteredStocksNotClear, "notclear"+fileName);
        }
    }

    public static List<StockDetails> finalizeStocks() {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        String testfileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day2";
        List<StockDetails> finalizeStocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            for (String file: files){
                if (!file.contains(DateUtil.getTodayDate()))
                    continue;
//                testfileLocation = testfileLocation + "\\" + files.get(3);
//            List<String[]> stockData = StockUtil.readFileData(path);
                List<String[]> stockData = StockUtil.readFileData(path+"\\"+file);
                if (!stockData.isEmpty() && stockData.size() != 0) {
                    for (String[] sd : stockData) {
                        StockDetails stockDetails = CandleUtil.convertStringToStockDetails(sd);
                        List<String[]> stockHistoryData = StockUtil.loadStockData(stockDetails.getStockName());
//                    stockHistoryData.remove(0);
                        CandleStick todayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(1), stockHistoryData.get(0));
                        CandleStick yesdayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(2), stockHistoryData.get(1));

                        if (stockDetails.getIsGreenRed().equals("GREEN")) {
                            if (yesdayCandle.getCandleType().equals("HallowGreen")) {
                                finalizeStocks.add(stockDetails);
                            }
                        } else if (stockDetails.getIsGreenRed().equals("RED")) {
                            if (yesdayCandle.getCandleType().equals("SolidRed")) {
                                finalizeStocks.add(stockDetails);
                            }
                        }
                    }
                }
            }

            //Store filtered stocks to day2
            finalizeStocks = StockUtil.separateGreenAndRedStockThenSortBasedOnTrenddays(finalizeStocks);
//            for (StockDetails sd : finalizeStocks){
//                System.out.println(sd);
//            }

            }catch (Exception e){
            System.out.println("StocksPatternToConfirmTrade extract file failed....");
            System.exit(0);
        }
        return finalizeStocks;
    }

    private static void divideBasedLogic() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        List<StockDetails> finalizeStocks = new ArrayList<>();
        List<StockDetails> stocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            fileLocation = fileLocation + "\\" + files.get(0);
            System.out.println("Reading from-"+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            for (String[] str: stockData){
                StockDetails sd = CandleUtil.convertStringToStockDetails(str);
                stocks.add(sd);
            }
            List<StockDetails> volStocks = StockUtil.sortStockDataBasedOnCompareDays(stocks);
            CandleUtil.storeThirdDayFilterStocks(volStocks, "Volume_Filter_"+DateUtil.getTodayDate());
            List<StockDetails> lowHighStocks = CandleUtil.checkIfStockInTop(stocks);
            CandleUtil.storeThirdDayFilterStocks(lowHighStocks, "Trande_On_Top_Filter_"+DateUtil.getTodayDate());
            List<StockDetails> stockOnlyCorrectDirection = finalizeStocks();
            CandleUtil.storeThirdDayFilterStocks(stockOnlyCorrectDirection, "Same_Direction_Filter_"+DateUtil.getTodayDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void findCommonStockUsingAllCriteria() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day3";
        List<StockDetails> filterStocks = new ArrayList<>();
        List<List<String[]>> stocks = new ArrayList<>();
        int size = 99999;
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            for (String fileName : files){
                if (!fileName.contains(DateUtil.getTodayDate())){
                    continue;
                }
                List<String[]> stockData = StockUtil.readFileData(fileLocation+ "\\" + fileName);
                if (size > stockData.size())
                    size = stockData.size();
                stocks.add(stockData);
            }
            stocks.stream().sorted();

            if (size == stocks.get(0).size()) {
                for (String[] stk: stocks.get(0)) {
                    StockDetails sd = CandleUtil.convertStringToStockDetails(stk);
                    boolean a= false,b = false,c = false;
                    String stockName = stk[0];

                    a = true;
                    for (String[] st1 : stocks.get(1)) {
                        if (stockName.equals(st1[0])) {
                            b = true;
                            break;
                        }
                    }
                    for (String[] st2 : stocks.get(2)) {
                        if (stockName.equals(st2[0])) {
                            c = true;
                            break;
                        }
                    }
                    if (a && b && c){
                        filterStocks.add(sd);
                    }
                }
            }else if (size == stocks.get(1).size()) {
                for (String[] stk: stocks.get(1)) {
                    StockDetails sd = CandleUtil.convertStringToStockDetails(stk);
                    boolean a= false,b = false,c = false;
                    String stockName = stk[0];
                    b = true;
                    for (String[] st : stocks.get(0)) {
                        if (stockName.equals(st[0])){
                            a = true;
                            break;
                        }
                    }
                    for (String[] st2 : stocks.get(2)) {
                        if (stockName.equals(st2[0])){
                            c = true;
                            break;
                        }
                    }
                    if (a && b && c){
                        filterStocks.add(sd);
                    }
                }
            }else if (size == stocks.get(2).size()) {
                for (String[] stk: stocks.get(2)) {
                    StockDetails sd = CandleUtil.convertStringToStockDetails(stk);
                    boolean a= false,b = false,c = false;
                    String stockName = stk[0];
                    c = true;
                    for (String[] st1 : stocks.get(1)) {
                        if (stockName.equals(st1[0])){
                            b = true;
                            break;
                        }
                    }
                    for (String[] st : stocks.get(0)) {
                        if (stockName.equals(st[0])){
                            a = true;
                            break;
                        }
                    }
                    if (a && b && c){
                        filterStocks.add(sd);
                    }
                }
            }
            filterStocks = StockUtil.sortStockDataBasedOnVolumeSizeThenCompareDays(filterStocks);
            CandleUtil.storeFourthDayFilterStocks(filterStocks, "common-stocks-"+DateUtil.getTodayDate());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void validateStocksToTradeOption() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        List<StockDetails> finalizeStocks = new ArrayList<>();
        List<StockDetails> stocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            fileLocation = fileLocation + "\\" + files.get(0);
            System.out.println("Reading from-"+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            List<String> optionStockNames = StockPropertiesUtil.getOptionStockSymbol();
            for (String[] str: stockData){
                if (optionStockNames.contains(str[0].split("= ")[1].split(".NS")[0])) {
                    StockDetails sd = CandleUtil.convertStringToStockDetails(str);
                    stocks.add(sd);
                }
            }
            List<StockDetails> verifyOptionStocks = CandleUtil.validateOptionStock(stocks);
            for (StockDetails sd: verifyOptionStocks){
                System.out.println(sd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void findStocksNoTradeBreak() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
//        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\stocks";
        List<StockDetails> noTradeBreakStocks = new ArrayList<>();
        List<StockDetails> tradeBreakStocks = new ArrayList<>();
        List<String[]> stocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int i = 0;
            fileLocation = fileLocation + "\\" + files.get(i);
            System.out.println("Reading from-"+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
//            List<String> optionStockNames = StockPropertiesUtil.getOptionStockSymbol();
            for (String[] str: stockData){
                StockDetails sd = StockUtil.prepareCandleData(str);
//                if (!sd.getStockName().equals("BIRLACABLE.NS")){
//                    continue;
//                }
                String checkType = "";
                if(sd.getIsGreenRed().equals("GREEN"))
                    checkType = CandleConstant.DESCENDING;
                else if (sd.getIsGreenRed().equals("RED"))
                    checkType = CandleConstant.ACCEDING;
                List<String[]> emaData = StockUtil.loadEmaData(sd.getStockName());
//                emaData = emaData.subList(i, emaData.size()-1);
                if (CandleUtil.isTrendSequenceBreak(sd.getStockName(), sd.getTrendDays(),emaData,checkType)) {
//                if (CandleUtil.isTrendSequenceBreak(sd.getStockName(), 6,null,checkType)) {
//                    if(!sd.getCandleTypesOccur().contains("Doji") && CandleUtil.validateCandleType(sd, null, null))
                        noTradeBreakStocks.add(sd);
                        stocks.add(str);
                }
//                else
//                    tradeBreakStocks.add(sd);
            }
            noTradeBreakStocks = StockUtil.separateGreenAndRedStockThenSortBasedOnTopInTrend(noTradeBreakStocks);
            tradeBreakStocks = StockUtil.separateGreenAndRedStockThenSortBasedOnTopInTrend(tradeBreakStocks);
//            StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\same_direction\\day2\\no_trend_break_stocks_"+DateUtil.getTodayDate(),stocks);
            for (StockDetails sd: noTradeBreakStocks){
                System.out.println(sd);
            }
//            System.out.println("---------------------");
//            for (StockDetails sd: tradeBreakStocks){
//                System.out.println(sd);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void storeCandleWiseStock() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\stocks";
        List<StockDetails> finalizeStocks = new ArrayList<>();
        List<StockDetails> stocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            fileLocation = fileLocation + "\\" + files.get(0);
            System.out.println("Reading from-"+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            Map<String, List<String[]>> candleBuckets = new HashMap<>();
            List<String[]> listStocks;
            for (String[] str: stockData){
                StockDetails sd = StockUtil.prepareCandleData(str);
                String cType = sd.getCandleTypesOccur().replace("|",",");
                String[] cTypes = cType.split(",");
                for (String candleType: cTypes) {
                    if (candleBuckets.containsKey(candleType)) {
                        listStocks = candleBuckets.get(candleType);
                        listStocks.add(str);
                        candleBuckets.replace(candleType, listStocks);
                    } else if (!candleBuckets.containsKey(candleType)) {
                        listStocks = new ArrayList<>();
                        listStocks.add(str);
                        candleBuckets.put(candleType, listStocks);
                    }
                }
            }
            Set<String> keys = candleBuckets.keySet();
            String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\candle_stocks";
            for (String key: keys){
                String fpath = path+"\\"+key+"\\"+DateUtil.getTodayDate();
                List<String[]> value = candleBuckets.get(key);
                StockUtil.storeFile(fpath, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void filterStocksBasedPreviousCandles() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        List<StockDetails> finalizeStocks = new ArrayList<>();
        List<StockDetails> stocks = new ArrayList<>();
        List<String[]> stocksToStore = new ArrayList<>();
        List<String[]> topInTrendStock = new ArrayList<>();
        List<String[]> optionStocksToStore = new ArrayList<>();
        Set<String> optionStockNames = StockUtil.loadOptionStockNames();
//        Map<String, String> resultDates = ReadResultsDateDataJob.getResultCalander();
        int i = 6;
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            fileLocation = fileLocation + "\\" + files.get(0);
            System.out.println("Reading from-" + fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            boolean storeIndicator = false;
            for (String[] str: stockData){
                storeIndicator = false;
                StockDetails sd = StockUtil.prepareCandleData(str);
//                String resultDate = resultDates.get(sd.getStockName().replace(".NS",""));
//                if (DateUtil.getDateDiffFromToday(resultDate) <= 5)
//                    continue;
 //              if (!sd.getStockName().equals("AUBANK.NS")){
//                    continue;
//                }
                String cType = sd.getCandleTypesOccur().replace("|",",");
                String[] cTypes = cType.split(",");
                if (cTypes[0].contains("Doji")){
                    List<String[]> stockHistory = StockUtil.loadStockData(sd.getStockName());
//                    stockHistory = stockHistory.subList(1, i);
                    CandleStick candleStick1 = CandleUtil.prepareCandleData(stockHistory.get(1),stockHistory.get(0));
                    CandleStick candleStick2 = CandleUtil.prepareCandleData(stockHistory.get(2),stockHistory.get(1));
                    System.out.println(sd.getStockName());
                    if (CandleUtil.validateCandleType(sd,stockHistory, null)){
//                        String checkType = "";
//                        if(sd.getIsGreenRed().equals("GREEN"))
//                            checkType = CandleConstant.DESCENDING;
//                        else if (sd.getIsGreenRed().equals("RED"))
//                            checkType = CandleConstant.ACCEDING;
//                        if (!CandleUtil.isTrendSequenceBreak(sd.getStockName(), sd.getTrendDays(), null, checkType))
                            stocks.add(sd);
                            storeIndicator = true;
                    }
                }else {
                    stocks.add(sd);
                    storeIndicator = true;
                }
                if (storeIndicator && CandleUtil.isStockInTop(sd)){
                    stocksToStore.add(str);
                    if (optionStockNames.contains(sd.getStockName().split(".NS")[0]))
                        optionStocksToStore.add(str);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        stocks = CandleUtil.checkIfStockInTop(stocks);
        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\stocks\\filter_stock_"+DateUtil.getTodayDate(), stocksToStore);
        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\option_stocks\\filter_stock_"+DateUtil.getTodayDate(), optionStocksToStore);
//        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\top_in_trend_stocks\\filter_stock_"+DateUtil.getTodayDate(), topInTrendStock);
        System.out.println("...................");
//        for (String[] ss: stocksToStore){
//            System.out.println(Arrays.toString(ss));
//        }
//        System.out.println("...................");
        for (String[] ss: optionStocksToStore){
            Map<String, String> volDetails = StockUtil.getVolumeDetails(ss[0].split("= ")[1], null, 1);
//            System.out.println(Arrays.toString(ss));
        }
//        System.out.println("...................");
    }

    private static void findStocksNoTradeBreakAndVolDetails() {
//        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\stocks";
        List<String[]> noTradeBreakStocks = new ArrayList<>();
        List<StockDetails> tradeBreakStocks = new ArrayList<>();
        List<String[]> stocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int i = 0;
            fileLocation = fileLocation + "\\" + files.get(i);
            System.out.println("Reading from-"+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
//            List<String> optionStockNames = StockPropertiesUtil.getOptionStockSymbol();
            for (String[] str: stockData){
                StockDetails sd = StockUtil.prepareCandleData(str);
//                if (!sd.getStockName().equals("BIRLACABLE.NS")){
//                    continue;
//                }
                String checkType = "";
                if(sd.getIsGreenRed().equals("GREEN"))
                    checkType = CandleConstant.DESCENDING;
                else if (sd.getIsGreenRed().equals("RED"))
                    checkType = CandleConstant.ACCEDING;
                List<String[]> emaData = StockUtil.loadEmaData(sd.getStockName());
                emaData = emaData.subList(i, emaData.size()-1);
                if (CandleUtil.isTrendSequenceBreak(sd.getStockName(), sd.getTrendDays(),emaData,checkType)) {
                    List<String[]> historyData = StockUtil.loadStockData(sd.getStockName());
                    historyData = historyData.subList(i, historyData.size()-1);
                    Map<String,String> volumeDetails = StockUtil.getVolumeDetails(sd.getStockName(), historyData, sd.getTrendDays());
                    noTradeBreakStocks.add(new String[]{sd.getStockName(), sd.getIsGreenRed(),sd.getCandleTypesOccur(),String.valueOf(sd.getVolume()),
                            volumeDetails.get("highVol"),volumeDetails.get("lowVol"),
                    volumeDetails.get("avgVol"),volumeDetails.get("highVolPos"),volumeDetails.get("lowVolPos"),
                    sd.getVolume()<Integer.parseInt(volumeDetails.get("avgVol"))?"LESS":"MORE"});
                    stocks.add(str);
                }
            }
//            noTradeBreakStocks = StockUtil.separateGreenAndRedStockThenSortBasedOnTopInTrend(noTradeBreakStocks);
            tradeBreakStocks = StockUtil.separateGreenAndRedStockThenSortBasedOnTopInTrend(tradeBreakStocks);
            StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\same_direction\\day2\\no_trend_break_stocks_"+DateUtil.getTodayDate(),stocks);
            for (String[] sd: noTradeBreakStocks){
                System.out.println(Arrays.asList(sd));
            }
//            System.out.println("---------------------");
//            for (StockDetails sd: tradeBreakStocks){
//                System.out.println(sd);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Map<String, Double> getPrevMonthHighLow(String stockName) {
        double low = 99999.99;
        double high = 0.0;
//        String highDate = "";
//        String lowDate = "";
        Map<String, Double> monthData = new HashMap<>();
        try{
            String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data_monthly\\"+stockName+".csv";
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            Collections.reverse(stockData);
            Date today = DateUtil.convertStrToDate(DateUtil.getPreviousMonthDate(), "yyyy_MM_dd");
            for (String[] sd: stockData){
                boolean flag = DateUtil.isDateInThisMonth(sd[0], "yyyy-MM-dd",today.getMonth());
                if (flag){
                    double h = Double.parseDouble(sd[2]);
                    double l = Double.parseDouble(sd[3]);
                    if (high < h) {
                        high = h;
//                        highDate = sd[0];
                    }
                    if (l < low) {
                        low = l;
//                        lowDate = sd[0];
                    }
                    break;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        monthData.put("high", StockUtil.convertDoubleToTwoPrecision(high));
        monthData.put("low", StockUtil.convertDoubleToTwoPrecision(low));
//        monthData.put("highDate", highDate);
//        monthData.put("lowData", lowDate);
        return monthData;
    }

    private static Map<String, Double> getPrevWeeklyHighLow(String stockName) {
        double low = 99999.99;
        double high = 0.0;
//        String highDate = "";
//        String lowDate = "";
        Map<String, Double> monthData = new HashMap<>();
        try{
            String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data_weekly\\"+stockName+".csv";
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            Collections.reverse(stockData);
            GregorianCalendar today = DateUtil.DateToGregorianCal(DateUtil.convertStrToDate(DateUtil.getPreviousWeekDate(), "yyyy_MM_dd"));
//            GregorianCalendar today = DateUtil.DateToGregorianCal(new Date());
            for (String[] sd: stockData){
                boolean flag = DateUtil.isDateInThisWeek(sd[0], "yyyy-MM-dd", today.get(Calendar.DAY_OF_WEEK_IN_MONTH));
                if (flag){
                    double h = Double.parseDouble(sd[2]);
                    double l = Double.parseDouble(sd[3]);
                    if (high < h) {
                        high = h;
//                        highDate = sd[0];
                    }
                    if (l < low) {
                        low = l;
//                        lowDate = sd[0];
                    }
                    break;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        monthData.put("high", StockUtil.convertDoubleToTwoPrecision(high));
        monthData.put("low", StockUtil.convertDoubleToTwoPrecision(low));
//        monthData.put("highDate", highDate);
//        monthData.put("lowData", lowDate);
        return monthData;
    }

    private static int readDaysBetweenTwoDays(String stockName, String beginDate, String lastDate, String format) {
        int days = 1;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date startDate =sdf.parse(beginDate);
            Date endDate =sdf.parse(lastDate);
            List<String[]> stockData = StockUtil.loadStockData(stockName);
            stockData = stockData.subList(0, 65);
            for (String[] data : stockData){
                Date cDt = sdf.parse(data[0]);
                if (cDt.after(startDate) && cDt.before(endDate))
                    days++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return days;
    }

    private static Map<String, String> getPrevWeekMonthHighLowDate(String stockName,Map<String, Double> prevWkHighLow, Map<String, Double> prevMonHighLow) {
        String wkHighDate = "";
        String monHighDate = "";
        String wkLowDate = "";
        String monLowDate = "";
        Map<String, String> dates = new HashMap<>();
        List<String[]> historyData = StockUtil.loadStockData(stockName);
        double prevWkHigh = prevWkHighLow.get("high");
        double prevWkLow = prevWkHighLow.get("low");
        double prevMonLow = prevMonHighLow.get("low");
        double prevMonHigh = prevMonHighLow.get("high");
        historyData = historyData.subList(0, 60);
        for (String[] sd : historyData){
            double sdHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(sd[2]));
            double sdLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(sd[3]));
            if (sdHigh==prevMonHigh)
                monHighDate = sd[0];
            if (sdHigh==prevWkHigh)
                wkHighDate = sd[0];
            if (sdLow==prevMonLow)
                monLowDate = sd[0];
            if (sdLow==prevWkLow)
                wkLowDate = sd[0];
        }
        dates.put("wkHighDate",wkHighDate);
        dates.put("monHighDate",monHighDate);
        dates.put("wkLowDate",wkLowDate);
        dates.put("monLowDate",monLowDate);
        return dates;
    }

    private static Map<String, Double> getExpectedMovementdata(String stockName, String marketTrend) {
        Map<String, Double> expectedMovement = new HashMap<>();
        Map<String, Double> prevMonHighLow = getPrevMonthHighLow(stockName);
        Map<String, Double> prevWkHighLow = getPrevWeeklyHighLow(stockName);
        Map<String, String> prevWkMonthHighLowDates = getPrevWeekMonthHighLowDate(stockName,prevWkHighLow, prevMonHighLow);
//        if (marketTrend.equals("GREEN")) {
        if (marketTrend.equals("GREEN") && (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkHighDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monHighDate"), "yyyy-MM-dd")))
              && (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monLowDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkLowDate"), "yyyy-MM-dd")))) {
            int wh_mh_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkHighDate"), prevWkMonthHighLowDates.get("monHighDate"), "yyyy-MM-dd");
            int ml_wl_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monLowDate"), prevWkMonthHighLowDates.get("wkLowDate"), "yyyy-MM-dd");
            double movePerDayHigh = (prevMonHighLow.get("high") - prevWkHighLow.get("high")) / wh_mh_days;
            double movePerDayLow = (prevWkHighLow.get("low") - prevMonHighLow.get("low")) / ml_wl_days;
            int daysHighTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monHighDate"), DateUtil.getTomorrowDate("yyyy-MM-dd"), "yyyy-MM-dd");
            int daysLowTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkLowDate"), DateUtil.getTomorrowDate("yyyy-MM-dd"), "yyyy-MM-dd");
            double expectedHighAmountToday = prevMonHighLow.get("high") + (movePerDayHigh * daysHighTillToday);
            double expectedLowAmountToday = prevWkHighLow.get("low") + (movePerDayLow * daysLowTillToday);
            expectedMovement.put("expectedHighAmountToday", expectedHighAmountToday);
            expectedMovement.put("expectedLowAmountToday", expectedLowAmountToday);
//        }else if(marketTrend.equals("RED")){
        }else if(marketTrend.equals("RED") && (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monHighDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkHighDate"), "yyyy-MM-dd")))
                && (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkLowDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monLowDate"), "yyyy-MM-dd")))){
            int mh_wh_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monHighDate"), prevWkMonthHighLowDates.get("wkHighDate"), "yyyy-MM-dd");
            int wl_ml_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkLowDate"), prevWkMonthHighLowDates.get("monLowDate"), "yyyy-MM-dd");
            double movePerDayHigh = (prevWkHighLow.get("high") - prevMonHighLow.get("high")) / mh_wh_days;
            double movePerDayLow = (prevMonHighLow.get("low") - prevWkHighLow.get("low")) / wl_ml_days;
            int daysHighTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkHighDate"), DateUtil.getTomorrowDate("yyyy-MM-dd"), "yyyy-MM-dd");
            int daysLowTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monLowDate"), DateUtil.getTomorrowDate("yyyy-MM-dd"), "yyyy-MM-dd");
            double expectedHighAmountToday = prevWkHighLow.get("high") + (movePerDayHigh * daysHighTillToday);
            double expectedLowAmountToday = prevMonHighLow.get("low") + (movePerDayLow * daysLowTillToday);
            expectedMovement.put("expectedHighAmountToday", expectedHighAmountToday);
            expectedMovement.put("expectedLowAmountToday", expectedLowAmountToday);
        }
        return expectedMovement;
    }

    private static void findBestStockForMovement() {
//        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\stock";
        List<ExpectedCandle> expectedCandleList = new ArrayList<>();
        try{
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            fileLocation = fileLocation+"\\"+files.get(0);
            System.out.println("Reading frm : "+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            for (String[] sd: stockData) {
                StockDetails stockDetails = StockUtil.prepareCandleData(sd);
                List<String[]> historyData = StockUtil.loadStockData(stockDetails.getStockName());
                if (Double.parseDouble(historyData.get(0)[4]) > 100) {
                    Map<String, Double> expectedMovement = getExpectedMovementdata(stockDetails.getStockName(), stockDetails.getIsGreenRed());
                    String[] todayData = historyData.get(0);
                    double stockHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[2]));
                    double stockLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[3]));
                    double highDiff = expectedMovement.get("expectedHighAmountToday")==null?99999:expectedMovement.get("expectedHighAmountToday") - stockHigh;
                    double lowDiff = expectedMovement.get("expectedLowAmountToday")==null?99999:expectedMovement.get("expectedLowAmountToday") - stockLow;
//                System.out.println("Stock="+stockDetails.getStockName()+", highDiff="+highDiff+", lowDiff="+lowDiff);
                    expectedCandleList.add(ExpectedCandle.builder()
                            .stockName(stockDetails.getStockName())
                            .highDiff(highDiff)
                            .lowDiff(lowDiff)
                            .build());
                }
            }
            List<ExpectedCandle> fList = expectedCandleList.stream().filter(e->e.getHighDiff() < 1).filter(e->e.getHighDiff() > -1).collect(Collectors.toList());
//            Collections.sort(expectedCandleList, Comparator.comparingDouble(ExpectedCandle::getHighDiff));
            fList.addAll(expectedCandleList.stream().filter(e->e.getLowDiff() < 1).filter(e->e.getLowDiff() > -1).collect(Collectors.toList()));
            for (ExpectedCandle ec :fList){
                System.out.println(ec);
            }

            StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\trendFilterStock\\stocks_"+DateUtil.getTodayDate(), fList.stream().map(e->e.toString().split(",")).collect(Collectors.toList()));

        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }

//        Map<String, Double> expectedMovement = getExpectedMovementdata("BHEL.NS", "GREEN");
//        expectedMovement.get("expectedHighAmountToday");
//        expectedMovement.get("expectedLowAmountToday");
    }

    public static void main(String[] args) {
//        finalizeStocks();

//        divideBasedLogic();
//        findCommonStockUsingAllCriteria();
//        validateStocksToTradeOption();
//        findStocksNoTradeBreak();
//        findStocksNoTradeBreakAndVolDetails();
//        storeCandleWiseStock();
//        filterStocksBasedPreviousCandles(); //filterbasedcandle


        //expected movement
        findBestStockForMovement();
    }

    public static void runJobs() {
        validateStocksToConfirm();
        StocksRuleCreateUpdateJob.readConfirmStocksAndPrepareRule();
    }
}
