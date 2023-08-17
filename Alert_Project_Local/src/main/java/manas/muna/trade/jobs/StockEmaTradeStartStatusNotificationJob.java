package manas.muna.trade.jobs;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import manas.muna.trade.util.SendMail;
import manas.muna.trade.util.StockUtil;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        for (String stockName : StockUtil.loadStockNames()) {
        for (String stockName : StockUtil.loadAllStockNames()) {
            System.out.println("Starting for stock........"+stockName);
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
            //skipping nifty and banknifty from here
            if (!stockName.equals("^NSEI") || !stockName.equals("^NSEBANK")) {
                Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\" + stockName + ".csv");
                Map<String, String> notificationData = StockUtil.readEmaData(path.toString(), stockName);
                verifyAndSenfNotification(notificationData);
            }
        }
        System.out.println("StockEmaTradeStartStatusNotificationJob end.......");
    }

    public static void testexecute() {
        System.out.println("StockEmaTradeStartStatusNotificationJob started.......");
        for (String stockName : StockUtil.loadTestStockNames()) {
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
            Map<String, String> notificationData = StockUtil.readEmaData(path.toString(), stockName);
            verifyAndSenfNotification(notificationData);
        }
        System.out.println("StockEmaTradeStartStatusNotificationJob end.......");
    }

    private static void verifyAndSenfNotification(Map<String, String> notificationData) {

        if (Boolean.parseBoolean(notificationData.get("stockIsGreen"))) {
            if (!ReadResultsDateDataJob.validateIsStockResultDateRecently(notificationData.get("stockName"))) {
                SendMail.sendMail(notificationData.get("msg"), notificationData.get("stockName"), notificationData.get("subject"));
                RunOptionTrade.calculateOptionLogicForStock(notificationData.get("stockName").substring(0, notificationData.get("stockName").indexOf('.')));
            }else{
                System.out.println("This "+notificationData.get("stockName")+" has direction but result date has near");
            }
        }
        if (Boolean.parseBoolean(notificationData.get("stockIsRed"))) {
            if (!ReadResultsDateDataJob.validateIsStockResultDateRecently(notificationData.get("stockName"))) {
//            SendMail.sendMail(notificationData.get("msg"), notificationData.get("stockName"), notificationData.get("subject"));
                RunOptionTrade.calculateOptionLogicForStock(notificationData.get("stockName").substring(0, notificationData.get("stockName").indexOf('.')));
            }else{
                System.out.println("This "+notificationData.get("stockName")+" has direction but result date has near");
            }
        }
    }
}
