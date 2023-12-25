package manas.muna.trade.patterns;

import manas.muna.trade.jobs.StockEmaTradeStartStatusNotificationJob;
import manas.muna.trade.stocksRule.StocksRuleCreateUpdateJob;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;
import manas.muna.trade.vo.StockDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StocksPatternToConfirmTrade {
    public static void validateStocksToConfirm() {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day1\\stock_details_"+ DateUtil.getYesterdayDate()+".csv";
        List<String[]> stockData = StockUtil.readFileData(path);
        List<StockDetails> filteredStocks = new ArrayList<>();
        List<StockDetails> filteredStocksNotClear = new ArrayList<>();
//        String testStockName = "DEEPAKFERT.NS";
//        List<String[]> testStockData = new ArrayList<>();
//        for (String[] str: stockData){
//            if (str[0].split("= ")[1].equals(testStockName)){
//                testStockData.add(str);
//            }
//        }
//        if (!testStockData.isEmpty() && testStockData.size()!=0) {
        if (!stockData.isEmpty() && stockData.size()!=0) {
            for (String[] sd : stockData) {
                StockDetails stockDetails = CandleUtil.convertStringToStockDetails(sd);
                List<String[]> stockHistoryData = StockUtil.loadStockData(stockDetails.getStockName());
                CandleStick todayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(1), stockHistoryData.get(0));
                CandleStick yesdayCandle = CandleUtil.prepareCandleData(stockHistoryData.get(2), stockHistoryData.get(1));

                if (stockDetails.getIsGreenRed().equals("GREEN")) {
                    if (todayCandle.getCandleType().contains("Solid") && todayCandle.getClose() < yesdayCandle.getClose()) {
                        filteredStocks.add(stockDetails);
                    }else
                        filteredStocksNotClear.add(stockDetails);
                } else if (stockDetails.getIsGreenRed().equals("RED")) {
                    if (todayCandle.getCandleType().contains("Hallow") && todayCandle.getClose() > yesdayCandle.getClose()) {
                        filteredStocks.add(stockDetails);
                    }else
                        filteredStocksNotClear.add(stockDetails);
                }
            }

            //Store filtered stocks to day2
            filteredStocks = StockUtil.separateGreenAndRedStockThenSortBasedOnVolume(filteredStocks);
            CandleUtil.storeSecondDayFilterStocks(filteredStocks, "stock_details");

            filteredStocksNotClear = StockUtil.separateGreenAndRedStockThenSortBasedOnVolume(filteredStocksNotClear);
            CandleUtil.storeSecondDayFilterStocks(filteredStocksNotClear, "stock_details_notclear");
        }
    }

    public static void confirmationCheckDone() {

    }

    public static void main(String[] args) {
        validateStocksToConfirm();
        confirmationCheckDone();
//        StockEmaTradeStartStatusNotificationJob.newExecuteWithTrendStocks();
    }

    public static void runJobs() {
        validateStocksToConfirm();
        StocksRuleCreateUpdateJob.readConfirmStocksAndPrepareRule();
        confirmationCheckDone();
        StockEmaTradeStartStatusNotificationJob.newExecuteWithTrendStocks();
    }
}
