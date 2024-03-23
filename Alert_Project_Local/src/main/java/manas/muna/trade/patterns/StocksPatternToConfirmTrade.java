package manas.muna.trade.patterns;

import manas.muna.trade.constants.CandleConstant;
import manas.muna.trade.constants.CandleTypes;
import manas.muna.trade.jobs.PrepareReportJob;
import manas.muna.trade.stocksRule.StocksRuleCreateUpdateJob;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockPropertiesUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.*;
import org.jsoup.internal.StringUtil;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static void findStocksTradeBreakToday() {
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
//        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\stocks";
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
        List<StockDetails> finalizeStocks = new ArrayList<>();
        List<StockDetails> stocks = new ArrayList<>();
        try {
            int i=1;
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
        List<String[]> trendBreakStock = new ArrayList<>();
        Set<String> optionStockNames = StockUtil.loadOptionStockNames();
//        Map<String, String> resultDates = ReadResultsDateDataJob.getResultCalander();
        int i = 1;
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
                //to check trendbreaktoday
                String checkType = "";
                if(sd.getIsGreenRed().equals("GREEN"))
                    checkType = CandleConstant.DESCENDING;
                else if (sd.getIsGreenRed().equals("RED"))
                    checkType = CandleConstant.ACCEDING;
                if (!CandleUtil.isTrendSequenceBreakToday(sd.getStockName(), sd.getTrendDays(), null, checkType)) {
//                if (!CandleUtil.isTrendSequenceBreak(sd.getStockName(), sd.getTrendDays(), null, checkType))
                    trendBreakStock.add(str);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        stocks = CandleUtil.checkIfStockInTop(stocks);
        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\stocks\\filter_stock_"+DateUtil.getTodayDate(), stocksToStore);
        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\option_stocks\\filter_stock_"+DateUtil.getTodayDate(), optionStocksToStore);
//        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\top_in_trend_stocks\\filter_stock_"+DateUtil.getTodayDate(), topInTrendStock);
        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\trendBreakStock\\stocks_"+DateUtil.getTodayDate(), trendBreakStock);
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

    private static Map<String, Double> getPrevWeeklyHighLow(String stockName, int checkDay) {
        double low = 99999.99;
        double high = 0.0;
//        String highDate = "";
//        String lowDate = "";
        Map<String, Double> weekData = new HashMap<>();
        try{
            String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data_weekly\\"+stockName+".csv";
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            Collections.reverse(stockData);
            GregorianCalendar today = DateUtil.DateToGregorianCal(DateUtil.convertStrToDate(DateUtil.getPreviousWeekDate(checkDay), "yyyy_MM_dd"));
            today.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//            GregorianCalendar today = DateUtil.DateToGregorianCal(new Date());
            Date begin = today.getTime();
            today.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            Date end = today.getTime();
            for (String[] sd: stockData){
//                boolean flag = DateUtil.isDateInThisWeek(sd[0], "yyyy-MM-dd", today.get(Calendar.DAY_OF_WEEK_IN_MONTH));
                boolean flag = DateUtil.isDateBetweenTwoDates(DateUtil.convertStrToDate(sd[0], "yyyy-MM-dd"), begin, end);
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
        weekData.put("high", StockUtil.convertDoubleToTwoPrecision(high));
        weekData.put("low", StockUtil.convertDoubleToTwoPrecision(low));
//        monthData.put("highDate", highDate);
//        monthData.put("lowData", lowDate);
        return weekData;
    }

    private static Map<String, Double> getWeeklyHighLow(String stockName, String checkDay, int day) {
        double low = 99999.99;
        double high = 0.0;
//        String highDate = "";
//        String lowDate = "";
        Map<String, Double> weekData = new HashMap<>();
        try{
            String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv";;
            if (DateUtil.checkIfTodayIsGivenDay(checkDay,"yyyy-MM-dd",Calendar.MONDAY)){
                fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data_weekly\\"+stockName+".csv";
            }
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            Collections.reverse(stockData);
            stockData = stockData.subList(day, stockData.size());
            GregorianCalendar today = DateUtil.DateToGregorianCal(DateUtil.convertStrToDate(DateUtil.checkIfMondayGivePrevWeekDate(checkDay,"yyyy-MM-dd"), "yyyy_MM_dd"));
            today.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//            GregorianCalendar today = DateUtil.DateToGregorianCal(new Date());
            Date begin = today.getTime();
            today.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            Date end = today.getTime();
            int dayGap = 0;
            if (end.before(DateUtil.convertStrToDate(stockData.get(0)[0], "yyyy-MM-dd"))) {
                dayGap = DateUtil.getDateDiffBetweenTwoDate(stockData.get(0)[0], "yyyy-MM-dd", DateUtil.convertDateToStr(end, "yyyy-MM-dd"), "yyyy-MM-dd");
                stockData = stockData.subList(dayGap, stockData.size());
            }
            for (String[] sd: stockData){
//                boolean flag = DateUtil.isDateInThisWeek(sd[0], "yyyy-MM-dd", today.get(Calendar.DAY_OF_WEEK_IN_MONTH));
                boolean flag = DateUtil.isDateBetweenTwoDates(DateUtil.convertStrToDate(sd[0], "yyyy-MM-dd"), begin, end);
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
//                    break;
                }else
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        weekData.put("high", StockUtil.convertDoubleToTwoPrecision(high));
        weekData.put("low", StockUtil.convertDoubleToTwoPrecision(low));
//        monthData.put("highDate", highDate);
//        monthData.put("lowData", lowDate);
        return weekData;
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

    private static Map<String, String> getPrevWeekMonthHighLowDate(String stockName,Map<String, Double> prevWkHighLow,
                                                                   Map<String, Double> prevMonHighLow, int day) {
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
        historyData = historyData.subList(day, 60);
        for (String[] sd : historyData){
            double sdHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(sd[2]));
            double sdLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(sd[3]));
            if (monHighDate.isEmpty() && sdHigh==prevMonHigh)
                monHighDate = sd[0];
            if (wkHighDate.isEmpty() && sdHigh==prevWkHigh)
                wkHighDate = sd[0];
            if (monLowDate.isEmpty() && sdLow==prevMonLow)
                monLowDate = sd[0];
            if (wkLowDate.isEmpty() && sdLow==prevWkLow)
                wkLowDate = sd[0];
            if (!monHighDate.isEmpty() && !wkHighDate.isEmpty() && !monLowDate.isEmpty() && !wkLowDate.isEmpty())
                break;
        }
        dates.put("wkHighDate",wkHighDate);
        dates.put("monHighDate",monHighDate);
        dates.put("wkLowDate",wkLowDate);
        dates.put("monLowDate",monLowDate);
        return dates;
    }

    public static Map<String, Double> getExpectedMovementdata(String stockName, String marketTrend, String date, int day) {
        Map<String, Double> expectedMovement = new HashMap<>();
        Map<String, Double> prevMonHighLow = getPrevMonthHighLow(stockName);
        Map<String, Double> prevWkHighLow = getWeeklyHighLow(stockName,date, day);
        expectedMovement.put("wkHigh",prevWkHighLow.get("high"));
        expectedMovement.put("wkLow",prevWkHighLow.get("low"));
        expectedMovement.put("monHigh",prevMonHighLow.get("high"));
        expectedMovement.put("monLow",prevMonHighLow.get("low"));
        expectedMovement.put("mrkTrend", 0.0);
        int lowDays = 0;
        int highDays = 0;
        if (prevMonHighLow.get("low") > prevWkHighLow.get("high")){
            expectedMovement.put("expectedHighAmountToday", 0.0);
            expectedMovement.put("expectedLowAmountToday", 0.0);
            return expectedMovement;
        }
        Map<String, String> prevWkMonthHighLowDates = getPrevWeekMonthHighLowDate(stockName,prevWkHighLow, prevMonHighLow, day);
        if(prevMonHighLow.get("high") > prevWkHighLow.get("high")
                && prevMonHighLow.get("low") < prevWkHighLow.get("low")){
            expectedMovement.put("mrkTrend", 0.0);
        }else if (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monHighDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monLowDate"),"yyyy-MM-dd"))){
            if (prevMonHighLow.get("high") > prevWkHighLow.get("high"))
                expectedMovement.put("mrkTrend", -1.0);
            else
                expectedMovement.put("mrkTrend", 1.0);
        }else if (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monLowDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monHighDate"),"yyyy-MM-dd"))){
            if (prevMonHighLow.get("low") < prevWkHighLow.get("low"))
                expectedMovement.put("mrkTrend", 1.0);
            else
                expectedMovement.put("mrkTrend", -1.0);
        }

        System.out.println("wkHighDate="+prevWkMonthHighLowDates.get("wkHighDate")+",monHighDate="+prevWkMonthHighLowDates.get("monHighDate")
                +",wkLowDate"+prevWkMonthHighLowDates.get("wkLowDate")+",monLowDate"+prevWkMonthHighLowDates.get("monLowDate"));
        if ((DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkHighDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monHighDate"), "yyyy-MM-dd")))) {
            int wh_mh_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkHighDate"), prevWkMonthHighLowDates.get("monHighDate"), "yyyy-MM-dd");
            double movePerDayHigh = (prevMonHighLow.get("high") - prevWkHighLow.get("high")) / wh_mh_days;
            int daysHighTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monHighDate"), date, "yyyy-MM-dd");
            double expectedHighAmountToday = prevMonHighLow.get("high") + (movePerDayHigh * daysHighTillToday);
            expectedMovement.put("expectedHighAmountToday", StockUtil.convertDoubleToTwoPrecision(expectedHighAmountToday));
            expectedMovement.put("movePerDayHigh", movePerDayHigh);
        }else if ((DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monHighDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkHighDate"), "yyyy-MM-dd")))){
            int mh_wh_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monHighDate"), prevWkMonthHighLowDates.get("wkHighDate"), "yyyy-MM-dd");
            double movePerDayHigh = (prevWkHighLow.get("high") - prevMonHighLow.get("high")) / mh_wh_days;
            int daysHighTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkHighDate"), date, "yyyy-MM-dd");
            double expectedHighAmountToday = prevWkHighLow.get("high") + (movePerDayHigh * daysHighTillToday);
            expectedMovement.put("expectedHighAmountToday", StockUtil.convertDoubleToTwoPrecision(expectedHighAmountToday));
            expectedMovement.put("movePerDayHigh", movePerDayHigh);
        }
        if (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monLowDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkLowDate"), "yyyy-MM-dd"))){
            int ml_wl_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monLowDate"), prevWkMonthHighLowDates.get("wkLowDate"), "yyyy-MM-dd");
            double movePerDayLow = (prevWkHighLow.get("low") - prevMonHighLow.get("low")) / ml_wl_days;
            int daysLowTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkLowDate"), date, "yyyy-MM-dd");
            double expectedLowAmountToday = prevWkHighLow.get("low") + (movePerDayLow * daysLowTillToday);
            expectedMovement.put("expectedLowAmountToday", StockUtil.convertDoubleToTwoPrecision(expectedLowAmountToday));
            expectedMovement.put("movePerDayLow", movePerDayLow);
        }else if (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkLowDate"),"yyyy-MM-dd")
                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monLowDate"), "yyyy-MM-dd"))){
            int wl_ml_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkLowDate"), prevWkMonthHighLowDates.get("monLowDate"), "yyyy-MM-dd");
            double movePerDayLow = (prevMonHighLow.get("low") - prevWkHighLow.get("low")) / wl_ml_days;
            int daysLowTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monLowDate"), date, "yyyy-MM-dd");
            double expectedLowAmountToday = prevMonHighLow.get("low") + (movePerDayLow * daysLowTillToday);
            expectedMovement.put("expectedLowAmountToday", StockUtil.convertDoubleToTwoPrecision(expectedLowAmountToday));
            expectedMovement.put("movePerDayLow", movePerDayLow);
        }

//        System.out.println("Verify this stock "+stockName+", it has some issue to calculate expected movement");

//        if (marketTrend.equals("GREEN")) {
//        if (marketTrend.equals("GREEN") && (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkHighDate"),"yyyy-MM-dd")
//                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monHighDate"), "yyyy-MM-dd")))
//              && (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monLowDate"),"yyyy-MM-dd")
//                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkLowDate"), "yyyy-MM-dd")))) {
//            int wh_mh_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkHighDate"), prevWkMonthHighLowDates.get("monHighDate"), "yyyy-MM-dd");
//            int ml_wl_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monLowDate"), prevWkMonthHighLowDates.get("wkLowDate"), "yyyy-MM-dd");
//            double movePerDayHigh = (prevMonHighLow.get("high") - prevWkHighLow.get("high")) / wh_mh_days;
//            double movePerDayLow = (prevWkHighLow.get("low") - prevMonHighLow.get("low")) / ml_wl_days;
//            int daysHighTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monHighDate"), DateUtil.getTomorrowDate("yyyy-MM-dd"), "yyyy-MM-dd");
//            int daysLowTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkLowDate"), DateUtil.getTomorrowDate("yyyy-MM-dd"), "yyyy-MM-dd");
//            double expectedHighAmountToday = prevMonHighLow.get("high") + (movePerDayHigh * daysHighTillToday);
//            double expectedLowAmountToday = prevWkHighLow.get("low") + (movePerDayLow * daysLowTillToday);
//            expectedMovement.put("expectedHighAmountToday", expectedHighAmountToday);
//            expectedMovement.put("expectedLowAmountToday", expectedLowAmountToday);
//        }else if(marketTrend.equals("RED")){
//        }else if(marketTrend.equals("RED") && (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monHighDate"),"yyyy-MM-dd")
//                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkHighDate"), "yyyy-MM-dd")))
//                && (DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("wkLowDate"),"yyyy-MM-dd")
//                .before(DateUtil.convertStrToDate(prevWkMonthHighLowDates.get("monLowDate"), "yyyy-MM-dd")))){
//            int mh_wh_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monHighDate"), prevWkMonthHighLowDates.get("wkHighDate"), "yyyy-MM-dd");
//            int wl_ml_days = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkLowDate"), prevWkMonthHighLowDates.get("monLowDate"), "yyyy-MM-dd");
//            double movePerDayHigh = (prevWkHighLow.get("high") - prevMonHighLow.get("high")) / mh_wh_days;
//            double movePerDayLow = (prevMonHighLow.get("low") - prevWkHighLow.get("low")) / wl_ml_days;
//            int daysHighTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("wkHighDate"), DateUtil.getTomorrowDate("yyyy-MM-dd"), "yyyy-MM-dd");
//            int daysLowTillToday = readDaysBetweenTwoDays(stockName, prevWkMonthHighLowDates.get("monLowDate"), DateUtil.getTomorrowDate("yyyy-MM-dd"), "yyyy-MM-dd");
//            double expectedHighAmountToday = prevWkHighLow.get("high") + (movePerDayHigh * daysHighTillToday);
//            double expectedLowAmountToday = prevMonHighLow.get("low") + (movePerDayLow * daysLowTillToday);
//            expectedMovement.put("expectedHighAmountToday", expectedHighAmountToday);
//            expectedMovement.put("expectedLowAmountToday", expectedLowAmountToday);
//        }
        return expectedMovement;
    }
    private static Set<ExpectedCandle> findBestStockForMovement(String fileLocation) {
        return findBestStockForMovement(fileLocation, 0, "");
    }

    private static void findHighLowEqualStocks() {
        findHighLowEqualStocks(0);
    }
    private static void verifyHighLowEqualStocks(int days) {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\high_low_stocks";
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int i = 0;
            fileLocation = fileLocation + "\\" + files.get(i);
            System.out.println("Reading frm : " + fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            Map<String, FutureStock> fsl = FutureStock.loadAllData(stockData);
            for (String key: fsl.keySet()){
//                if (!key.equals("APLAPOLLO.NS"))
//                    continue;
                List<String[]> historyData = StockUtil.loadStockData(key);
                historyData = historyData.subList(days, historyData.size()-1);
                String[] todayData = historyData.get(0);
                FutureStock fs = fsl.get(key);
                double exHg = fs.getExctHigh()+fs.getMovePerDayHigh();
                double exLow = fs.getExctLow()-fs.getMovePerDayLow();
                if (fs.getExctMrktDirection().equals("UP")
                        && Double.parseDouble(todayData[1]) >= exHg && Double.parseDouble(todayData[4]) >=exHg){
                    System.out.println(fs.getStockName()+" breckout");
                }else if(fs.getExctMrktDirection().equals("DOWN")
                        && Double.parseDouble(todayData[1]) <= exLow && Double.parseDouble(todayData[4]) <=exLow) {
                    System.out.println(fs.getStockName()+" breckout");
                }
                double dif = (fs.getExctHigh() - fs.getExctLow());
//                System.out.println("-----"+fs.getStockName()+"---"+dif);
                if(dif < 5){
                    System.out.println("-----"+fs.getStockName());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void findHighLowEqualStocks(int days) {
        String reportLoc = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024";
        List<FutureStock> stockList = new ArrayList<>();
        List<FutureStock> topStockList = new ArrayList<>();
        for (String name: StockUtil.loadAllStockNames()){
            List<String[]> historyData = StockUtil.loadStockData(name);
            historyData = historyData.subList(days, historyData.size()-1);
//            if (!name.equals("AWL.NS"))
//                continue;
            if (Double.parseDouble(historyData.get(0)[4]) > 100) {
                Map<String, Double> expectedMovement = getExpectedMovementdata(name, null, historyData.get(0)[0], days);
                if ((expectedMovement.get("expectedHighAmountToday") == null || Double.compare(expectedMovement.get("expectedHighAmountToday"), 0.0) == 0)
                        && (expectedMovement.get("expectedLowAmountToday") == null || Double.compare(expectedMovement.get("expectedLowAmountToday"), 0.0) == 0)) {
                    continue;
                }

                String[] todayData = historyData.get(0);
                CandleStick candleStick = CandleUtil.prepareCandleData(historyData.get(1), todayData);
                double stockHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[2]));
                double stockLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[3]));
                double stockOpen = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[1]));
                double stockClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[4]));
                String isRedGreen = stockOpen < stockClose ? "GREEN": "RED";
                double stockOCLow = stockOpen < stockClose ? stockOpen : stockClose;
                double stockOCHigh = stockOpen < stockClose ? stockClose : stockOpen;
                double highDiff = expectedMovement.get("expectedHighAmountToday")==null?99999:expectedMovement.get("expectedHighAmountToday") - stockHigh;
                double lowDiff = expectedMovement.get("expectedLowAmountToday")==null?99999:expectedMovement.get("expectedLowAmountToday") - stockLow;
                System.out.println("Stock="+name+", highDiff="+highDiff+", lowDiff="+lowDiff+",DT="+historyData.get(0)[0]);
                System.out.println(name+"...low="+expectedMovement.get("expectedLowAmountToday")
                        +"....high="+expectedMovement.get("expectedHighAmountToday"));
                double expctLowAmount = expectedMovement.get("expectedLowAmountToday")==null? 0: expectedMovement.get("expectedLowAmountToday");
                double expctHighAmount = expectedMovement.get("expectedHighAmountToday")==null? 99999:expectedMovement.get("expectedHighAmountToday");
                double movePerDayLow = expectedMovement.get("movePerDayLow")==null?0.0:expectedMovement.get("movePerDayLow");
//                if(movePerDayLow < 0)
//                    movePerDayLow = movePerDayLow * -1;
                double movePerDayHigh = expectedMovement.get("movePerDayHigh")==null?0.0:expectedMovement.get("movePerDayHigh");
//                if (movePerDayHigh < 0)
//                    movePerDayHigh = movePerDayHigh * -1;
                String mrkDirection = Double.compare(expectedMovement.get("mrkTrend"), -1.0)==0?"DOWN":
                        Double.compare(expectedMovement.get("mrkTrend"), 1.0)==0? "UP": "NEUTRAL";
                double bothHighDiff = expectedMovement.get("wkHigh") - expectedMovement.get("monHigh");
                bothHighDiff = bothHighDiff < 0 ? bothHighDiff * -1 : bothHighDiff;
                double bothLowDiff = expectedMovement.get("wkLow") - expectedMovement.get("monLow");
                bothLowDiff = bothLowDiff < 0 ? bothLowDiff * -1 : bothLowDiff;
                //report data
                List<String[]> reportData = StockUtil.readFileData(reportLoc+"\\"+name);
                Collections.reverse(reportData);
                reportData = reportData.subList(days,reportData.size()-1);
                String[] todaysReport = reportData.get(0);
                //this to store top stock
                if (CandleUtil.checkIfStockInTop(name,21,mrkDirection, historyData)){
                    Map<String, Object> cData = null;
                    if (mrkDirection.equals("UP"))
                        cData = CandleUtil.checkBearishStockPatterns(name, historyData);
                    if (mrkDirection.equals("DOWN"))
                        cData = CandleUtil.checkBullishStockPatterns(name, historyData);
                    String candleOccur = cData==null?"":cData.get("candleTypesOccur")==null?"":cData.get("candleTypesOccur").toString();
                    topStockList.add(FutureStock.builder().stockName(name).exctMrktDirection("Not "+mrkDirection).selectType("TOP")
                            .rsiVal(Double.parseDouble(todaysReport[9])).expHighLowDiff((expctHighAmount-expctLowAmount))
                            .movePerDayLow(movePerDayLow).movePerDayHigh(movePerDayHigh).open(stockOpen).close(stockClose)
                            .exctHigh(expctHighAmount).exctLow(expctLowAmount).candleOccur(candleOccur).build());
                }

                if(StockUtil.calculatePercantage(bothHighDiff, expectedMovement.get("wkHigh")) <= 1
                    && expctLowAmount>=stockLow && expctLowAmount<stockClose){
                    stockList.add(FutureStock.builder().stockName(name).exctMrktDirection("DOWN").selectType("HighEqual")
                            .rsiVal(Double.parseDouble(todaysReport[9])).expHighLowDiff((expctHighAmount-expctLowAmount))
                            .movePerDayLow(movePerDayLow).movePerDayHigh(movePerDayHigh).open(stockOpen).close(stockClose)
                            .exctHigh(expctHighAmount).exctLow(expctLowAmount).build());
                }else if (StockUtil.calculatePercantage(bothLowDiff, expectedMovement.get("monLow")) <= 1
                    && expctHighAmount<=stockHigh && expctHighAmount > stockClose){
                    stockList.add(FutureStock.builder().stockName(name).exctMrktDirection("UP").selectType("LowEqual")
                            .rsiVal(Double.parseDouble(todaysReport[9])).expHighLowDiff((expctHighAmount-expctLowAmount))
                            .movePerDayLow(movePerDayLow).movePerDayHigh(movePerDayHigh).open(stockOpen).close(stockClose)
                            .exctHigh(expctHighAmount).exctLow(expctLowAmount).build());
                }
            }
        }
        List<FutureStock> down = stockList;
        down.stream().filter(e->e.getExctMrktDirection().equals("DOWN")).collect(Collectors.toList());
        down = down.stream().sorted(Comparator.comparing(FutureStock::getRsiVal)).collect(Collectors.toList());
        List<FutureStock> up = stockList;
        up.stream().filter(e->e.getExctMrktDirection().equals("UP")).collect(Collectors.toList());
        up = up.stream().sorted(Comparator.comparing(FutureStock::getRsiVal)).collect(Collectors.toList());
        down.addAll(up);

        down = new ArrayList<>(down.stream().sorted(Comparator.comparing(FutureStock::getExpHighLowDiff)).collect(Collectors.toSet()));
        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\high_low_stocks\\"+DateUtil.getTodayDate()+"_high_low_equal_stocks", down.stream().map(e->e.toString().split(",")).collect(Collectors.toList()));
        List<FutureStock> upDownStocks = topStockList.stream().filter(e->!e.getExctMrktDirection().equals("NEUTRAL")).filter(a->!a.getCandleOccur().isEmpty()).collect(Collectors.toList());
        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\high_low_stocks\\"+DateUtil.getTodayDate()+"_top_in_trend_stocks", upDownStocks.stream().map(e->e.toString().split(",")).collect(Collectors.toList()));
    }
    private static Set<ExpectedCandle> findBestStockForMovement(String fileLocation, int checkDay, String checkType) {
//        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
//        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\stock";
        Set<ExpectedCandle> fList = new HashSet<>();
        List<ExpectedCandle> expectedCandleList = new ArrayList<>();
        List<ExpectedCandle> expectedCandleTop = new ArrayList<>();
        List<ExpectedCandle> expectedCandleUp = new ArrayList<>();
        List<ExpectedCandle> expectedCandleDown = new ArrayList<>();
        List<ExpectedCandle> expectedHighLowEqual = new ArrayList<>();
        Set<ExpectedCandle> expectedHighLowEqualTop = new HashSet<>();
        List<ExpectedCandle> expectedMoveTomorrow = new ArrayList<>();
        try{
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int i=0;
            fileLocation = fileLocation+"\\"+files.get(checkDay);
            System.out.println("Reading frm : "+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            for (String[] sd: stockData) {
                StockDetails stockDetails = StockUtil.prepareCandleData(sd);
//                if (!stockDetails.getStockName().equals("CGCL.NS"))
//                    continue;
                List<String[]> historyData = new ArrayList<>();
                try {
                    historyData = StockUtil.loadStockData(stockDetails.getStockName());
                }catch (Exception e){
                    System.out.println("no data for this stock: "+stockDetails.getStockName());
                }
                if (historyData.isEmpty())
                    continue;
                historyData = historyData.subList(checkDay, historyData.size()-1);
                if (Double.parseDouble(historyData.get(0)[4]) > 100) {
                    Map<String, Double> expectedMovement = getExpectedMovementdata(stockDetails.getStockName(), stockDetails.getIsGreenRed(), historyData.get(0)[0], checkDay);
                    if((expectedMovement.get("expectedHighAmountToday") == null || Double.compare(expectedMovement.get("expectedHighAmountToday"),0.0)==0)
                            && (expectedMovement.get("expectedLowAmountToday") == null || Double.compare(expectedMovement.get("expectedLowAmountToday"),0.0)==0)){
                        continue;
                    }
                    String[] todayData = historyData.get(0);
                    CandleStick candleStick = CandleUtil.prepareCandleData(historyData.get(1), todayData);
                    double stockHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[2]));
                    double stockLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[3]));
                    double stockOpen = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[1]));
                    double stockClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[4]));
                    String isRedGreen = stockOpen < stockClose ? "GREEN": "RED";
                    double stockOCLow = stockOpen < stockClose ? stockOpen : stockClose;
                    double stockOCHigh = stockOpen < stockClose ? stockClose : stockOpen;
                    double highDiff = expectedMovement.get("expectedHighAmountToday")==null?99999:expectedMovement.get("expectedHighAmountToday") - stockHigh;
                    double lowDiff = expectedMovement.get("expectedLowAmountToday")==null?99999:expectedMovement.get("expectedLowAmountToday") - stockLow;
                System.out.println("Stock="+stockDetails.getStockName()+", highDiff="+highDiff+", lowDiff="+lowDiff+",DT="+historyData.get(0)[0]);
