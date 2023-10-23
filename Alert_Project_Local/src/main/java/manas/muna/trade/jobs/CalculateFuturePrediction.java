package manas.muna.trade.jobs;

import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.StockAttrDetails;
import manas.muna.trade.vo.StockDetails;

import java.util.*;

public class CalculateFuturePrediction {
    public static void main(String[] args) {
        getAllCloseValue(new ArrayList<>());
    }

    public static List<StockAttrDetails> getAllCloseValue(List<StockDetails> listStock) {
        LinkedList<StockAttrDetails> list = new LinkedList<>();
        List<String> l = null;
        if (listStock.isEmpty()) {
            //"AUROPHARMA.NS","ALEMBICLTD.NS","DBL.NS",
            l = Arrays.asList("AYMSYNTEX.NS", "BUTTERFLY.NS", "ABBOTINDIA.NS", "DICIND.NS");
        }else{
            List<String> nm = new ArrayList<>();
            for (StockDetails sd : listStock)
                nm.add(sd.getStockName());
            l = nm;
        }
//        for (String stockName : StockUtil.loadAllStockNames()) {
        Date lastMonthFirstDate = StockUtil.getLastMonthFirstDayDate();
        for (String stockName : l) {
            List<String[]> data = StockUtil.loadStockData(stockName);
            String[] todaysData = data.get(0);
            for (int i=1; i<data.size()-1; i++){
                list.add(StockAttrDetails.builder()
                        .date(StockUtil.getDateFromString(data.get(i)[0], "yyyy-MM-dd"))
                        .price(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data.get(i)[4])))
                        .build());
                if (StockUtil.getDateFromString(todaysData[0],"yyyy-MM-dd").before(lastMonthFirstDate))
                    break;
            }
            System.out.println("check");
            Collections.sort(list, Comparator.comparingDouble(obj->obj.getPrice()));
            Collections.reverse(list);
            LinkedList<StockAttrDetails> stMovDt = new LinkedList<>();
            stMovDt.add(list.get(0));stMovDt.add(list.get(2));
            boolean isMarketUpTrend = isMarketUpTrend(stMovDt);
//            boolean isMarketDownTrend = isMarketDownTrend(stMovDt);
            double expectedPriceToday = calculateExpectedPrice(stMovDt, isMarketUpTrend);
            if (isMarketUpTrend){

            }else if (!isMarketUpTrend){
                if(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysData[4])) < expectedPriceToday){
                    if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysData[2])) >= expectedPriceToday){
                        System.out.println(stockName+" It may be reverse....");
                    }
                }else if(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysData[4])) > expectedPriceToday){

                }else
                    System.out.println(stockName+" It near to breakout wait 1 day.........");
            }
        }
        return list;
    }

    public static Map<String, Object> calculateTargetForStock(String stockName) {
        Map<String, Object> result = new HashMap<>();
        LinkedList<StockAttrDetails> list = new LinkedList<>();
        Date lastMonthFirstDate = StockUtil.getLastMonthFirstDayDate();

        List<String[]> data = StockUtil.loadStockData(stockName);
        String[] todaysData = data.get(0);
        for (int i=1; i<data.size()-1; i++){
            list.add(StockAttrDetails.builder()
                    .date(StockUtil.getDateFromString(data.get(i)[0], "yyyy-MM-dd"))
                    .price(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data.get(i)[4])))
                    .build());
            if (StockUtil.getDateFromString(data.get(i)[0],"yyyy-MM-dd").before(lastMonthFirstDate))
                break;
        }
        System.out.println("check");
        Collections.sort(list, Comparator.comparingDouble(obj->obj.getPrice()));
        Collections.reverse(list);
        LinkedList<StockAttrDetails> stMovDt = new LinkedList<>();
        stMovDt.add(list.get(0));stMovDt.add(list.get(2));
        boolean isMarketUpTrend = isMarketUpTrend(stMovDt);
        result.put("isMarketUpTrend", isMarketUpTrend);
//            boolean isMarketDownTrend = isMarketDownTrend(stMovDt);
        double expectedPriceToday = calculateExpectedPrice(stMovDt, isMarketUpTrend);
        result.put("expectedPriceToday", StockUtil.convertDoubleToTwoPrecision(expectedPriceToday));
        result.put("movCheckStockList", list);
        return result;
    }

    private static double calculateExpectedPrice(LinkedList<StockAttrDetails> list, boolean isMarketUpTrend) {
        long daysDiff = 0;
        double priceDiff = 0.0;
        Date targetDt = null;
        double targetPrice = 0.0;
        if (isMarketUpTrend){
            daysDiff = StockUtil.getDaysDiff(list.get(0).getDate(), list.get(1).getDate());
            priceDiff = list.get(0).getPrice() - list.get(1).getPrice();
            targetDt = list.get(0).getDate();
            targetPrice = list.get(0).getPrice();
        }else if (!isMarketUpTrend){
            daysDiff = StockUtil.getDaysDiff(list.get(1).getDate(), list.get(0).getDate());
            priceDiff = list.get(1).getPrice() - list.get(0).getPrice();
            targetDt = list.get(1).getDate();
            targetPrice = list.get(1).getPrice();
        }
        if (daysDiff<0)
            daysDiff = daysDiff * -1;
//        targetDt = StockUtil.addDaysToDate(targetDt, daysDiff, "yyyy-MM-dd");
//        targetPrice = targetPrice + priceDiff;
        Date today = new Date();//StockUtil.addDaysToDate(new Date(), -1 * daysDiff, "yyyy-MM-dd");
        while(targetDt.before(today)){
            targetDt = StockUtil.addDaysToDate(targetDt, daysDiff, "yyyy-MM-dd");
            if (!StockUtil.isWeekEnd(targetDt) && !StockUtil.isNSEHoliday(targetDt)){
                targetPrice = targetPrice + priceDiff;
            }
        }
        return targetPrice;
    }

    private static boolean isMarketUpTrend(LinkedList<StockAttrDetails> list) {
        boolean marketTrendUp = false;
        if (list.get(0).getDate().after(list.get(1).getDate())){
            marketTrendUp = true;
        }else if (list.get(0).getDate().before(list.get(1).getDate())){
            marketTrendUp = false;
        }
        return marketTrendUp;
    }
}
