package manas.muna.trade.jobs;

import manas.muna.trade.util.SendMail;
import manas.muna.trade.util.StockUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class StockEmaRedNotificationForBuyStockJob {
//    public static void main(String args[]) {
//        for (String stockName : StockUtil.loadBuyStockNames()) {
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
//            Map<String, String> notificationData = StockUtil.readEmaBuyStok(stockEmaDataLoad, stockName);
//            verifyAndSenfNotification(notificationData);
//        }
//    }

    public static void execute() {
        System.out.println("StockEmaRedNotificationForBuyStockJob started.......");
//        if(StringUtils.isEmpty(StockUtil.loadBuyStockNames())){
        String[] stList = StockUtil.loadBuyStockNames();
        if (stList.length!=0) {
            for (String stockName : stList) {
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
                Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\" + stockName + ".csv");
                Path ema_path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\" + stockName + ".csv");
                Map<String, String> notificationData = StockUtil.readEmaBuyStok(ema_path.toString(), stockName);
                //Read previous close and compare with today's close
//            Path path1 = Paths.get("D:\\share-market\\Alert_Project_Local\\src\\main\\resources\\profit_loss\\"+stockName+".csv");
                Map<String, String> notificationCloseData = StockUtil.readPreviousdayClose(path.toString(), stockName);
                verifyAndSenfNotification(notificationCloseData, notificationData);
            }
        }else{
            if (!(StockUtil.loadBuyStockFileData("new_buy_stock").length == 0)) {
                String[] stockNames = StockUtil.loadBuyStockFileData("new_buy_stock");
                if(!stockNames[0].isEmpty()) {
                    for (String stockName : stockNames) {
                        CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName, "Yesterday");
                    }
                }
            }
        }
        System.out.println("StockEmaRedNotificationForBuyStockJob end.......");
    }

    public static void testexecute() {
        System.out.println("StockEmaRedNotificationForBuyStockJob started.......");
        String[] stList = StockUtil.loadBuyStockNames();
        if (stList.length!=0) {
            for (String stockName : stList) {
    //            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
                Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\" + stockName + ".csv");
                Path ema_path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\" + stockName + ".csv");
                Map<String, String> notificationData = StockUtil.readEmaBuyStok(ema_path.toString(), stockName);
                //Read previous close and compare with today's close
    //            Path path1 = Paths.get("D:\\share-market\\Alert_Project_Local\\src\\main\\resources\\profit_loss\\"+stockName+".csv");
                Map<String, String> notificationCloseData = StockUtil.readPreviousdayClose(path.toString(), stockName);
                verifyAndSenfNotification(notificationCloseData, notificationData);
            }
        }else{
            if (!(StockUtil.loadBuyStockFileData("new_buy_stock").length == 0)) {
                String[] stockNames = StockUtil.loadBuyStockFileData("new_buy_stock");
                if(!stockNames[0].isEmpty()) {
                    for (String stockName : stockNames) {
                        CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName, "Yesterday");
                    }
                }
            }
        }
        System.out.println("StockEmaRedNotificationForBuyStockJob end.......");
    }

    private static void verifyAndSenfNotification(Map<String, String> notificationCloseData,Map<String, String> notificationData) {
        //this is ema based compare and update
        if (Boolean.parseBoolean(notificationData.get("stockIsRed"))){
            CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(notificationData.get("stockName"));
            SendMail.sendMail(notificationData.get("msg"), notificationData.get("stockName"), notificationData.get("subject"));
        }
//        if (Boolean.parseBoolean(notificationCloseData.get("isRedToday"))){
//            CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(notificationCloseData.get("stockName"));
////            SendMail.sendMail(notificationCloseData.get("msg"), notificationCloseData.get("stockName"), notificationCloseData.get("subject"));
//        }
        if (!(StockUtil.loadBuyStockFileData("new_buy_stock").length == 0)) {
            String[] stockNames = StockUtil.loadBuyStockFileData("new_buy_stock");
            if(!stockNames[0].isEmpty()) {
                for (String stockName : stockNames) {
                    CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName, "Yesterday");
                }
            }
        }
    }
}
