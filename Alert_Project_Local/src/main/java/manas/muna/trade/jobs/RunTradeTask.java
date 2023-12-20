package manas.muna.trade.jobs;

import com.google.common.collect.ComparisonChain;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.EmaChangeDetails;
import manas.muna.trade.vo.StockDetails;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RunTradeTask {
    public static void main(String[] args){
        List<String[]> stockData = null;
        try {
            stockData = StockUtil.loadStockData("^NSEI");
        }catch (Exception e){
            System.exit(0);
        }
        String[] stockYesdData = stockData.get(0);
        String sDate = stockYesdData[0];
//        Read History Data
        try {
            StoreStockHistoryToCvsJob.execute();
        }catch (Exception e){
            System.out.println("Error during history data read");
        }

        if (StockUtil.isExecutionDataAvailableCorrect() && StockUtil.checkDateAnddata(sDate)) {
//        if (StockUtil.isExecutionDataAvailableCorrect()) {
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
//            try {
//                Thread.sleep(120000);
//                RunIndexOptionTrade.calculateOptionLogic();
//            } catch (Exception e) {
//                System.out.println("Error during option calculateOptionLogic");
//            }

//            Run option trade
            try {
                List<String> stockList = prepareStockList();
                Thread.sleep(120000);
                RunOptionTrade.calculateOptionLogic(stockList);
            } catch (Exception e) {
                System.out.println("Error during option calculateOptionLogic");
            }
        }else {
            System.out.println("Data is not correct. Kindly check update/current Data not loaded");
        }
    }

    public static List<String> prepareStockList() {
        List<String> stockNames = new ArrayList<>();
//        List<StockDetails> bothStockData = StockUtil.getListStockDetailsToSendMailForBothIndicator();
        List<StockDetails> stockData83 = StockUtil.getListStockDetailsToSendMailForEMA8And3();
//        List<StockDetails> stock95 = StockUtil.getListStockDetailsToSendMailForDEMA9And5();
//        List<StockDetails> stocks = StockUtil.getListStockDetailsOfCross();
//        bothStockData.addAll(stockData83);
//        bothStockData.addAll(stock95);
//        bothStockData.addAll(stocks);
        for (StockDetails sd : stockData83){
            if(!ReadResultsDateDataJob.validateIsStockResultDateRecently(sd.getStockName())) {
                stockNames.add(sd.getStockName());
            }else
                System.out.println(sd.getStockName()+" has recently result day");
        }
        return stockNames;
    }


}
