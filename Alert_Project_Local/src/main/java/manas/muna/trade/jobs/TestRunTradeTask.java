package manas.muna.trade.jobs;

import manas.muna.trade.util.StockUtil;
import org.jsoup.internal.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class TestRunTradeTask {
    public static void main(String[] args){
        //Read History Data
//        try {
//            StoreStockHistoryToCvsJob.execute();
//        }catch (Exception e){
//            System.out.println("Error during history data read");
//        }

//        Read Excel And Calculate EMA
//        try {
//            Thread.sleep(12);
//            ReadingExcelAndCalculateEMAJob.execute();
//        }catch (Exception e){
//            System.out.println("Error calculate EMA");
//        }

        //Send Green Notification
//        try {
//            Thread.sleep(12);
//            StockEmaTradeStartStatusNotificationJob.testexecute();
//        }catch (Exception e){
//            System.out.println("Error during send green notification");
//        }

        //Send Red Notification
//        try {
//            Thread.sleep(12);
//            StockEmaRedNotificationForBuyStockJob.testexecute();
//        }catch (Exception e){
//            System.out.println("Error during send red notification");
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
        System.out.println(StockUtil.isExecutionDataAvailableCorrect());

        //expory date check
//        RunOptionTrade.calculateOptionLogic();
//        RunOptionTrade.isLastMonthSupport("","CIPLA.NS","");
        //check
//        StockUtil.checkDateAnddata("2023-07-04");
    }
}
