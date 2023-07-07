package manas.muna.trade.jobs;

import manas.muna.trade.util.StockUtil;

import java.util.Set;

public class DeleteJobs {
    public static void main(String[]  args){
        runDeleteRowsFromEmaData(1);
    }

    public static void runDeleteRowsFromEmaData(int rowCount) {
        Set<String> stocks = StockUtil.loadAllStockNames();
//        String[] stocks = StockUtil.loadTestStockNames();
        for (String stockName: stocks) {
//            StockUtil.loadEmaData(stockName);
            StockUtil.deleteRecordFromEmaData(stockName,rowCount);
        }
    }
}
