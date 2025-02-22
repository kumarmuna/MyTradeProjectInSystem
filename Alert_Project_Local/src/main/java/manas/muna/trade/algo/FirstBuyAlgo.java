package manas.muna.trade.algo;

import manas.muna.trade.constants.CandleTypes;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;

import java.util.List;

public class FirstBuyAlgo {

    public static boolean verifyFirstAlgo(String stockName, String candlePatter) {
        boolean pass = false;
        List<String[]> historyData = StockUtil.loadStockData(stockName);
//        historyData = historyData.subList(6, historyData.size()-1);
        CandleStick preCandl = CandleUtil.prepareCandleData(historyData.get(3),historyData.get(2));
        CandleStick postCandl = CandleUtil.prepareCandleData(historyData.get(1),historyData.get(0));
        CandleStick curCandl = CandleUtil.prepareCandleData(historyData.get(2),historyData.get(1));

        if (preCandl.getCandleType().equalsIgnoreCase("SolidRed") && curCandl.getCandleType().contains("Green")
                && CandleTypes.getAllDojiCanldeNames().contains(candlePatter)){
            if (postCandl.getLow() > preCandl.getLow() && curCandl.getClose() < postCandl.getHigh()){
                pass = true;
                System.out.println("Its BUYING signal.....");
            }
        }

        return pass;
    }
    public static void main(String[] args) {
        verifyFirstAlgo("ASTRAL.NS", "SomeDojiPattern");
    }
}
