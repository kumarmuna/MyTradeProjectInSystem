package manas.muna.trade.algo;

import manas.muna.trade.constants.CandleTypes;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;

import java.util.List;

public class FirstSellAlgo {

    public static boolean verifyFirstAlgo(String stockName, String candlePattern) {
        boolean pass = false;
        List<String[]> historyData = StockUtil.loadStockData(stockName);
//        historyData = historyData.subList(6, historyData.size()-1);
        CandleStick preCandl = CandleUtil.prepareCandleData(historyData.get(3),historyData.get(2));
        CandleStick postCandl = CandleUtil.prepareCandleData(historyData.get(1),historyData.get(0));
        CandleStick curCandl = CandleUtil.prepareCandleData(historyData.get(2),historyData.get(1));

        if (preCandl.getCandleType().equalsIgnoreCase("HallowGreen") && postCandl.getCandleType().equalsIgnoreCase("HallowGreen")
                && curCandl.getCandleType().contains("Red")
                && CandleTypes.getAllDojiCanldeNames().contains(candlePattern)){
            if (postCandl.getHigh() < preCandl.getHigh()){
                pass = true;
            }
        }
        return pass;
    }
    public static void main(String[] args) {

    }
}
