package manas.muna.trade.jobs;

import manas.muna.trade.util.StockPropertiesUtil;
import manas.muna.trade.util.StockUtil;

import java.util.List;

public class ConditionalJob {

    public static void main(String args[]) {

        List<String[]> stockData = StockUtil.loadStockData("^NSEI");
        String[] stockYesdData = stockData.get(0);
        String sDate = stockYesdData[0];

        //Read History Data
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("readHistoryDataIndicator")) {
            try {
                StoreStockHistoryToCvsJob.execute();
            } catch (Exception e) {
                System.out.println("Error during history data read");
            }
        }

        //Read Excel And Calculate EMA
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("readExcelCalculateEMAIndicator")) {
            try {
                if (StockPropertiesUtil.getBooleanIndicatorProps().get("readExcelCalculateEMADataValidationCheckIndicator")) {
                    if (StockUtil.isExecutionDataAvailableCorrect() && StockUtil.checkDateAnddata(sDate)) {
                        Thread.sleep(120000);
                        ReadingExcelAndCalculateEMAJob.execute();
                    }
                } else {
                    Thread.sleep(120000);
                    ReadingExcelAndCalculateEMAJob.execute();
                }
            } catch (Exception e) {
                System.out.println("Error calculate EMA");
            }
        }

        //Send Green Notification
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("emaTradeStatusCheckIndicator")) {
            try {
                Thread.sleep(120000);
                StockEmaTradeStartStatusNotificationJob.execute();
            } catch (Exception e) {
                System.out.println("Error during send green notification");
            }
        }
    }
}
