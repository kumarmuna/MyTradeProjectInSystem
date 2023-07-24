package manas.muna.trade.jobs;

import manas.muna.trade.util.SendMail;
import manas.muna.trade.util.StockPropertiesUtil;
import manas.muna.trade.util.StockUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.time.DayOfWeek.THURSDAY;
import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static java.time.temporal.TemporalAdjusters.lastInMonth;

public class RunOptionTrade {
    public static List<String> optionStocks = new ArrayList<>();
    public static void main(String[] args){
        calculateOptionLogic();
    }
    public static void calculateOptionLogic() {
        Set<String> names = StockUtil.loadAllStockNames();
        for (String stockName : names){
            List<String[]> datas = StockUtil.loadStockData(stockName);
            List<String[]> emaDatas = StockUtil.loadEmaData(stockName);
            Map<String, Boolean> emaIndicator = calculateEmaOptionData(emaDatas,stockName);
            Map<String, String> finalIndicator = calculateHistoryOptionData(datas, emaIndicator,stockName);
//            Map<String, String> finalIndicator = checkStockOptionTradeStatus(historyDataIndicator);
            verifyAndSenfNotification(finalIndicator);
        }
    }

    public static void calculateOptionLogicForStock(String stockName) {
        if(StockPropertiesUtil.getOptionStockSymbol().contains(stockName)) {
            if(stockName.indexOf('.') == -1)
                stockName = stockName+".NS";
            List<String[]> datas = StockUtil.loadStockData(stockName);
            List<String[]> emaDatas = StockUtil.loadEmaData(stockName);
            Map<String, Boolean> emaIndicator = calculateEmaOptionData(emaDatas, stockName);
            Map<String, String> finalIndicator = calculateHistoryOptionData(datas, emaIndicator, stockName);
            verifyAndSenfNotification(finalIndicator);
        }else{
            System.out.println(stockName +" Stock is positive but this is not a option stock");
        }
    }

    private static Map<String, Boolean> calculateEmaOptionData(List<String[]> emaDatas, String stockName) {
        System.out.println("Starting calculateEmaOptionData......");
        Collections.reverse(emaDatas);
        boolean prevPosEma = false;
        boolean prevNegEma = false;
        int positiveMov = 0;
        int negativeMov = 0;
        int count = 0;
        for (String[] emaData : emaDatas){
            double ema30 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[0]));
            double ema9 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[1]));
            if(ema30 <= ema9) {
                if (!prevPosEma && count!=0)
                    positiveMov++;
                prevPosEma=true;
                count++;
            }else{
                if (!prevNegEma && count!=0)
                    negativeMov++;
                prevNegEma = true;
                count++;
            }
        }
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
                    if ((todayPrice < yesterdayPrice) && checkIfNearToExpiryDate(stockName, "negativeMov")) {
                        String subject = "Option PE "+stockName + " is eligible to option trade PE";
                        finalIndicator.put("eligibleToOptionTrade", "true");
                        finalIndicator.put("stockName", stockName);
                        finalIndicator.put("subject", subject);
                        finalIndicator.put("msg", msg);
                    }
                }
                if (emaIndicator.get("positiveMov")) {
                    if ((todayPrice > yesterdayPrice) && checkIfNearToExpiryDate(stockName,"positiveMov")) {
                        String subject = "Option CE "+stockName + " is eligible to option trade CE";
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
    private static Map<String, String> checkStockOptionTradeStatus(Map<String, String> historyDataIndicator) {
        boolean flag = false;

        return null;
    }
    public static boolean isLastMonthSupport(String positiveMov, String stockName, String thursday) {
        boolean flag = false;
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate now = LocalDate.now();
            long lastMonEndDate = 0;
            long secLastMonStartDate = 0;
            if (thursday.equals("lastthursday")){
                lastMonEndDate = getDateTimeIn24Hrs(now.with(TemporalAdjusters.lastDayOfMonth()).format(format));
                secLastMonStartDate = getDateTimeIn24Hrs(now.with(TemporalAdjusters.firstDayOfMonth()).format(format));
            }else if(thursday.equals("firstthursday")){
                lastMonEndDate = getDateTimeIn24Hrs(now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(format));
                secLastMonStartDate = getDateTimeIn24Hrs(now.minusMonths(2).with(TemporalAdjusters.firstDayOfMonth()).format(format));
            }
            StockUtil.loadStockHistoryData(stockName,secLastMonStartDate,lastMonEndDate);
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\temp\\"+stockName+".csv");
            List<String[]> stockResult = StockUtil.loadStockDataUsingPath(path.toString());
            if (positiveMov.equals("positiveMov") &&
                    (Double.parseDouble(stockResult.get(0)[4]) > Double.parseDouble(stockResult.get(1)[4])))
                flag = true;
            if (positiveMov.equals("negativeMov") &&
                    (Double.parseDouble(stockResult.get(0)[4]) < Double.parseDouble(stockResult.get(1)[4])))
                flag = true;

        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    private static long getDateTimeIn24Hrs(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        long time = 0;
        try{
            Date dt = sdf.parse(date);
            String dateTimeIn24Hrs = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dt);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime dtt = LocalDateTime.parse(dateTimeIn24Hrs, formatter);
            time =  dtt.toEpochSecond(ZoneOffset.UTC);
        }catch (Exception e){
            e.printStackTrace();
        }
        return time;
    }

    public static boolean checkIfNearToExpiryDate(String stockName, String movement) {
        boolean flag = false;
        try {
            //get current months last Thursday
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int year = Calendar.getInstance().get(Calendar.YEAR);
            LocalDate lastThursday = LocalDate.of(year, month, 1).with(lastInMonth(THURSDAY));
            LocalDate firstThursday = LocalDate.of(year, month, 1).with(firstInMonth(THURSDAY));
            Date lt = Date.from(lastThursday.atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            Date ft = Date.from(firstThursday.atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
            Date today = new Date();
            System.out.println("FirstThursday = " + ft);
            System.out.println("LastThursday = " + lt);
            System.out.println("Today = " + today);
            long ltDateDiff = today.getTime() - lt.getTime();
            long ftDateDiff = today.getTime() - ft.getTime();
            if (dateDaysDiff(ftDateDiff) <= 3) {
                flag = isLastMonthSupport(movement, stockName, "firstthursday");
            }else if(dateDaysDiff(ltDateDiff) <= 3){
                flag = isLastMonthSupport(movement, stockName, "lastthursday");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    private static long dateDaysDiff(long dateDiff) {
        long daysDiff = 0;
        if (dateDiff < 0) {
            daysDiff = TimeUnit.MILLISECONDS
                    .toDays(-1 * dateDiff)
                    % 365;
        } else {
            daysDiff = TimeUnit.MILLISECONDS
                    .toDays(dateDiff)
                    % 365;
        }
        return daysDiff;
    }

    private static void verifyAndSenfNotification(Map<String, String> notificationData) {
        if (Boolean.parseBoolean(notificationData.get("eligibleToOptionTrade"))){
            SendMail.sendMail(notificationData.get("msg"), notificationData.get("stockName"), notificationData.get("subject"));
        }
    }
}
