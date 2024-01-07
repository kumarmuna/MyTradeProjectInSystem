package manas.muna.trade.patterns;

import manas.muna.trade.constants.CandleConstant;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;

import java.util.List;

public class CandlestickBearishPatterns {

    public static boolean isBearishAbandonedBaby(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1),stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2),stockEmaData.get(1));
        CandleStick prevPrevCandle = CandleUtil.prepareCandleData(stockEmaData.get(3),stockEmaData.get(2));
        if (prevPrevCandle.getCandleType().contains("HallowGreen") && todayCandle.getCandleType().contains("Solid")){
            if (prevCandle.getOpen() > prevPrevCandle.getClose() && prevCandle.getClose() > prevPrevCandle.getClose()
                && todayCandle.getOpen() < prevCandle.getOpen() && todayCandle.getOpen() < prevCandle.getClose()
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
        if(prevCandle.getCandleType().equals("HallowGreen") && todayCandle.getCandleType().contains("Solid")){
            if (todayCandle.getOpen() >= prevCandle.getClose() && todayCandle.getClose() < prevCandle.getOpen()
                && prevCandle.getHigh() < todayCandle.getHigh() && prevCandle.getLow()>todayCandle.getClose()){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isHaramiBearish(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if(prevCandle.getCandleType().equals("HallowGreen") && todayCandle.getCandleType().contains("Solid")){
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

    public static boolean isDojis(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        double diff = todayCandle.getClose()-todayCandle.getOpen();
        double upParts = todayCandle.getOpen() < todayCandle.getClose()?todayCandle.getHigh()-todayCandle.getClose(): todayCandle.getHigh()-todayCandle.getOpen();
        double downParts = todayCandle.getOpen() < todayCandle.getClose()?todayCandle.getOpen()-todayCandle.getLow():todayCandle.getClose()-todayCandle.getLow();
        if (diff<0)
            diff = diff*-1;
        if(upParts > diff && downParts > diff){
            if (diff < 5)
                flag = true;
            else if(StockUtil.calculatePercantage(diff, todayCandle.getLow()) <= 0.3) {
                flag = true;
            }
        }
        if(todayCandle.getOpen()== todayCandle.getClose())
            flag = true;
        return flag;
    }

    public static boolean isBearishRailwayTracks(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().equals("HallowGreen") && todayCandle.getCandleType().contains("Solid")){
            if ((int)prevCandle.getClose()==(int)todayCandle.getOpen() && prevCandle.getOpen()>todayCandle.getClose()
                && todayCandle.getClose() > prevCandle.getLow()) {
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
            if (todayCandle.getOpen() <= prevCandle.getClose() && (todayCandle.getClose()< prevCandle.getHigh()
                    || (todayCandle.getClose() < prevCandle.getOpen() && todayCandle.getLow() < prevCandle.getLow()))){
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