//                    if (highDiff < 1 || lowDiff < 1) {
//                    if (expectedMovement.get("expectedHighAmountToday") != null && stockClose > expectedMovement.get("expectedHighAmountToday")
//                            && stockLow > expectedMovement.get("expectedHighAmountToday")) {
//                    if ((highDiff < 1 && lowDiff < -3) || (lowDiff > -1 && highDiff > 3)) {
                    System.out.println(stockDetails.getStockName()+"...low="+expectedMovement.get("expectedLowAmountToday")
                                +"....high="+expectedMovement.get("expectedHighAmountToday"));
                    String mrkDirection = Double.compare(expectedMovement.get("mrkTrend"), -1.0)==0?"DOWN":
                            Double.compare(expectedMovement.get("mrkTrend"), 1.0)==0? "UP": "NEUTRAL";
                    double bothHighDiff = expectedMovement.get("wkHigh") - expectedMovement.get("monHigh");
                    bothHighDiff = bothHighDiff < 0 ? bothHighDiff * -1 : bothHighDiff;
                    double bothLowDiff = expectedMovement.get("wkLow") - expectedMovement.get("monLow");
                    bothLowDiff = bothLowDiff < 0 ? bothLowDiff * -1 : bothLowDiff;
                    if(checkType.equals("allStockCandle")){
                        if(StockUtil.calculatePercantage(bothHighDiff, expectedMovement.get("wkHigh")) < 2){
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 4);
                            ExpectedCandle ec = ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.convertDoubleToTwoPrecision(StockUtil.calculatePercantage(highDiff, stockOCHigh)))
                                    .lowDiff(StockUtil.convertDoubleToTwoPrecision(StockUtil.calculatePercantage(lowDiff, stockOCLow)))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(0)
                                    .selectedCategory("HighEqual")
                                    .expctHigh(expectedMovement.get("expectedHighAmountToday")==null?0:expectedMovement.get("expectedHighAmountToday"))
                                    .expctLow(expectedMovement.get("expectedLowAmountToday")==null?0:expectedMovement.get("expectedLowAmountToday"))
                                    .highMovePerDay(expectedMovement.get("movePerDayHigh")==null?0:expectedMovement.get("movePerDayHigh"))
                                    .lowMovePerDay(expectedMovement.get("movePerDayLow")==null?0:expectedMovement.get("movePerDayLow"))
                                    .build();
                            if (CandleUtil.isStockInTopOfShortTrend(stockDetails, historyData, mrkDirection))
                                expectedHighLowEqualTop.add(ec);
                            expectedHighLowEqual.add(ec);
                        }else if(StockUtil.calculatePercantage(bothLowDiff, expectedMovement.get("monLow")) < 2){
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 4);
                            ExpectedCandle ec = ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.convertDoubleToTwoPrecision(StockUtil.calculatePercantage(highDiff, stockOCHigh)))
                                    .lowDiff(StockUtil.convertDoubleToTwoPrecision(StockUtil.calculatePercantage(lowDiff, stockOCLow)))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(0)
                                    .selectedCategory("LowEqual")
                                    .expctHigh(expectedMovement.get("expectedHighAmountToday"))
                                    .expctLow(expectedMovement.get("expectedLowAmountToday"))
                                    .highMovePerDay(expectedMovement.get("movePerDayHigh"))
                                    .lowMovePerDay(expectedMovement.get("movePerDayLow"))
                                    .build();
                            if (CandleUtil.isStockInTopOfShortTrend(stockDetails, historyData, mrkDirection))
                                expectedHighLowEqualTop.add(ec);
                            expectedHighLowEqual.add(ec);
                        }else if (mrkDirection.equals("DOWN") && StockUtil.calculatePercantage(highDiff, stockHigh)<2){
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 5);
                            expectedCandleTop.add(ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.convertDoubleToTwoPrecision(StockUtil.calculatePercantage(highDiff, stockOCHigh)))
                                    .lowDiff(StockUtil.convertDoubleToTwoPrecision(StockUtil.calculatePercantage(lowDiff, stockOCLow)))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(0)
                                    .selectedCategory("High0-Down")
                                    .expctHigh(expectedMovement.get("expectedHighAmountToday"))
                                    .expctLow(expectedMovement.get("expectedLowAmountToday"))
                                    .highMovePerDay(expectedMovement.get("movePerDayHigh"))
                                    .lowMovePerDay(expectedMovement.get("movePerDayLow"))
                                    .build());
                        }else if(mrkDirection.equals("UP") && StockUtil.calculatePercantage(lowDiff, stockLow)<2){
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 5);
                            expectedCandleTop.add(ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.convertDoubleToTwoPrecision(StockUtil.calculatePercantage(highDiff, stockOCHigh)))
                                    .lowDiff(StockUtil.convertDoubleToTwoPrecision(StockUtil.calculatePercantage(lowDiff, stockOCLow)))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(0)
                                    .selectedCategory("Low0-High")
                                    .expctHigh(expectedMovement.get("expectedHighAmountToday"))
                                    .expctLow(expectedMovement.get("expectedLowAmountToday"))
                                    .highMovePerDay(expectedMovement.get("movePerDayHigh"))
                                    .lowMovePerDay(expectedMovement.get("movePerDayLow"))
                                    .build());
                        }
                        if (mrkDirection.equals("DOWN") && (lowDiff<1 && lowDiff >-1) && highDiff<0){
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 5);
                            ExpectedCandle ec = ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.calculatePercantage(highDiff, stockOCHigh))
                                    .lowDiff(StockUtil.calculatePercantage(lowDiff, stockOCLow))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(0)
                                    .selectedCategory("Breakout-DOWN")
                                    .expctHigh(expectedMovement.get("expectedHighAmountToday"))
                                    .expctLow(expectedMovement.get("expectedLowAmountToday"))
                                    .highMovePerDay(expectedMovement.get("movePerDayHigh"))
                                    .lowMovePerDay(expectedMovement.get("movePerDayLow"))
                                    .build();
                            if (StockUtil.calculatePercantage(bothLowDiff, expectedMovement.get("monLow")) < 2)
                                ec.setSelectedCategory("Breakout-LowEqual");
                            expectedMoveTomorrow.add(ec);
                        }
                        if (mrkDirection.equals("UP") && (highDiff<1 && highDiff >-1) && lowDiff>0){
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 5);
                            ExpectedCandle ec = ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.calculatePercantage(highDiff, stockOCHigh))
                                    .lowDiff(StockUtil.calculatePercantage(lowDiff, stockOCLow))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(0)
                                    .selectedCategory("Breakout-UP")
                                    .expctHigh(expectedMovement.get("expectedHighAmountToday"))
                                    .expctLow(expectedMovement.get("expectedLowAmountToday"))
                                    .highMovePerDay(expectedMovement.get("movePerDayHigh"))
                                    .lowMovePerDay(expectedMovement.get("movePerDayLow"))
                                    .build();
                            if(StockUtil.calculatePercantage(bothHighDiff, expectedMovement.get("wkHigh")) < 2)
                                ec.setSelectedCategory("Breakout-HighEqual");
                            expectedMoveTomorrow.add(ec);
                        }
                        if (mrkDirection.equals("NEUTRAL") && (highDiff<0 || lowDiff>0)){
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 5);
                            ExpectedCandle ec = ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.calculatePercantage(highDiff, stockOCHigh))
                                    .lowDiff(StockUtil.calculatePercantage(lowDiff, stockOCLow))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(0)
                                    .selectedCategory("Breakout-NEUTRAL")
                                    .expctHigh(expectedMovement.get("expectedHighAmountToday"))
                                    .expctLow(expectedMovement.get("expectedLowAmountToday"))
                                    .highMovePerDay(expectedMovement.get("movePerDayHigh"))
                                    .lowMovePerDay(expectedMovement.get("movePerDayLow"))
                                    .build();
                            if(StockUtil.calculatePercantage(bothHighDiff, expectedMovement.get("wkHigh")) < 2)
                                ec.setSelectedCategory("Breakout-NEUTRAL");
                            expectedMoveTomorrow.add(ec);
                        }
                        List<ExpectedCandle> upCandle = expectedCandleTop;
                        List<ExpectedCandle> highLowEqual = new ArrayList<>();
                        highLowEqual.addAll(expectedHighLowEqual.stream().filter(e->e.getMrkDirection().equals("DOWN")).filter(e->e.getSelectedCategory().equals("LowEqual")).filter(e->e.getIsGreenRed().equals("GREEN")).collect(Collectors.toList()));
