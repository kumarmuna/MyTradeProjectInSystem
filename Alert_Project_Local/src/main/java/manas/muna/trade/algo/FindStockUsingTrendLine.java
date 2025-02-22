package manas.muna.trade.algo;

import manas.muna.trade.patterns.StocksPatternToConfirmTrade;
import manas.muna.trade.repository.StockHighLowFeigenClient;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FindStockUsingTrendLine {

    public static Map<String, Double> checkTrendLine(String stockName, List<String[]> historyDataList, String direction) {
        Map<String, Double> trendMapDetails = new HashMap<>();
        try {
            String expctDirection = direction.equalsIgnoreCase("Not DOWN") ? "UP" : direction.equalsIgnoreCase("Not UP") ? "DOWN" : "";
            if (historyDataList == null) {
                historyDataList = StockUtil.loadStockData(stockName);
                historyDataList = historyDataList.subList(0, historyDataList.size() - 1);
            }
            Map<String, Map<String, Double>> data = StockHighLowFeigenClient.getHighLowData(stockName);
            if (expctDirection.equalsIgnoreCase("DOWN")) {
                Map<String, Double> highData = data.get("highData");
                Map<String, Double> highData3Months = data.get("highData3Months");
                String highKey = highData.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                String secondHighKey = highData.entrySet().stream().filter(a -> !a.getKey().equalsIgnoreCase(highKey)).max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                Date date = DateUtil.convertStrToDate(highKey, "yyyy-MM-dd");
                Date highKey_startDate = DateUtil.getMonthFirstDate(date);
                Date highKey_endDate = DateUtil.getMonthLastDate(date);
                Date date1 = DateUtil.convertStrToDate(secondHighKey, "yyyy-MM-dd");
                String last3MonthHighKey = highData3Months.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                String last3MonthSecondHighKey = highData3Months.entrySet().stream().filter(a -> !a.getKey().equalsIgnoreCase(last3MonthHighKey)).max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                Date last3MonthHighKeyDate = DateUtil.convertStrToDate(last3MonthHighKey, "yyyy-MM-dd");
                Date last3MonthHighKey_startDate = DateUtil.getMonthFirstDate(last3MonthHighKeyDate);
                Date last3MonthHighKey_endDate = DateUtil.getMonthLastDate(last3MonthHighKeyDate);
                double last3MonthHigh = Double.MIN_VALUE;
                double last3MonthSecondHigh = 0;
                String last3MonthHighDate = "";
                String last3MonthSecondHighDate = "";
                double monthHighClose = Double.MIN_VALUE;
                double monthHigh = Double.MIN_VALUE;
                double monthSecondHigh = 0;
                double monthSecondHighClose = 0;
                String monthHighDate = null, monthSecondHighDate = null;
                String monthHighCloseDate = null, monthSecondHigCloseDate = null;

                for (String[] historyData : historyDataList) {
                    Date stDate = DateUtil.convertStrToDate(historyData[0], "yyyy-MM-dd");
                    if ((DateUtil.isBothDateSame(stDate, last3MonthHighKey_startDate) || stDate.after(last3MonthHighKey_startDate))
                            && (DateUtil.isBothDateSame(stDate, last3MonthHighKey_endDate) || last3MonthHighKey_endDate.after(stDate))) {
                        if (last3MonthHigh < Double.parseDouble(historyData[2])) {
                            last3MonthSecondHigh = last3MonthHigh;
                            last3MonthHigh = Double.parseDouble(historyData[2]);
                            last3MonthSecondHighDate = last3MonthHighDate;
                            last3MonthHighDate = historyData[0];
                        }
                    }
                    if ((stDate.after(highKey_startDate) || DateUtil.isBothDateSame(stDate, highKey_startDate))
                            && (highKey_endDate.after(stDate) || DateUtil.isBothDateSame(stDate, highKey_endDate))) {
                        if (monthHigh < Double.parseDouble(historyData[2])) {
                            monthSecondHigh = monthHigh;
                            monthHigh = Double.parseDouble(historyData[2]);
                            monthSecondHighDate = monthHighDate;
                            monthHighDate = historyData[0];
                        }
                        if (monthHighClose < Double.parseDouble(historyData[4])) {
                            monthSecondHighClose = monthHighClose;
                            monthHighClose = Double.parseDouble(historyData[4]);
                            monthSecondHigCloseDate = monthHighCloseDate;
                            monthHighCloseDate = historyData[0];
                        }
                    }
                }
                double target = calculateTrendLine(highData.get(highKey), monthHighDate, highData.get(secondHighKey), monthSecondHighDate, "yyyy-MM-dd", historyDataList);
//                System.out.println("Year High Target=" + target);
                trendMapDetails.put("YearHighTarget", target);
                double closetarget = calculateTrendLine(monthHighClose, monthHighCloseDate, monthSecondHighClose, monthSecondHigCloseDate, "yyyy-MM-dd", historyDataList);
//                System.out.println("Year close Target=" + target);
                trendMapDetails.put("YearCloseTarget", target);
                double last3MonthTrendLinetarget = calculateTrendLine(last3MonthHigh, last3MonthHighDate, highData3Months.get(last3MonthSecondHighKey), last3MonthSecondHighKey, "yyyy-MM-dd", historyDataList);
//                System.out.println("Last 3Months TrendLine High Target=" + last3MonthTrendLinetarget);
                trendMapDetails.put("Last3MonHighTrendTarget", target);
                double last3MonthHightarget = calculateTrendLine(last3MonthHigh, last3MonthHighDate, last3MonthSecondHigh, last3MonthSecondHighDate, "yyyy-MM-dd", historyDataList);
//                System.out.println("Last 3Months High Target=" + last3MonthHightarget);
                trendMapDetails.put("Last3MonHighTarget", target);

            } else if (expctDirection.equalsIgnoreCase("UP")) {
                Map<String, Double> lowData = data.get("lowData");
                String lowKey = lowData.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                String secondLowKey = lowData.entrySet().stream().filter(a -> !a.getKey().equalsIgnoreCase(lowKey)).min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                Date date = DateUtil.convertStrToDate(lowKey, "yyyy-MM-dd");
                Date lowKey_startDate = DateUtil.getMonthFirstDate(date);
                Date lowKey_endDate = DateUtil.getMonthLastDate(date);
                Map<String, Double> lowData3Months = data.get("lowData3Months");
                String last3MonthLowKey = lowData3Months.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                String last3MonthSecondLowKey = lowData3Months.entrySet().stream().filter(a -> !a.getKey().equalsIgnoreCase(last3MonthLowKey)).max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
                Date last3MonthLowKeyDate = DateUtil.convertStrToDate(last3MonthLowKey, "yyyy-MM-dd");
                Date last3MonthLowKey_startDate = DateUtil.getMonthFirstDate(last3MonthLowKeyDate);
                Date last3MonthLowKey_endDate = DateUtil.getMonthLastDate(last3MonthLowKeyDate);
                double last3MonthLow = Double.MIN_VALUE;
                double last3MonthSecondLow = 0;
                String last3MonthLowDate = "";
                String last3MonthSecondLowDate = "";
                double monthLowOpen = Double.MAX_VALUE;
                double monthLow = Double.MAX_VALUE;
                double monthSecondLow = 0;
                double monthSecondLowOpen = 0;
                String monthLowDate = null, monthSecondLowDate = "";
                String monthLowOpenDate = null, monthSecondLowOpenDate = "";

                for (String[] historyData : historyDataList) {
                    Date stDate = DateUtil.convertStrToDate(historyData[0], "yyyy-MM-dd");
                    if ((DateUtil.isBothDateSame(stDate, last3MonthLowKey_startDate) || stDate.after(last3MonthLowKey_startDate))
                            && (DateUtil.isBothDateSame(stDate, last3MonthLowKey_endDate) || last3MonthLowKey_endDate.after(stDate))) {
                        if (last3MonthLow < Double.parseDouble(historyData[3])) {
                            last3MonthSecondLow = last3MonthLow;
                            last3MonthLow = Double.parseDouble(historyData[3]);
                            last3MonthSecondLowDate = last3MonthLowDate;
                            last3MonthLowDate = historyData[0];
                        }
                    }
                    if (stDate.after(lowKey_startDate) && lowKey_endDate.after(stDate)) {
                        if (monthLowOpen > Double.parseDouble(historyData[1])) {
                            monthSecondLowOpen = monthLowOpen;
                            monthLowOpen = Double.parseDouble(historyData[1]);
                            monthSecondLowOpenDate = monthLowOpenDate;
                            monthLowOpenDate = historyData[0];
                        }
                        if (monthLow > Double.parseDouble(historyData[3])) {
                            monthSecondLow = monthLow;
                            monthLow = Double.parseDouble(historyData[3]);
                            monthSecondLowDate = monthLowDate;
                            monthLowDate = historyData[0];
                        }
                    }
                }
                double lowTarget = calculateTrendLine(lowData.get(lowKey), monthLowDate, lowData.get(secondLowKey), monthSecondLowDate, "yyyy-MM-dd", historyDataList);
//                System.out.println("Year Low Target=" + lowTarget);
                trendMapDetails.put("YearLowTarget", lowTarget);
                double openTarget = calculateTrendLine(monthLowOpen, monthLowOpenDate, monthSecondLowOpen, monthSecondLowOpenDate, "yyyy-MM-dd", historyDataList);
//                System.out.println("Year Open Target=" + lowTarget);
                trendMapDetails.put("YearOpenTarget", lowTarget);
                double last3MonthLowTrenLineTarget = calculateTrendLine(last3MonthLow, last3MonthLowDate, lowData.get(last3MonthSecondLowKey), last3MonthSecondLowKey, "yyyy-MM-dd", historyDataList);
//                System.out.println("Last 3Months Trendline Low Target=" + last3MonthLowTrenLineTarget);
                trendMapDetails.put("Last3MonLowTrendTarget", last3MonthLowTrenLineTarget);
                double last3MonthsLowTarget = calculateTrendLine(last3MonthLow, last3MonthLowDate, last3MonthSecondLow, last3MonthSecondLowDate, "yyyy-MM-dd", historyDataList);
//                System.out.println("Last 3Months Low Target=" + last3MonthsLowTarget);
                trendMapDetails.put("Last3MonLowTarget", last3MonthsLowTarget);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return trendMapDetails;
    }

    private static double calculateTrendLine(double value1, String date1, double value2, String date2, String format, List<String[]> historyData) {
        double target = 0;
        if (StringUtils.isEmpty(date1) || StringUtils.isEmpty(date2))
            return 0.0;
        if (DateUtil.convertStrToDate(date1, format).before(DateUtil.convertStrToDate(date2, format))) {
            int days = StockUtil.readDaysBetweenTwoDaysFromHistoryData(date1, date2, format, historyData);
            int targetDate = StockUtil.readDaysBetweenTwoDaysFromHistoryData(date2, DateUtil.getTodayDate(format), format, historyData);
            if (value1 < value2) {
                double movePerday = (value2 - value1) / days;
                target = value2 + (targetDate * movePerday);
            }else if(value2 < value1){
                double movePerday = (value1 - value2) / days;
                target = value2 - (targetDate * movePerday);
            }
        }else if(DateUtil.convertStrToDate(date1, format).after(DateUtil.convertStrToDate(date2, format))){
            int days = StockUtil.readDaysBetweenTwoDaysFromHistoryData(date2, date1, format, historyData);
            int targetDate = DateUtil.getDateDiffBetweenTwoDate(date1, format, DateUtil.getTodayDate(format), format);
            if (value1 < value2) {
                double movePerday = (value2 - value1) / days;
                target = value1 - (targetDate * movePerday);
            }else if(value2 < value1){
                double movePerday = (value1 - value2) / days;
                target = value1 + (targetDate * movePerday);
            }
        }
        return target;
    }

    public static void matchMarketMoveWithTrendLine() {
//        String dateValue = "2025_02_14";
        String dateValue = DateUtil.getTodayDate();
        List<String> trendStocks = new ArrayList<>();//validateTopTrendStockWithTrendLine();
        List<String[]> dt = StockUtil.readFileData("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\high_low_stocks\\"+dateValue+"_top_in_trend_stocks_option");
        trendStocks.addAll(dt.stream().map(a->a[0].split("Name:")[1].trim()+" - "+a[1].split("expctMrkDirction:")[1].trim()).collect(Collectors.toList()));
        List<String[]> dt1 = StockUtil.readFileData("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\high_low_stocks\\"+dateValue+"_top_in_trend_stocks");
        trendStocks.addAll(dt1.stream().map(a->a[0].split("Name:")[1].trim()+" - "+a[1].split("expctMrkDirction:")[1].trim()).collect(Collectors.toList()));

        //        StockUtil.storeFile(finalStockPath, trendStocks.stream().map(a->a.toString()).collect(Collectors.joining("\n")), true);
//
//        List<String> gapUpStocks = checkIfAnyGapUp();
//        StockUtil.storeFile(finalStockPath, gapUpStocks.stream().map(a->a.toString()).collect(Collectors.joining("\n","\n","\n")), true);
        for (String stock : trendStocks){
            String[] stockData = stock.split("-");
//            String marketMove = StockUtil.calculateMarketMove(stockData[0].trim());
            String marketMove = StocksPatternToConfirmTrade.updateMarketTrendFromDB(stockData[0].trim());
            if (marketMove.isEmpty())
                marketMove = StockUtil.calculateMarketMove(stockData[0].trim());
            String tmp = stockData[1].trim().equalsIgnoreCase("Not UP")?"DOWN"
                    :stockData[1].trim().equalsIgnoreCase("Not DOWN")?"UP":"";
            if (tmp.equals(marketMove))
                System.out.println(stock);
        }
    }

    public static void main(String[] args) {
//        matchMarketMoveWithTrendLine();
//        checkTrendLine("BOSCHLTD.NS", null,"UP");
        String marketMove = StockUtil.calculateMarketMove("DMART.NS");
        System.out.println(marketMove);
    }
}
