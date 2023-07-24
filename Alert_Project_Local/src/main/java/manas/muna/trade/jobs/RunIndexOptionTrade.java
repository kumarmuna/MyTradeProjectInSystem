package manas.muna.trade.jobs;

import manas.muna.trade.util.SendMail;
import manas.muna.trade.util.StockUtil;

import java.util.*;

public class RunIndexOptionTrade {

    public static void main(String[] args){
        calculateOptionLogic();
    }
    public static void calculateOptionLogic() {
        Set<String> names = StockUtil.loadIndexStockNames();
        for (String stockName : names){
            List<String[]> datas = StockUtil.loadStockData(stockName);
            List<String[]> emaDatas = StockUtil.loadEmaData(stockName);
            Map<String, Boolean> emaIndicator = calculateEmaOptionData(emaDatas,stockName);
            Map<String, String> finalIndicator = calculateHistoryOptionData(datas, emaIndicator,stockName);
            verifyAndSenfNotification(finalIndicator);
        }
    }

    private static Map<String, Boolean> calculateEmaOptionData(List<String[]> emaDatas, String stockName) {
        System.out.println("Starting calculateEmaOptionData......");
//        Collections.reverse(emaDatas);
        boolean prevPosEma = false;
        boolean prevNegEma = false;
        int positiveMov = 0;
        int negativeMov = 0;
        int count = 0;
        String[] todaysEma = emaDatas.get(0);
        String[] yesdEma = emaDatas.get(1);
        if ((Double.parseDouble(todaysEma[0]) < Double.parseDouble(todaysEma[1]) &&
                (Double.parseDouble(yesdEma[0]) > Double.parseDouble(yesdEma[1])))){
            positiveMov++;
        }
        if((Double.parseDouble(todaysEma[0]) > Double.parseDouble(todaysEma[1]) &&
                (Double.parseDouble(yesdEma[0]) < Double.parseDouble(yesdEma[1])))) {
            negativeMov++;
        }
//        for (String[] emaData : emaDatas){
//            double ema30 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[0]));
//            double ema9 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[1]));
//            if(ema30 <= ema9) {
//                if (!prevPosEma && count!=0)
//                    positiveMov++;
//                prevPosEma=true;
//                count++;
//            }else{
//                if (!prevNegEma && count!=0)
//                    negativeMov++;
//                prevNegEma = true;
//                count++;
//            }
//        }
        Map<String,Boolean> emaIndicator = new HashMap<>();
        emaIndicator.put("positiveMov",(positiveMov > 0));
        emaIndicator.put("negativeMov",(negativeMov > 0));
        emaIndicator.put("optionTradeEligible", (positiveMov > 0 || negativeMov > 0));
        System.out.println("End calculateEmaOptionData......");
        return emaIndicator;
    }

    private static Map<String,String> calculateHistoryOptionData(List<String[]> datas, Map<String, Boolean> emaIndicator, String stockName) {
        System.out.println("Starting calculateHistoryOptionData......");
        Map<String,String> finalIndicator = new HashMap<>();
        if (emaIndicator.get("optionTradeEligible")) {
            String[] todaysData = datas.get(0);
            String[] yesterdaysData = datas.get(1);
            if (!todaysData[4].equals("null")) {
                if (yesterdaysData[4].equals("null")) {
                    for (int i = 2; i < datas.size() - 1; i++) {
                        if (!datas.get(i)[4].equals("null")) {
                            yesterdaysData = datas.get(i);
                            break;
                        }
                    }
                }
                double todayOpen = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysData[1]));
                double todayClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysData[4]));
                double todayPrice = todayOpen < todayClose ? todayClose : todayOpen;
                double yesterdayOpen = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesterdaysData[1]));
                double yesterdayClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesterdaysData[4]));
                double yesterdayPrice = yesterdayOpen < yesterdayClose ? yesterdayClose : yesterdayOpen;
                String msg = stockName + " is change direction check and place yout option trade";
                if (emaIndicator.get("negativeMov")) {
                    if (todayPrice < yesterdayPrice) {
                        String subject = "IndexOption PE "+stockName + " is eligible to option trade PE";
                        finalIndicator.put("eligibleToOptionTrade", "true");
                        finalIndicator.put("stockName", stockName);
                        finalIndicator.put("subject", subject);
                        finalIndicator.put("msg", msg);
                    }
                }
                if (emaIndicator.get("positiveMov")) {
                    if (todayPrice > yesterdayPrice) {
                        String subject = "IndexOption CE "+stockName + " is eligible to option trade CE";
                        finalIndicator.put("eligibleToOptionTrade", "true");
                        finalIndicator.put("stockName", stockName);
                        finalIndicator.put("subject", subject);
                        finalIndicator.put("msg", msg);
                    }
                }
            }
        }
        System.out.println("End calculateHistoryOptionData......");
        return finalIndicator;
    }

    private static void verifyAndSenfNotification(Map<String, String> notificationData) {
        if (Boolean.parseBoolean(notificationData.get("eligibleToOptionTrade"))){
            SendMail.sendMail(notificationData.get("msg"), notificationData.get("stockName"), notificationData.get("subject"));
        }
    }
}