//                        highLowEqual.addAll(expectedHighLowEqual.stream().filter(e->e.getMrkDirection().equals("DOWN")).filter(e->e.getSelectedCategory().equals("LowEqual")).filter(e-> CandleTypes.getLGDDojiCanldeNames().contains(getDojiName(e.getCandleType()))).collect(Collectors.toList()));
                        highLowEqual.addAll(expectedHighLowEqual.stream().filter(e->e.getMrkDirection().equals("UP")).filter(e->e.getSelectedCategory().equals("HighEqual")).filter(e->e.getIsGreenRed().equals("RED")).collect(Collectors.toList()));
//                        highLowEqual.addAll(expectedHighLowEqual.stream().filter(e->e.getMrkDirection().equals("UP")).filter(e->e.getSelectedCategory().equals("HighEqual")).filter(e->CandleTypes.getLGDDojiCanldeNames().contains(getDojiName(e.getCandleType()))).collect(Collectors.toList()));
//                        upCandle = upCandle.stream().filter(e->e.getHighDiff()<1).filter(e->e.getHighDiff()>-1).collect(Collectors.toList());
//                        upCandle1 = upCandle1.stream().filter(e->e.getMrkDirection().equals("DOWN")).filter(e->e.getSelectedCategory().equals("HighEqual")).filter(e->e.getIsGreenRed().equals("RED")).collect(Collectors.toList());
                        List<ExpectedCandle> downCandle = expectedCandleTop;
