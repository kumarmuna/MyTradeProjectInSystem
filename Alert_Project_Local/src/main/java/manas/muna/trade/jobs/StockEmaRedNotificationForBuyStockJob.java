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
        for (String stockName : StockUtil.loadBuyStockNames()) {
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
            Path ema_path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
            Map<String, String> notificationData = StockUtil.readEmaBuyStok(ema_path.toString(), stockName);
            //Read previous close and compare with today's close
//            Path path1 = Paths.get("D:\\share-market\\Alert_Project_Local\\src\\main\\resources\\profit_loss\\"+stockName+".csv");
            Map<String, String> notificationCloseData = StockUtil.readPreviousdayClose(path.toString(), stockName);
            verifyAndSenfNotification(notificationCloseData,notificationData);
        }
        System.out.println("StockEmaRedNotificationForBuyStockJob end.......");
    }

    public static void testexecute() {
        System.out.println("StockEmaRedNotificationForBuyStockJob started.......");
        for (String stockName : StockUtil.loadTestBuyStockNames()) {
//            String stockEmaDataLoad = "D:\\share-market\\history_ema_data\\"+stockName+".csv";
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
            Path ema_path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
            Map<String, String> notificationData = StockUtil.readEmaBuyStok(ema_path.toString(), stockName);
            //Read previous close and compare with today's close
//            Path path1 = Paths.get("D:\\share-market\\Alert_Project_Local\\src\\main\\resources\\profit_loss\\"+stockName+".csv");
            Map<String, String> notificationCloseData = StockUtil.readPreviousdayClose(path.toString(), stockName);
            verifyAndSenfNotification(notificationCloseData,notificationData);
        }
        System.out.println("StockEmaRedNotificationForBuyStockJob end.......");
    }

    private static void verifyAndSenfNotification(Map<String, String> notificationCloseData,Map<String, String> notificationData) {
        if (Boolean.parseBoolean(notificationData.get("stockIsRed"))){
            SendMail.sendMail(notificationData.get("msg"), notificationData.get("stockName"), notificationData.get("subject"));
        }
        if (Boolean.parseBoolean(notificationCloseData.get("isRedToday"))){
            SendMail.sendMail(notificationCloseData.get("msg"), notificationCloseData.get("stockName"), notificationCloseData.get("subject"));
        }
    }
}
