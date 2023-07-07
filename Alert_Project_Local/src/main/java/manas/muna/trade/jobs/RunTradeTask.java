package manas.muna.trade.jobs;

import manas.muna.trade.util.StockUtil;

import java.util.List;

public class RunTradeTask {
    public static void main(String[] args){

        List<String[]> stockData = StockUtil.loadStockData("^NSEI");
        String[] stockYesdData = stockData.get(0);
        String sDate = stockYesdData[0];
        //Read History Data
        try {
            StoreStockHistoryToCvsJob.execute();
        }catch (Exception e){
            System.out.println("Error during history data read");
        }

        if (StockUtil.isExecutionDataAvailableCorrect() && StockUtil.checkDateAnddata(sDate)) {
            StockUtil.updateExceutiondate();
//        Read Excel And Calculate EMA
            try {
                Thread.sleep(120000);
                ReadingExcelAndCalculateEMAJob.execute();
            } catch (Exception e) {
                System.out.println("Error calculate EMA");
            }

            //Send Green Notification
            try {
                Thread.sleep(120000);
                StockEmaTradeStartStatusNotificationJob.execute();
            } catch (Exception e) {
                System.out.println("Error during send green notification");
            }

//        Send Red Notification
            try {
                Thread.sleep(120000);
                StockEmaRedNotificationForBuyStockJob.execute();
            } catch (Exception e) {
                System.out.println("Error during send red notification");
            }
//
//            //Run Index option trade
            try {
                Thread.sleep(120000);
                RunIndexOptionTrade.calculateOptionLogic();
            } catch (Exception e) {
                System.out.println("Error during option calculateOptionLogic");
            }

            //Run option trade
            try {
                Thread.sleep(120000);
                RunOptionTrade.calculateOptionLogic();
            } catch (Exception e) {
                System.out.println("Error during option calculateOptionLogic");
            }
        }else {
            System.out.println("Data is not correct. Kindly check update/current Data not loaded");
        }
    }
}