//                        List<ExpectedCandle> downCandle1 = expectedCandleTop;
//                        downCandle = downCandle.stream().filter(e->e.getMrkDirection().equals("UP")).filter(e->e.getSelectedCategory().equals("HighEqual")).filter(e->e.getIsGreenRed().equals("RED")).collect(Collectors.toList());
//                        downCandle1 = downCandle1.stream().filter(e->e.getLowDiff()<1).filter(e->e.getLowDiff()>-1).collect(Collectors.toList());

                        expectedCandleList.addAll(highLowEqual);
                        expectedCandleList.addAll(upCandle);
                        expectedCandleList.addAll(downCandle);
                    }else {
                        if (Double.compare(expectedMovement.get("wkHigh"), expectedMovement.get("monHigh")) == 0
                                || Double.compare(expectedMovement.get("wkLow"), expectedMovement.get("monLow")) == 0
                                || StockUtil.calculatePercantage(bothHighDiff, expectedMovement.get("wkHigh")) < 0.3
                                || StockUtil.calculatePercantage(bothLowDiff, expectedMovement.get("wkLow")) < 0.3) {
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 5);
                            expectedCandleTop.add(ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.calculatePercantage(highDiff, stockOCHigh))
                                    .lowDiff(StockUtil.calculatePercantage(lowDiff, stockOCLow))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(0)
                                    .build());
                            expectedCandleList.addAll(expectedCandleTop.stream().filter(e -> e.getHighDiff() > 6).filter(e -> e.getLowDiff() > 6)
                                    .filter(e -> !e.getCandleType().isEmpty()).collect(Collectors.toList()));
