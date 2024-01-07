package manas.muna.trade.patterns;

import manas.muna.trade.constants.CandleConstant;
import manas.muna.trade.jobs.StockEmaTradeStartStatusNotificationJob;
import manas.muna.trade.stocksRule.StocksRuleCreateUpdateJob;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockPropertiesUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;
import manas.muna.trade.vo.StockDetails;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
        List<StockDetails> finalizeStocks = new ArrayList<>();
        List<StockDetails> stocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            fileLocation = fileLocation + "\\" + files.get(0);
            System.out.println("Reading from-"+fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
//            List<String> optionStockNames = StockPropertiesUtil.getOptionStockSymbol();
            for (String[] str: stockData){
                StockDetails sd = StockUtil.prepareCandleData(str);
                String checkType = "";
                if(sd.getIsGreenRed().equals("GREEN"))
                    checkType = CandleConstant.DESCENDING;
                else if (sd.getIsGreenRed().equals("RED"))
                    checkType = CandleConstant.ACCEDING;
                if (CandleUtil.isTrendSequenceBreak(sd.getStockName(), sd.getTrendDays(),null,checkType)) {
                    stocks.add(sd);
                }
            }
            for (StockDetails sd: stocks){
                System.out.println(sd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        finalizeStocks();

//        divideBasedLogic();
//        findCommonStockUsingAllCriteria();
//        validateStocksToTradeOption();
        findStocksNoTradeBreak();
    }


    public static void runJobs() {
        validateStocksToConfirm();
        StocksRuleCreateUpdateJob.readConfirmStocksAndPrepareRule();
    }
}
