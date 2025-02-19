package manas.muna.trade.jobs;

import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.Stock;
import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.stream.Collectors;

public class PrepareReportJob {

    public static void prepareReport(String stockName, List<String[]> historyData, int reportDays) {
        System.out.println(historyData.size()-1+","+reportDays);
        try {
            if (historyData.size() - 1 < reportDays)
                reportDays = historyData.size() - 2;
            historyData = historyData.subList(0, reportDays + 1);
            Collections.reverse(historyData);
            List<String[]> reportDataList = new ArrayList<>();
            List<String[]> reportSMADataList = new ArrayList<>();
            List<String[]> stockReportData = getReportData(stockName);
//        List<String[]> stockSMAData = getSMAReportData(stockName);
//        List<String[]> stockReportData = new ArrayList<>();
            if (stockReportData.size() == 0)
                stockReportData.add(new String[]{"NAME", "DATE", "OPEN", "CLOSE", "VOLUME", "PROFIT", "AVGPROFIT", "LOSS", "AVGLOSS", "RSI","MOM","MFI"});
//            stockReportData.add(new String[]{"NAME", "DATE", "OPEN", "CLOSE", "VOLUME", "PROFIT", "AVGPROFIT", "LOSS", "AVGLOSS", "RSI","MOM"});
//        if (stockSMAData.size() == 0)
//            stockSMAData.add(new String[]{"NAME", "DATE", "DAILY-SMA","WK-SMA"});
            for (int i = 1; i <= reportDays; i++) {
                String[] reportData = new String[12]; //name,date,open,close,vol,profit,avg-profit,loss,avg-loss,rsi
//            String[] reportSMA = new String[4];//name,date,sma
                String[] todayData = historyData.get(i);
                String[] yesterdayData = historyData.get(i - 1);
                reportData[0] = stockName;
//            reportSMA[0] = stockName;
                reportData[1] = todayData[0];
//            reportSMA[1] = todayData[0];
                reportData[2] = getDoubleToString(todayData[1]);
                reportData[3] = getDoubleToString(todayData[4]);
                reportData[4] = todayData[6];
                reportData[5] = getDoubleToString(Double.parseDouble(todayData[4]) - Double.parseDouble(yesterdayData[4]));
                double avgGain = calculateAvg(stockName, "profit", 14, reportDataList, reportData);//[(prev avg . gain)*13)+ currnt . gain)]/14
                reportData[6] = String.valueOf(avgGain);
                reportData[7] = getDoubleToString(Double.parseDouble(yesterdayData[4]) - Double.parseDouble(todayData[4]));
                double avgLoss = calculateAvg(stockName, "loss", 14, reportDataList, reportData);//[(prev avg . loss)*13)+ currnt . loss)]/14
                reportData[8] = String.valueOf(avgLoss);
                reportData[9] = String.valueOf(calculateRSI(stockName, avgGain, avgLoss));
                reportData[10] = calculateMOM(stockName, historyData, i);
                reportData[11] = calculateMFI(historyData.get(i));
//            reportSMA[2] = String.valueOf(calculateSMA(stockName,8,"DAILY"));
//            reportSMA[3] = String.valueOf(calculateSMA(stockName,8,"WK"));
                reportDataList.add(reportData);
//            reportSMADataList.add(reportSMA);
            }
            stockReportData.addAll(reportDataList);
            stockReportData.set(0,new String[]{"NAME", "DATE", "OPEN", "CLOSE", "VOLUME", "PROFIT", "AVGPROFIT", "LOSS", "AVGLOSS", "RSI","MOM","MFI"});
//        stockSMAData.addAll(reportSMADataList);

            storeReportData(stockName, stockReportData);
//        storeSMAReportData(stockName, stockSMAData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String calculateMFI(String[] todayHist) {
        double mfi = 0.0;
        double typicalPrice = (Double.parseDouble(todayHist[2]) + Double.parseDouble(todayHist[3]) + Double.parseDouble(todayHist[4])) / 3 ;
        mfi = (int)typicalPrice * (todayHist[6]==null? 0 : Long.parseLong(todayHist[6]));
        return String.valueOf(StockUtil.convertDoubleToTwoPrecision(mfi));
    }

    private static String calculateMOM(String name, List<String[]> historyData, int i) {
        double dt = 0.0;
        if (i < 6 && historyData!=null)
            return "0.0";
        if (historyData==null) {
            historyData = StockUtil.loadStockData(name);
            dt = Double.parseDouble(historyData.get(i)[4]) - Double.parseDouble(historyData.get(i+6)[4]);
        }else
            dt = Double.parseDouble(historyData.get(i)[4]) - Double.parseDouble(historyData.get(i-6)[4]);
        return String.valueOf(StockUtil.convertDoubleToTwoPrecision(dt));
    }

    public static String calculateMOM(String name, int i){
        return calculateMOM(name, null, i);
    }

    private static double calculateSMA(String stockName, int day, String period) {
        double sum = 0.0;
        double sma = 0.0;
        List<String[]> data = null;
        try {
            if (period.equalsIgnoreCase("DAILY")) {
                data = StockUtil.loadStockData(stockName);
            }
            if (period.equalsIgnoreCase("WK")) {
                data = StockUtil.loadStockData(stockName, "WK");
            }
            for (int i = 0; i < day; i++) {
                sum += StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data.get(i)[3]));
            }
            sma = sum/day;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return sma;
    }