//                        expectedCandleList.addAll(expectedCandleTop.stream().filter(e->e.getHighDiff()>10).collect(Collectors.toList()));
//                        List<ExpectedCandle> ee = expectedCandleTop.stream().filter(e->e.getHighDiff()<10).collect(Collectors.toList());
//                        ee.forEach(e->System.out.println(e));
                        } else if (highDiff > 0 && lowDiff > 0 && stockClose > expectedMovement.get("expectedLowAmountToday")) {
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 5);
                            expectedCandleUp.add(ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.calculatePercantage(highDiff, stockOCHigh))
                                    .lowDiff(StockUtil.calculatePercantage(lowDiff, stockOCLow))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(1)
                                    .build());
                            expectedCandleList.addAll(expectedCandleUp.stream().filter(e -> e.getIsGreenRed().equals("GREEN"))
                                    .filter(e -> e.getLowDiff() >= 5).collect(Collectors.toList()));
                        } else if (highDiff < 1 && lowDiff < 1 && stockClose < expectedMovement.get("expectedHighAmountToday")) {
                            Map<String, String> volData = StockUtil.getVolumeDetails(stockDetails.getStockName(), historyData, 5);
                            expectedCandleDown.add(ExpectedCandle.builder()
                                    .stockName(stockDetails.getStockName())
                                    .highDiff(StockUtil.calculatePercantage(highDiff, stockOCHigh))
                                    .lowDiff(StockUtil.calculatePercantage(lowDiff, stockOCLow))
                                    .candleType(stockDetails.getCandleTypesOccur())
                                    .stockMovement(stockDetails.getIsGreenRed())
                                    .isGreenRed(isRedGreen)
                                    .mrkDirection(mrkDirection)
                                    .volumePos(Integer.parseInt(volData.get("highVolPos")))
                                    .priority(2)
                                    .build());
                            expectedCandleList.addAll(expectedCandleDown.stream().filter(e -> e.getIsGreenRed().equals("RED"))
                                    .filter(e -> e.getLowDiff() <= -5).collect(Collectors.toList()));
                        }
                    }
                }
            }
            expectedCandleList.sort(Comparator.comparingInt(ExpectedCandle::getVolumePos));
            List<ExpectedCandle> highZeroFilter = expectedCandleList;
            List<ExpectedCandle> lowZeroFilter = expectedCandleList;
            highZeroFilter = highZeroFilter.stream().filter(e->e.getHighDiff() < 1).filter(e->e.getHighDiff() > -1).collect(Collectors.toList());
            highZeroFilter = highZeroFilter.stream().filter(e->!StringUtil.isBlank(e.getCandleType())).collect(Collectors.toList());
            highZeroFilter.sort(Comparator.comparing(ExpectedCandle::getLowDiff));
            lowZeroFilter = lowZeroFilter.stream().filter(e->e.getLowDiff() < 1).filter(e->e.getLowDiff() > -1).collect(Collectors.toList());
            lowZeroFilter = lowZeroFilter.stream().sorted(Comparator.comparingDouble(ExpectedCandle::getHighDiff)).collect(Collectors.toList());
