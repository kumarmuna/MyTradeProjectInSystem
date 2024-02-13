package manas.muna.trade.patterns;

import manas.muna.trade.constants.CandleConstant;
import manas.muna.trade.constants.CandleTypes;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandlestickBearishPatterns {

    public static boolean isBearishAbandonedBaby(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1),stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2),stockEmaData.get(1));
        CandleStick prevPrevCandle = CandleUtil.prepareCandleData(stockEmaData.get(3),stockEmaData.get(2));
        if (prevPrevCandle.getCandleType().contains("HallowGreen") && todayCandle.getCandleType().contains("Solid")){
            if (prevCandle.getOpen() > prevPrevCandle.getClose() && prevCandle.getClose() > prevPrevCandle.getClose()
                && todayCandle.getOpen() < prevCandle.getOpen() && todayCandle.getOpen() < prevCandle.getClose()
                    && prevCandle.getLow() >= prevPrevCandle.getHigh() && todayCandle.getHigh() <= prevCandle.getLow()
                    && todayCandle.getClose() < (prevPrevCandle.getOpen()+ (prevPrevCandle.getClose()-prevPrevCandle.getOpen()/2))
                && isDojis(stockName, stockEmaData.subList(1, 3))){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isEngulfingBearish(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1),stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2),stockEmaData.get(1));
        if(prevCandle.getCandleType().contains("Hallow") && todayCandle.getCandleType().contains("SolidRed")){
            if (todayCandle.getOpen() >= prevCandle.getClose() && todayCandle.getClose() < prevCandle.getOpen()){
//                && prevCandle.getHigh() < todayCandle.getHigh() && prevCandle.getLow()>todayCandle.getClose()){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isHaramiBearish(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if((prevCandle.getCandleType().equals("HallowGreen") || prevCandle.getCandleType().equals("Solid"))
                && todayCandle.getCandleType().contains("Solid")){
            if(todayCandle.getHigh()< prevCandle.getClose() && todayCandle.getLow()>prevCandle.getOpen()){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isDarkCloudCover(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().equals("HallowGreen") && todayCandle.getCandleType().contains("Solid")){
            if (todayCandle.getOpen() > prevCandle.getClose() && todayCandle.getClose()> prevCandle.getClose()
                    && todayCandle.getClose() < (prevCandle.getOpen()+((prevCandle.getClose()-prevCandle.getOpen())/2))){
//                && todayCandle.getLow() > todayCandle.getLow()){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isEveningStar(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        CandleStick prevPrevCandle = CandleUtil.prepareCandleData(stockEmaData.get(3), stockEmaData.get(2));
        if (prevPrevCandle.getCandleType().equals("HallowGreen") && todayCandle.getCandleType().contains("Solid")){
            if (prevCandle.getOpen() > prevPrevCandle.getClose() && prevCandle.getClose() > prevPrevCandle.getClose()
                    && todayCandle.getOpen() < prevCandle.getClose()
                    && prevCandle.getLow() > prevPrevCandle.getHigh() && todayCandle.getHigh() < prevCandle.getLow()
                    && todayCandle.getClose() < (prevPrevCandle.getOpen() + ((prevPrevCandle.getClose()-prevPrevCandle.getOpen())/2))){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isShootingStar(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().equals("HalloGreen")){
            double upperPart = todayCandle.getHigh()- todayCandle.getClose();
            double belowPart = todayCandle.getOpen() - todayCandle.getLow();
            if((upperPart > belowPart)
                    && (upperPart > (todayCandle.getHigh() - todayCandle.getLow()) /2)){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isTweezerTops(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        double diff = todayCandle.getOpen()<prevCandle.getClose()?prevCandle.getClose()-todayCandle.getOpen():
                todayCandle.getOpen()-prevCandle.getClose();
        if(prevCandle.getCandleType().equals("Hallow") && todayCandle.getCandleType().contains("SolidRed")){
            if (Double.compare(todayCandle.getOpen(), prevCandle.getClose())==0 && todayCandle.getClose()<=prevCandle.getOpen()
                    && todayCandle.getClose() >= (prevCandle.getClose()+((prevCandle.getOpen()-prevCandle.getClose())/2))){
                flag = true;
            }
        }

        return flag;
    }

    public static boolean isDojis(String stockName, List<String[]> historyData) {
        boolean flag = false;
        Map<String,Object> mp = CandleUtil.typeOfDojiCandle(stockName, historyData);
        return (boolean) mp.get("isDoji");
    }
    public static boolean isBearishRailwayTracks(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().equals("HallowGreen") && todayCandle.getCandleType().contains("Solid")){
            if ((int)prevCandle.getClose()==(int)todayCandle.getOpen() && prevCandle.getOpen()>todayCandle.getClose()
                && todayCandle.getClose() > prevCandle.getLow() && todayCandle.getLow() < prevCandle.getLow()) {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isMyFirstCandle(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (todayCandle.getCandleType().equals(CandleConstant.SOLID_RED) && prevCandle.getCandleType().equals(CandleConstant.HALLOW_GREEN)){
            if (todayCandle.getOpen() > prevCandle.getClose() && todayCandle.getOpen() > prevCandle.getOpen()
                    && todayCandle.getClose()> prevCandle.getLow()
                    && todayCandle.getClose()<=(prevCandle.getOpen()+((prevCandle.getClose()) - prevCandle.getOpen())/2)+1)
//                    || (todayCandle.getClose() < prevCandle.getOpen() && todayCandle.getLow() < prevCandle.getLow())))
                    {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isMySecondCandle(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (todayCandle.getCandleType().equals(CandleConstant.SOLID_RED) && prevCandle.getCandleType().equals(CandleConstant.SOLID_GREEN)){
            if (todayCandle.getOpen() > prevCandle.getClose() && todayCandle.getClose()<prevCandle.getOpen()){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isBearishReversal(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (todayCandle.getCandleType().equals(CandleConstant.SOLID_RED) && prevCandle.getCandleType().equals(CandleConstant.HALLOW_GREEN)){
            if (todayCandle.getOpen() > prevCandle.getClose() && todayCandle.getClose() < prevCandle.getOpen()){
                flag = true;
            }
        }
        return flag;
    }
}
