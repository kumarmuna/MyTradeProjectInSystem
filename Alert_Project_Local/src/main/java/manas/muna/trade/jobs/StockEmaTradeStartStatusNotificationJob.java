package manas.muna.trade.jobs;

import com.google.common.collect.ComparisonChain;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import manas.muna.trade.patterns.CandlestickBullishPatterns;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.SendMail;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;
import manas.muna.trade.vo.EmaChangeDetails;
import manas.muna.trade.vo.StockDetails;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class StockEmaTradeStartStatusNotificationJob {
//    public static void main(String args[]){
//        for (String stockName : StockUtil.loadStockNames()) {
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
//            Map<String, String> notificationData = StockUtil.readEmaData(stockEmaDataLoad, stockName);
//            verifyAndSenfNotification(notificationData);
//        }
//    }

    public static void execute() {
        System.out.println("StockEmaTradeStartStatusNotificationJob started.......");
        Map<String, String> notificationData = new HashMap<>();
//        for (String stockName : StockUtil.loadStockNames()) {
        for (String stockName : StockUtil.loadAllStockNames()) {
            System.out.println("Starting for stock........"+stockName);
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
            //skipping nifty and banknifty from here
//            if (!stockName.equals("^NSEI") || !stockName.equals("^NSEBANK")) {
                Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\" + stockName + ".csv");
                if (StockUtil.checkNewAddedstock(stockName)){
                    StockUtil.readEmaDataModify(path.toString(), stockName);
                }else {
                    StockUtil.readEmaData(path.toString(), stockName);
                }
//                verifyAndSenfNotification(notificationData);
//            }
        }
        System.out.println("Preparing Notification to send mail");
        notificationData = StockUtil.getStoCKTradeDetailsAndPrepareNotificationMessage();
        sendNotificationToMail(notificationData);
        System.out.println("StockEmaTradeStartStatusNotificationJob end.......");
    }

    public static void testexecute() {
        System.out.println("StockEmaTradeStartStatusNotificationJob started.......");
        Map<String, String> notificationData = new HashMap<>();
        for (String stockName : StockUtil.loadTestStockNames()) {
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
            if (StockUtil.checkNewAddedstock(stockName)){
                StockUtil.readEmaDataModify(path.toString(), stockName);
            }else {
                StockUtil.readEmaData(path.toString(), stockName);
            }
        }
        System.out.println("Preparing Notification to send mail");
        notificationData = StockUtil.getStoCKTradeDetailsAndPrepareNotificationMessage();
//        verifyAndSenfNotification(notificationData);
//        sendNotificationToMail(notificationData);
        System.out.println("StockEmaTradeStartStatusNotificationJob end.......");
    }

    private static void sendNotificationToMail(Map<String, String> notificationData) {
        if (Boolean.parseBoolean(notificationData.get("isStockAvl"))){
            SendMail.sendMail(notificationData.get("stockMsg"), notificationData.get("stockName"), notificationData.get("stockSubject"));
        }
//        if (Boolean.parseBoolean(notificationData.get("isStockEma8And3Avl"))){
//            SendMail.sendMail(notificationData.get("stockEma8And3Msg"), notificationData.get("stockName"), notificationData.get("stockEma8And3Subject"));
//        }
//        if (Boolean.parseBoolean(notificationData.get("isStockDEma9And5Avl"))){
//            SendMail.sendMail(notificationData.get("stockDEma9And5Msg"), notificationData.get("stockName"), notificationData.get("stockDEma9And5Subject"));
//        }
//        if (Boolean.parseBoolean(notificationData.get("isStockBothIndicatorAvl"))){
//            SendMail.sendMail(notificationData.get("stockBothIndicatorMsg"), notificationData.get("stockName"), notificationData.get("stockBothIndicatorSubject"));
//        }
    }

    private static void verifyAndSenfNotification(Map<String, String> notificationData) {

        if (Boolean.parseBoolean(notificationData.get("stockIsGreen"))) {
            if (!ReadResultsDateDataJob.validateIsStockResultDateRecently(notificationData.get("stockName"))) {
                SendMail.sendMail(notificationData.get("msg"), notificationData.get("stockName"), notificationData.get("subject"));
//                RunOptionTrade.calculateOptionLogicForStock(notificationData.get("stockName").substring(0, notificationData.get("stockName").indexOf('.')));
            }else{
                System.out.println("This "+notificationData.get("stockName")+" has direction but result date has near");
            }
        }
        if (Boolean.parseBoolean(notificationData.get("stockIsRed"))) {
            if (!ReadResultsDateDataJob.validateIsStockResultDateRecently(notificationData.get("stockName"))) {
//            SendMail.sendMail(notificationData.get("msg"), notificationData.get("stockName"), notificationData.get("subject"));
//                RunOptionTrade.calculateOptionLogicForStock(notificationData.get("stockName").substring(0, notificationData.get("stockName").indexOf('.')));
            }else{
                System.out.println("This "+notificationData.get("stockName")+" has direction but result date has near");
            }
        }
    }

    public static void newExecute() {
        System.out.println("StockEmaTradeStartStatusNotificationJob started.......");
        List<EmaChangeDetails> stocks = new ArrayList<>();
        Map<String, String> notificationData = new HashMap<>();
        for (String stockName : StockUtil.loadAllStockNames()) {
//        for (String stockName : StockUtil.loadTestStockNames()) {
            System.out.println("Starting for stock........"+stockName);
            if (StockUtil.checkNewAddedstock(stockName)){
                Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\" + stockName + ".csv");
                StockUtil.readEmaDataModifyLogic(path.toString(), stockName, "");
            }
        }
//        for (String stockName : StockUtil.loadStockNames()) {
//        for (String stockName : StockUtil.loadAllStockNames()) {
//            System.out.println("Starting for stock........"+stockName);
//            if (StockUtil.isWeekLowWithin3Days(stockName)){
//                stocks.add(EmaChangeDetails.builder().stockName(stockName).build());
//            }
//        }
//        List<EmaChangeDetails> list = loadEmaChangeStockData();
//        list.addAll(stocks);
//        System.out.println("Preparing Notification to send mail");
//        System.out.println(list.toString());
        List<StockDetails> list = StockUtil.getListStockDetailsOfCross();
//        try {
//            Thread.sleep(60000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        list = StockUtil.separateGreenAndRedStockThenSortBasedOnVolume(list);
        List<StockDetails> refinedList = new ArrayList<>();
        List<StockDetails> refinedListNoCandle = new ArrayList<>();
        for (StockDetails stockDetails: list){
            Map<String, Object> candlePatternsDetail = null;
            List<String[]> stockHistoryData = StockUtil.loadStockData(stockDetails.getStockName());
            if (stockDetails.getIsGreenRed().equals("GREEN"))
                candlePatternsDetail = CandleUtil.checkBullishStockPatterns(stockDetails.getStockName(), stockHistoryData);
            if (stockDetails.getIsGreenRed().equals("RED"))
                candlePatternsDetail = CandleUtil.checkBearishStockPatterns(stockDetails.getStockName(), stockHistoryData);
            if (candlePatternsDetail != null && candlePatternsDetail.get("isValidToTrade")!=null &&Boolean.parseBoolean(candlePatternsDetail.get("isValidToTrade").toString())){
                stockDetails.setCandleTypesOccur(candlePatternsDetail.get("candleTypesOccur").toString());
                stockDetails.setEntryExit(candlePatternsDetail.get("entryExit").toString());
                refinedList.add(stockDetails);
            }else {
                refinedListNoCandle.add(stockDetails);
            }
        }
        list = refinedList;
        if (!list.isEmpty() || list.size()!=0) {
            StringBuilder sb = new StringBuilder();
            for (StockDetails s: list)
                sb.append(s).append(System.lineSeparator());
//            sb.append("stocksNoCandle").append(System.lineSeparator());
//            for (StockDetails s: refinedListNoCandle)
//                sb.append(s).append(System.lineSeparator());
            notificationData.put("isStockAvl", "true");
            notificationData.put("stockMsg", sb.toString()); //+refinedListNoCandle.stream().toArray(String[]::new));
            notificationData.put("stockSubject", "Only low and change direction stocks");
        }
        sendNotificationToMail(notificationData);
        System.out.println("end.......");
        String[] stt = list.toString().split("StockName=");
        String[] stt1 = refinedListNoCandle.toString().split("StockName=");

        for (String ss: stt){
            System.out.println(ss);
        }
        System.out.println("Prints stocks no candle");
        for (String ss: stt1){
            System.out.println(ss);
        }
    }

    public static List<EmaChangeDetails> loadEmaChangeStockData() {
        //test EMA direction change
        List<EmaChangeDetails> list = new ArrayList<>();
        List<EmaChangeDetails> listS = new ArrayList<>();
//        List<String> strList = new ArrayList<>();
//        for (String stockName : StockUtil.loadTestStockNames()) {
        for (String stockName : StockUtil.loadAllStockNames()) {
            System.out.println("Loading for Stock: "+stockName);
            EmaChangeDetails flag = StockUtil.isChangeEmaDirection(stockName);
            if (!flag.isEligible()){
                System.out.println("Its empty");
            }else{
                list.add(flag);
            }
        }
        Collections.sort(list, new Comparator<EmaChangeDetails>() {
            @Override
            public int compare(EmaChangeDetails e1, EmaChangeDetails e2) {
                return ComparisonChain.start()
                        .compare(e2.getEma8Change(), e1.getEma8Change())
//                        .compare(e2.getEma3Change(), e1.getEma3Change())
                        .result();
            }
        });
        listS.addAll(list.stream().limit(3).collect(Collectors.toList()));

        Collections.sort(list, new Comparator<EmaChangeDetails>() {
            @Override
            public int compare(EmaChangeDetails e1, EmaChangeDetails e2) {
                return ComparisonChain.start()
//                        .compare(e2.getEma8Change(), e1.getEma8Change())
                        .compare(e2.getEma3Change(), e1.getEma3Change())
                        .result();
            }
        });
        listS.addAll(list.stream().limit(3).collect(Collectors.toList()));
//        for (EmaChangeDetails ecd : list){
//            strList.add(ecd.toString());
//        }
//        System.out.println(strList);
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\change_direction\\changedirection.txt"))) {
//            for (String line : strList) {
//                bw.write(line);
//                bw.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return listS;
    }

    public static void newExecuteWithTrendStocks() { //trendStocks
        System.out.println("StockEmaTradeStartStatusNotificationJob started.......");
        List<EmaChangeDetails> stocks = new ArrayList<>();
        Map<String, String> notificationData = new HashMap<>();
        for (String stockName : StockUtil.loadAllStockNames()) {
//        for (String stockName : StockUtil.loadTestStockNames()) {
            System.out.println("Starting for stock........"+stockName);
            if (StockUtil.checkNewAddedstock(stockName)){
                Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\" + stockName + ".csv");
                StockUtil.readEmaDataModifyLogic(path.toString(), stockName, "trendStocks");
            }
        }

        List<StockDetails> list = StockUtil.getListTrendStockDetails();
        list = StockUtil.separateGreenAndRedStockThenSortBasedOnTopInTrend(list);
        List<StockDetails> refinedList = new ArrayList<>();
        List<StockDetails> refinedListForSameDirection = new ArrayList<>();
        for (StockDetails stockDetails: list){
            Map<String, Object> candlePatternsDetail = null;
            Map<String, Object> candlePatternsDetailForSameDirection = null;
            List<String[]> stockHistoryData = StockUtil.loadStockData(stockDetails.getStockName());
            //Here we r running check with trend reversal
            if (stockDetails.getIsGreenRed().equals("GREEN")) {
                candlePatternsDetail = CandleUtil.checkBearishStockPatterns(stockDetails.getStockName(), stockHistoryData);
                candlePatternsDetailForSameDirection = CandleUtil.checkBullishStockPatterns(stockDetails.getStockName(), stockHistoryData);
            }
            if (stockDetails.getIsGreenRed().equals("RED")) {
                candlePatternsDetail = CandleUtil.checkBullishStockPatterns(stockDetails.getStockName(), stockHistoryData);
                candlePatternsDetailForSameDirection = CandleUtil.checkBearishStockPatterns(stockDetails.getStockName(), stockHistoryData);
            }
            if (candlePatternsDetail != null && candlePatternsDetail.get("isValidToTrade")!=null &&Boolean.parseBoolean(candlePatternsDetail.get("isValidToTrade").toString())){
                stockDetails.setCandleTypesOccur(candlePatternsDetail.get("candleTypesOccur").toString());
                stockDetails.setEntryExit(candlePatternsDetail.get("entryExit").toString());
                refinedList.add(stockDetails);
            }
            if (candlePatternsDetailForSameDirection != null && candlePatternsDetailForSameDirection.get("isValidToTrade")!=null &&Boolean.parseBoolean(candlePatternsDetailForSameDirection.get("isValidToTrade").toString())){
                if (candlePatternsDetailForSameDirection.get("candleTypesOccur").toString().contains("Harami")) {
                    stockDetails.setCandleTypesOccur(candlePatternsDetailForSameDirection.get("candleTypesOccur").toString());
                    stockDetails.setEntryExit(candlePatternsDetailForSameDirection.get("entryExit").toString());
                    refinedListForSameDirection.add(stockDetails);
                }
            }
        }
        list = refinedList;
        CandleUtil.storeFirstDayFilterStocks(list);
        CandleUtil.storeFirstDayFilterStocksSameDirection(refinedListForSameDirection);

//        if (!list.isEmpty() || list.size()!=0) {
//            notificationData.put("isStockAvl", "true");
//            notificationData.put("stockMsg", list.toString());
//            notificationData.put("stockSubject", "Stocks with only indicators");
//        }
//        sendNotificationToMail(notificationData);
        System.out.println("end.......");
        String[] stt = refinedListForSameDirection.toString().split("StockName=");
        for (String ss: stt){
            System.out.println(ss);
        }
    }

}