//            fList = expectedCandleList.stream().sorted(Comparator.comparingInt(ExpectedCandle::getPriority)).collect(Collectors.toSet());
//            fList = expectedCandleList.stream().sorted(Comparator.comparingInt(ExpectedCandle::getVolumePos)).collect(Collectors.toSet());
            highZeroFilter.addAll(lowZeroFilter);
            List<ExpectedCandle> allList = new ArrayList<>(highZeroFilter.stream().collect(Collectors.toSet()));
            fList = allList.stream().collect(Collectors.toSet());
//            for (ExpectedCandle ec :fList){
//                System.out.println(ec);
//            }

//            StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\trendFilterStock\\stocks_"+DateUtil.getTodayDate(), fList.stream().map(e->e.toString().split(",")).collect(Collectors.toList()));
            //filter out if same was given yesterday

            StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\expectedMove\\stocks_"+DateUtil.getTodayDate(), fList.stream().map(e->e.toString().split(",")).collect(Collectors.toList()));

            //it will store only highlow top one
            List<ExpectedCandle> ehlTop = new ArrayList<>();
            ehlTop.addAll(expectedHighLowEqualTop);
//            ehlTop.addAll(expectedHighLowEqualTop.stream().filter(e->e.getMrkDirection().equals("DOWN")).filter(e->e.getSelectedCategory().equals("LowEqual")).filter(e->e.getIsGreenRed().equals("GREEN")).collect(Collectors.toList()));
//            ehlTop.addAll(expectedHighLowEqualTop.stream().filter(e->e.getMrkDirection().equals("UP")).filter(e->e.getSelectedCategory().equals("HighEqual")).filter(e->e.getIsGreenRed().equals("RED")).collect(Collectors.toList()));
            StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\expectedMove\\stocks_"+DateUtil.getTodayDate()+"_high_low_top", ehlTop.stream().map(e->e.toString().split(",")).collect(Collectors.toList()));

            //it will store only expected to move tomorrow
            List<ExpectedCandle> emt = new ArrayList<>();
            emt.addAll(expectedMoveTomorrow.stream().collect(Collectors.toSet()));
            emt.sort(Comparator.comparing(ExpectedCandle::getVolumePos));
            StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\expectedMove\\stocks_"+DateUtil.getTodayDate()+"_expct_tomrw", emt.stream().map(e->e.toString().split(",")).collect(Collectors.toList()));

        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }

