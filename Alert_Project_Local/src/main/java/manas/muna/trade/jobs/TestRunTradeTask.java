package manas.muna.trade.jobs;

import manas.muna.trade.util.StockPropertiesUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.StockDetails;
import org.jsoup.internal.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class TestRunTradeTask {
    public static void main(String[] args){
        //Read History Data
//        try {
////            StoreStockHistoryToCvsJob.testexecute();
//            StoreStockHistoryToCvsJob.execute();
//        }catch (Exception e){
//            System.out.println("Error during history data read");
//        }

//        Read Excel And Calculate EMA
//        try {
//            Thread.sleep(12);
////            ReadingExcelAndCalculateEMAJob.testexecute();
//            ReadingExcelAndCalculateEMAJob.execute();
//        }catch (Exception e){
//            System.out.println("Error calculate EMA");
//        }

        //Send Green Notification
        try {
            Thread.sleep(12);
//            StockEmaTradeStartStatusNotificationJob.testexecute();
            StockEmaTradeStartStatusNotificationJob.execute();
        }catch (Exception e){
            System.out.println("Error during send green notification");
        }

        //Send Red Notification
//        try {
//            Thread.sleep(12);
//            StockEmaRedNotificationForBuyStockJob.testexecute();
//        }catch (Exception e){
//            System.out.println("Error during send red notification");
//        }
//        try {
//            Thread.sleep(12);
//            RunIndexOptionTrade.calculateOptionLogic();
//        } catch (Exception e) {
//            System.out.println("Error during option calculateOptionLogic");
//        }

        //Test deletiomn of EMA row record
//        String[] stocks = StockUtil.loadTestStockNames();
//        for (String stockName: stocks) {
////            StockUtil.loadEmaData(stockName);
//            StockUtil.deleteRecordFromEmaData(stockName,1);
//        }

        //Test run execution date
//        StockUtil.updateExceutiondate();

        //Test if execution date correct
//        System.out.println(StockUtil.isExecutionDataAvailableCorrect());

        //expory date check
//        RunOptionTrade.calculateOptionLogic();
//        RunOptionTrade.isLastMonthSupport("","CIPLA.NS","");
        //check
//        StockUtil.checkDateAnddata("2023-07-04");
        //Test backup job
//        RunBackUpJob.backupEmaData();
        //Test option stock symbol
//        List<String> testName = StockPropertiesUtil.getOptionStockSymbol();
//        System.out.println(testName.contains("MARI"));
//
//        System.out.println("ASIANTILES.NS".substring(0,"ASIANTILES.NS".indexOf('.')));

        //check option stock
//        RunOptionTrade.calculateOptionLogicForStock("ATUL");

//        //test sorting logic
//        List<StockDetails> list = new ArrayList<>();
//        list.add(StockDetails.builder()
//                        .highVolumeCompareDays(2)
//                        .volume(21000)
//                        .stockName("Adgd")
//                        .isGreenRed("Green")
//                .build());
//        list.add(StockDetails.builder()
//                .highVolumeCompareDays(2)
//                .volume(53434)
//                .stockName("Ccecec")
//                .isGreenRed("Green")
//                .build());
//        list.add(StockDetails.builder()
//                .highVolumeCompareDays(5)
//                .volume(21000)
//                .stockName("Bbbbb")
//                .isGreenRed("Red")
//                .build());
//        list.add(StockDetails.builder()
//                .highVolumeCompareDays(3)
//                .volume(3245)
//                .stockName("Dddd")
//                .isGreenRed("Green")
//                .build());
//        list.add(StockDetails.builder()
//                .highVolumeCompareDays(4)
//                .volume(7554)
//                .stockName("Vvvv")
//                .isGreenRed("Red")
//                .build());
//        List<StockDetails> ls = StockUtil.sortStockDataBasedOnVolumeSizeThenCompareDays(list);
//        System.out.println(ls);
    }
}
