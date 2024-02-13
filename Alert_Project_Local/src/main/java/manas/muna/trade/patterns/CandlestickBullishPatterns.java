package manas.muna.trade.patterns;

import manas.muna.trade.constants.CandleConstant;
import manas.muna.trade.constants.CandleTypes;
import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandlestickBullishPatterns {

    /**
     * This pattern appears in a downtrend and is a combination of one dark candle followed by a larger hollow candle.
     * On the second day of the pattern, the price opens lower than the previous low,
     * yet buying pressure pushes the price up to a higher level than the previous high,
     * culminating in an obvious win for the buyers.
     * NOTE - it should have a high volume indicated by the volume bars
     * @param stockName
     * @return
     */
    public static boolean isBullishEngulfingOccurs(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1),stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2),stockEmaData.get(1));
        if (prevCandle.getCandleType().contains("Solid") && todayCandle.getCandleType().equals("HallowGreen")){
            if(todayCandle.getClose() > prevCandle.getOpen() && todayCandle.getOpen() <= prevCandle.getClose()
//                && prevCandle.getHigh() < todayCandle.getClose() && prevCandle.getLow() > todayCandle.getLow()
            ){
                //check if voulme also high
//                Map<String, String> volumeDataDetails = StockUtil.checkVolumeSize(stockName);
//                if (Boolean.parseBoolean(volumeDataDetails.get("isVolumeHigh")))
                    flag = true;
            }
        }

        return flag;
    }

    public static boolean isHammerInvertedHammer(String stockName) {
        boolean flag = false;

        return flag;
    }

    public static boolean isHammer(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().equals("SolidRed")){
            double upperPart = todayCandle.getOpen()<todayCandle.getClose()? (todayCandle.getHigh()- todayCandle.getClose()):(todayCandle.getHigh()- todayCandle.getOpen());
            double belowPart = todayCandle.getOpen()<todayCandle.getClose()? (todayCandle.getOpen() - todayCandle.getLow()):(todayCandle.getClose() - todayCandle.getLow());
            if ((upperPart < belowPart) && (belowPart > (todayCandle.getLow() + ((todayCandle.getHigh()-todayCandle.getLow())/2)))){
                flag = true;
            }
        }

        return flag;
    }

    public static boolean isInvertedHammer(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().equals("SolidRed")){
            double upperPart = todayCandle.getOpen()<todayCandle.getClose()? (todayCandle.getHigh()- todayCandle.getClose()):(todayCandle.getHigh()- todayCandle.getOpen());
            double belowPart = todayCandle.getOpen()<todayCandle.getClose()? (todayCandle.getOpen() - todayCandle.getLow()):(todayCandle.getClose() - todayCandle.getLow());
            if((upperPart > belowPart)
                    && (upperPart > (todayCandle.getLow() +((todayCandle.getHigh() - todayCandle.getLow()) /2)))){
                flag = true;
            }
        }

        return flag;
    }

    public static boolean isPiercingLine(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().contains("Solid") && todayCandle.getCandleType().equals("HallowGreen")){
            if (todayCandle.getOpen() < prevCandle.getClose() && todayCandle.getClose() < prevCandle.getHigh()
                    && todayCandle.getClose() > (prevCandle.getClose()+((prevCandle.getHigh()-prevCandle.getLow())/2))){
                flag = true;
            }
        }

        return flag;
    }

    public static boolean isMornigstar(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        CandleStick prevPrevCandle = CandleUtil.prepareCandleData(stockEmaData.get(3), stockEmaData.get(2));
        if (prevPrevCandle.getCandleType().contains("Solid") && todayCandle.getCandleType().equals("HallowGreen")){
            double mCloseValue = prevCandle.getOpen()< prevCandle.getClose()?prevCandle.getClose():prevCandle.getOpen();
            if (prevPrevCandle.getClose() > mCloseValue && todayCandle.getClose() > (prevPrevCandle.getClose()+((prevPrevCandle.getHigh()- prevPrevCandle.getLow())/2))){
                Map<String, String> volumeData = StockUtil.checkVolumeSize(stockName);
                if (Boolean.parseBoolean(volumeData.get("isVolumeHigh")))
                    flag = true;
            }
        }

        return flag;
    }

    public static boolean isThreeWhiteSoldiers(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        CandleStick prevPrevCandle = CandleUtil.prepareCandleData(stockEmaData.get(3), stockEmaData.get(2));
        if (todayCandle.getCandleType().equals("HallowGreen") && prevCandle.getCandleType().equals("HallowGreen")
                && prevPrevCandle.getCandleType().equals("HallowGreen")){
            if (todayCandle.getClose() > prevCandle.getClose() && prevCandle.getClose() > prevPrevCandle.getClose()
                    && todayCandle.getOpen()>prevCandle.getOpen() && todayCandle.getOpen()<=prevCandle.getClose()
                    && prevCandle.getOpen()>prevPrevCandle.getOpen() && prevCandle.getOpen()<=prevPrevCandle.getClose()){
                flag = true;
            }
        }

        return flag;
    }

    public static boolean isTweezerBottoms(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        double diff = todayCandle.getOpen()<prevCandle.getClose()?prevCandle.getClose()-todayCandle.getOpen():
                todayCandle.getOpen()-prevCandle.getClose();
        if(prevCandle.getCandleType().equals("SolidRed") && todayCandle.getCandleType().contains("Hallow")){
            if (Double.compare(todayCandle.getOpen(), prevCandle.getClose())==0 && todayCandle.getClose()<=prevCandle.getOpen()
                && todayCandle.getClose() >= (prevCandle.getClose()+((prevCandle.getOpen()-prevCandle.getClose())/2))){
                flag = true;
            }
        }

        return flag;
    }

    public static boolean isBullishHarami(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().contains(CandleConstant.SOLID_RED)){
            if (todayCandle.getHigh() < prevCandle.getOpen() && todayCandle.getLow() > prevCandle.getClose()){
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

    public static boolean isMyFirstCandle(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().equals("Solid") && todayCandle.getCandleType().equals(CandleConstant.HALLOW_GREEN)){
            if (todayCandle.getOpen() >= prevCandle.getClose() && todayCandle.getOpen()<=prevCandle.getOpen()
//                    && (todayCandle.getClose()> prevCandle.getHigh() && todayCandle.getClose() >= ((prevCandle.getOpen()-prevCandle.getClose())/2)
                    && (todayCandle.getClose() > prevCandle.getHigh())
            ){
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isMySecondCandle(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        //need to add
        return flag;
    }

    public static boolean isBulishRailwayTracks(String stockName, List<String[]> stockEmaData) {
        boolean flag = false;
        CandleStick todayCandle = CandleUtil.prepareCandleData(stockEmaData.get(1), stockEmaData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(stockEmaData.get(2), stockEmaData.get(1));
        if (prevCandle.getCandleType().contains("Solid") && todayCandle.getCandleType().equals("HallowGreen")) {
            if ((int)prevCandle.getClose()==(int)todayCandle.getOpen() && (int)prevCandle.getLow()==(int)todayCandle.getLow()
                    && prevCandle.getOpen()<todayCandle.getClose()){
                flag = true;
            }
        }
        return flag;
    }
}