//        Map<String, Double> expectedMovement = getExpectedMovementdata("BHEL.NS", "GREEN");
//        expectedMovement.get("expectedHighAmountToday");
//        expectedMovement.get("expectedLowAmountToday");
        return fList;
    }

    private static String getDojiName(String candleType) {
        String[] names = candleType.replace("|",",").split(",");
        String name = "";
        for (String nm : names){
            if (nm.contains("Doji")) {
                name = nm;
                break;
            }
        }
        return name;
    }

    private static void findCommonElements() {
        String fileLocation1 = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\expectedMove";
        String fileLocation2 = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\stocks";
        String fileLocation3 = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\trendBreakStock";
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();
        try {
            List<String> files1 = Files.list(Paths.get(fileLocation1))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files1.sort(Comparator.reverseOrder());
            List<String> files2 = Files.list(Paths.get(fileLocation2))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files2.sort(Comparator.reverseOrder());
            List<String> files3 = Files.list(Paths.get(fileLocation3))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files3.sort(Comparator.reverseOrder());
            int i = 0;
            System.out.println("Reading frm : " + fileLocation1);
            List<String[]> stockData1 = StockUtil.readFileData(fileLocation1 + "\\" + files1.get(i));
            List<String[]> stockData2 = StockUtil.readFileData(fileLocation2 + "\\" + files2.get(i));
            List<String[]> stockData3 = StockUtil.readFileData(fileLocation3 + "\\" + files3.get(i));
            list1 = stockData1.stream().map(e->e[0].split("=")[1].trim()).collect(Collectors.toList());
            list2 = stockData2.stream().map(e->e[0].split("=")[1].trim()).collect(Collectors.toList());
            list3 = stockData3.stream().map(e->e[0].split("=")[1].trim()).collect(Collectors.toList());
            System.out.println(list1.stream().distinct().filter(list2::contains).filter(list3::contains).collect(Collectors.toSet()));
//            System.out.println(Stream.of(list1,list2).reduce((l1,l2)->{l1.retainAll(l2);return l1;}).orElse(Collections.EMPTY_LIST));
            System.out.println();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void findStockPrepareReportData(String fileLocation, int days) {
        String reportLoc = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024";
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int day = 1;
            if (days > 0)
                day = (3 * days) + 1;
            List<ExpectedStockDetails> result = new ArrayList<>();
            List<FutureStock> futureStocks = new ArrayList<>();
            fileLocation = fileLocation + "\\" + files.get(day);
            System.out.println("reading from :"+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            String expctTomoLoc = "";
            if (fileLocation.contains("_high_low_top"))
                expctTomoLoc = fileLocation.split("_high_low_top")[0]+"_expct_tomrw";
            if (fileLocation.contains("_expct_tomrw"))
                expctTomoLoc = fileLocation.split("_expct_tomrw")[0]+"_high_low_top";
            System.out.println("reading from :"+expctTomoLoc);
            List<String[]> expctStockData = StockUtil.readFileData(expctTomoLoc);
            stockData.addAll(expctStockData);
            for (String[] data : stockData) {
                ExpectedStockDetails esd = ExpectedStockDetails.prepareExpectedStockDetails(data);
                if (!esd.getName().equals("ASTRAL.NS"))
                    continue;
                List<String[]> historyData = StockUtil.loadStockData(esd.getName());
                historyData = historyData.subList(days, historyData.size()-1);
//                historyData = historyData.subList(1, historyData.size()-1);
                List<String[]> reportData = StockUtil.readFileData(reportLoc+"\\"+esd.getName());
                Collections.reverse(reportData);
                reportData = reportData.subList(days,reportData.size()-1);
                String[] todaysReport = reportData.get(0);
                Map<String, Double> expectedMovement = getExpectedMovementdata(esd.getName(), esd.getMrkDirection(), historyData.get(0)[0], days);
                double wkHigh = expectedMovement.get("wkHigh");
                double wkLow = expectedMovement.get("wkLow");
                double monHigh = expectedMovement.get("monHigh");
                double monLow = expectedMovement.get("monLow");
                double expctHigh = expectedMovement.get("expectedHighAmountToday");
                double expctLow = expectedMovement.get("expectedLowAmountToday");
                double open = Double.parseDouble(historyData.get(0)[1]);
                double high = Double.parseDouble(historyData.get(0)[2]);
                double close = Double.parseDouble(historyData.get(0)[4]);
                double low = Double.parseDouble(historyData.get(0)[3]);
                if(esd.getSelectCategory().equals("LowEqual")){
                    //find  expected high
                    if (expctHigh <= close && Double.parseDouble(todaysReport[9]) < 50.00){
                        result.add(esd);
                    }
                    futureStocks.add(FutureStock.builder().stockName(esd.getName()).monHigh(monHigh).monLow(monLow).wkHigh(wkHigh).wkLow(wkLow)
                            .exctMrktDirection("High").entryPoint(expctHigh+1).build());
                }else if(esd.getSelectCategory().equals("HighEqual")) {
                    //find  expected low
                    if (expctLow >= close && Double.parseDouble(todaysReport[9]) > 60.00){
                        result.add(esd);
                    }
                    futureStocks.add(FutureStock.builder().stockName(esd.getName()).monHigh(monHigh).monLow(monLow).wkHigh(wkHigh).wkLow(wkLow)
                            .exctMrktDirection("Low").entryPoint(expctLow-1).build());
                }
//                else if(esd.getSelectCategory().equals("Breakout-NEUTRAL")){
//                    double hg = open < close? close : open;
//                    double lw = open < close? open : close;
//                    if (expctHigh <=hg && expctLow >= lw) {
//                        result.add(esd);
//                        futureStocks.add(FutureStock.builder().stockName(esd.getName()).monHigh(monHigh).monLow(monLow).wkHigh(wkHigh).wkLow(wkLow)
//                                .exctMrktDirection("NEUTRAL").entryDesc("As NEUTRAL Buy-" + (wkHigh + 1) + " Sell-" + (wkLow - 1)).build());
//                    }
//                }

//                if ((esd.getMrkDirection().equals("DOWN") && esd.getSelectCategory().equals("LowEqual") && esd.getTodayMove().equals("GREEN")
//                        && Double.parseDouble(todaysReport[9]) < 50.00)
//                        || (esd.getMrkDirection().equals("UP") && esd.getSelectCategory().equals("HighEqual") && esd.getTodayMove().equals("RED")
//                        && Double.parseDouble(todaysReport[9]) > 60.00)){
//                    result.add(esd);
//                }
            }

            processFutureAndStore(futureStocks);
            result.stream().forEach(e->System.out.println(e));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void processFutureAndStore(List<FutureStock> futureStocks) {
        String fileLoc = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\future_stocks";
        try {
//            List<String> files = Files.list(Paths.get(fileLoc))
//                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
//            files.sort(Comparator.reverseOrder());
            String thisWkDate = DateUtil.getThisWeekModay();
            String fileName = "stocks_"+thisWkDate;
            String filePath = fileLoc+"\\"+fileName;
            List<String[]> stockData = StockUtil.readFileData(filePath);
            if (stockData.isEmpty()){
                StockUtil.storeFile(filePath, futureStocks.stream().map(e->e.toString().split(",")).collect(Collectors.toList()));
            }else {
                List<String[]> newDataSet = stockData;
                Map<String, FutureStock> data = FutureStock.loadAllData(stockData);
                for (FutureStock fs : futureStocks){
                    if (!data.containsKey(fs.getStockName())){
                        newDataSet.add(fs.toString().split(","));
                    }
                }
                StockUtil.storeFile(filePath, newDataSet);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        finalizeStocks();

//        divideBasedLogic();
//        findCommonStockUsingAllCriteria();
//        validateStock sToTradeOption();
//        findStocksNoTradeBreak();
//        findStocksNoTradeBreakAndVolDetails();
//        storeCandleWiseStock();
//        filterStocksBasedPreviousCandles(); //filterbasedcandle


        //expected movement
        String fileLocation = "";
        Set<ExpectedCandle> fList = null;
//        fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1";
//        fList = findBestStockForMovement(fileLocation);
//        if (fList.isEmpty()){
            fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\stock";
            fList = findBestStockForMovement(fileLocation,0, "allStockCandle");
//        }

//        findCommonElements();

        //find only toady trend break stocks
//        findStocksTradeBreakToday();

        //find stock using prepare report
//        String location = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\expectedMove";
//         findStockPrepareReportData(location, 1);

        findHighLowEqualStocks(0);
//        verifyHighLowEqualStocks(0);

    }

    public static void runJobs() {
        validateStocksToConfirm();
        StocksRuleCreateUpdateJob.readConfirmStocksAndPrepareRule();
    }
}
