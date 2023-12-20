package manas.muna.trade.jobs;

import com.google.common.collect.ComparisonChain;
import manas.muna.trade.patterns.CandlestickBullishPatterns;
import manas.muna.trade.util.StockPropertiesUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.EmaChangeDetails;
import manas.muna.trade.vo.StockDetails;
import org.jsoup.internal.StringUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class TestRunTradeTask {
    public static void main(String[] args){
        //Read History Data
//        try {
//            StoreStockHistoryToCvsJob.testexecute();
////            StoreStockHistoryToCvsJob.execute();
//        }catch (Exception e){
//            System.out.println("Error during history data read");
//        }

        //remove null value and update file
//        try{
//            String path = "";
////            for (String stockName : StockUtil.loadTestStockNames()) {
//            for (String stockName : StockUtil.loadAllStockNames()) {
//                path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv";
//                System.out.println("Loading for.... "+stockName);
//                StockUtil.removeNullDataUpdateFile(stockName, path);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        Read Excel And Calculate EMA
//        try {
//            Thread.sleep(12);
//            ReadingExcelAndCalculateEMAJob.testexecute();
////            ReadingExcelAndCalculateEMAJob.execute();
//        }catch (Exception e){
//            System.out.println("Error calculate EMA");
//        }

        //Send Green Notification
//        try {
//            Thread.sleep(12);
////            StockEmaTradeStartStatusNotificationJob.testexecute();
//            StockEmaTradeStartStatusNotificationJob.execute();
//        }catch (Exception e){
//            System.out.println("Error during send green notification");
//        }

        //Send Red Notification
//        try {
//            Thread.sleep(12);
////            StockEmaRedNotificationForBuyStockJob.testexecute();
//            StockEmaRedNotificationForBuyStockJob.execute();
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
//        try {
//            List<String> stockList = RunTradeTask.prepareStockList();
//            Thread.sleep(120000);
//            RunOptionTrade.calculateOptionLogic(stockList);
//        } catch (Exception e) {
//            System.out.println("Error during option calculateOptionLogic");
//        }

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
//        double val = StockUtil.calculateTarget(5.0, 3, 100);
//        System.out.println(val);

//        int daysCheck = (int)StockUtil.getDaysDiff(StockUtil.getLastMonthFirstDayDate(), new Date());
//        System.out.println("days="+daysCheck);

        //testing sort logic
//        List<StockDetails> list = new ArrayList<>();
//        list.add(StockDetails.builder().stockName("A").volume(10).isGreenRed("RED").highVolumeCompareDays(5).build());
//        list.add(StockDetails.builder().stockName("c").volume(30).isGreenRed("GREEN").highVolumeCompareDays(5).build());
//        list.add(StockDetails.builder().stockName("B").volume(20).isGreenRed("RED").highVolumeCompareDays(5).build());
//        list.add(StockDetails.builder().stockName("D").volume(50).isGreenRed("GREEN").highVolumeCompareDays(5).build());
//        list.add(StockDetails.builder().stockName("F").volume(32).isGreenRed("GREEN").highVolumeCompareDays(5).build());
//        list.add(StockDetails.builder().stockName("E").volume(70).isGreenRed("RED").highVolumeCompareDays(5).build());
//        list = StockUtil.sortStockDataBasedOnCompareDaysThenVolume(list);
//        System.out.println(list);

//        test EMA direction change
//        RunTradeTask.loadEmaChangeStockData();
//        StockUtil.isWeekLowWithin3Days("3PLAND.NS");
        StockEmaTradeStartStatusNotificationJob.newExecute();
//        StockEmaTradeStartStatusNotificationJob.newExecuteWithTrendStocks();
//        List<String[]> stockEmaData = StockUtil.loadStockData("CAMPUS.NS");
//        CandlestickBullishPatterns.isBullishEngulfingOccurs("CAMPUS.NS");
//        CandlestickBullishPatterns.isInvertedHammer("CAMPUS.NS", stockEmaData);
    }
}
