package manas.muna.trade.jobs;

public class RunTradeTaskForOptionTrade {
    public static void main(String[] args){
        //Read History Data weekly
        try {
            StoreStockHistoryToCvsJob.execute();
        }catch (Exception e){
            System.out.println("Error during history data read");
        }

//        Read Excel And Calculate EMA
        try {
            Thread.sleep(120000);
            ReadingExcelAndCalculateEMAJob.execute();
        }catch (Exception e){
            System.out.println("Error calculate EMA");
        }

        //Send Green Notification
        try {
            Thread.sleep(120000);
            StockEmaTradeStartStatusNotificationJob.execute();
        }catch (Exception e){
            System.out.println("Error during send green notification");
        }

        //Send Red Notification
        try {
            Thread.sleep(120000);
            StockEmaRedNotificationForBuyStockJob.execute();
        }catch (Exception e){
            System.out.println("Error during send red notification");
        }
    }
}