    private static void storeReportData(String stockName, List<String[]> stockReportData) {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024\\"+stockName;
        StockUtil.storeFile(fileLocation, stockReportData);
    }

    private static void storeSMAReportData(String stockName, List<String[]> stockReportData) {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024\\SMA\\"+stockName;
        StockUtil.storeFile(fileLocation, stockReportData);
    }

    private static double calculateAvg(String stockName, String avgInd, int days, List<String[]> preparingReportList,String[] currentData) {
        List<String[]> reportData = getReportData(stockName);
        Collections.reverse(reportData);
        double total = 0.0;
        if (avgInd.equals("profit")){
            if (reportData.size()==0 && preparingReportList.size() ==14) {
                return calculateAvgFromList(preparingReportList.stream().map(e->Double.parseDouble(e[5])).collect(Collectors.toList()));
            }else if(reportData.size()==0 && preparingReportList.size() > 14) {
                String[] data = preparingReportList.get(preparingReportList.size()-1);//[(Past Average Gain) x 13 + Current Gain] ÷ 14
                return StockUtil.convertDoubleToTwoPrecision(((Double.parseDouble(data[6]) * 13) + Double.parseDouble(currentData[5])) /14);
            }else if (reportData.size()<=0)
                return 0.0;
            else {
                List<String[]> profCal = new ArrayList<>();
                if (reportData.size() > 14)
                    profCal = reportData.subList(0, 14);
                else
                    profCal = reportData;
//                return calculateAvgFromList(profCal.stream().map(e->Double.parseDouble(e[5])).collect(Collectors.toList()));
                String[] prevReportRecord = reportData.get(0);//[(Past Average Gain) x 13 + Current Gain] ÷ 14
                return StockUtil.convertDoubleToTwoPrecision(((Double.parseDouble(prevReportRecord[6]) * 13) + Double.parseDouble(currentData[5])) /14);
            }
        }else if(avgInd.equals("loss")){
            if (reportData.size()==0 && preparingReportList.size() ==14) {
                return calculateAvgFromList(preparingReportList.stream().map(e->Double.parseDouble(e[7])).collect(Collectors.toList()));
            }else if(reportData.size()==0 && preparingReportList.size() > 14) {
                String[] data = preparingReportList.get(preparingReportList.size()-1);//[(Past Average Gain) x 13 + Current Gain] ÷ 14
                return StockUtil.convertDoubleToTwoPrecision(((Double.parseDouble(data[8]) * 13) + Double.parseDouble(currentData[7])) /14);
            }else if (reportData.size()<=0)
                return 0.0;
            else {
                List<String[]> profCal = new ArrayList<>();
                if (reportData.size()>14)
                    profCal = reportData.subList(0, 14);
                else
                    profCal = reportData;
//                return calculateAvgFromList(profCal.stream().map(e->Double.parseDouble(e[7])).collect(Collectors.toList()));
                String[] prevReportRecord = reportData.get(0);//[(Past Average Loss) x 13 + Current Loss] ÷ 14
                return StockUtil.convertDoubleToTwoPrecision(((Double.parseDouble(prevReportRecord[8]) * 13) + Double.parseDouble(currentData[7])) /14);
            }
        }
        return total/14;
    }

    private static double calculateAvgFromList(List<Double> list) {
        double total = list.stream().reduce(0.0, Double::sum);
        return StockUtil.convertDoubleToTwoPrecision(total/14);
    }
    private static double calculateRSI(String stockName, double avgGain, double avgLoss) {
        List<String[]> reportData = null;
        double RS = 0.0;
        if (avgGain==0.0) {
            reportData = getReportData(stockName);
            Collections.reverse(reportData);
            if (reportData.size()==0)
                return RS;
            String[] latestReport = reportData.get(0);
            RS = Double.parseDouble(latestReport[6]) ==0.0?0.0 :  (Double.parseDouble(latestReport[6])/ Double.parseDouble(latestReport[8]));
        }else
            RS = avgGain/avgLoss;
        return StockUtil.convertDoubleToTwoPrecision(100 - (100/(1+ RS)));

    }

    private static List<String[]> getReportData(String stockName){
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024\\"+stockName;
        List<String[]> stockReportData = StockUtil.readFileData(fileLocation);
//        Collections.reverse(stockReportData);
        return stockReportData;
    }

    private static List<String[]> getSMAReportData(String stockName){
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024\\SMA\\"+stockName;
        List<String[]> stockReportData = new ArrayList<>();
        stockReportData = StockUtil.readFileData(fileLocation);
//        Collections.reverse(stockReportData);
        return stockReportData;
    }

    private static String getDoubleToString(String data) {
        return String.valueOf(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data)));
    }

    private static String getDoubleToString(double data) {
        return String.valueOf(StockUtil.convertDoubleToTwoPrecision(data < 0 ? 0 : data));
    }
    public static void main(String[] args){
//        List<String> nm = List.of("^NSEBANK");
        String[] names = new String[]{"3IINFOLTD.NS"};
//        Set<String> names = StockUtil.loadAllStockNames();
        for (String name: names) {
//            if (!nm.contains(name))
//                continue;
            List<String[]> hist = StockUtil.loadStockData(name);
            System.out.println(hist.size()-1+","+name);
            prepareReport(name, hist, 60);
        }
    }
}
