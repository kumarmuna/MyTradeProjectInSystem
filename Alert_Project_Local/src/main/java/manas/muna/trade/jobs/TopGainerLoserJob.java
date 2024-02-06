package manas.muna.trade.jobs;

import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;
import manas.muna.trade.vo.StockAttrDetails;
import manas.muna.trade.vo.StockDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TopGainerLoserJob {

    public static void findTopGainerAndLoser(){
        findTopGainerAndLoser(0);
    }
    public static void findTopGainerAndLoser(int previousDays) {
        Set<String> stockNames = new HashSet<>();
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\stock";
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int i=2;
            fileLocation = fileLocation + "\\" + files.get(previousDays);
            System.out.println("Reading from-" + fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            for (String[] dt: stockData){
                stockNames.add(dt[0].split("= ")[1]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        Set<String> stockNames = StockUtil.loadAllStockNames();
//        String[] stockNames = StockUtil.loadTestStockNames();
//        List<StockAttrDetails> stockAttrDetailsList = new Stack<>();
        List<StockAttrDetails> gainerList = new Stack<>();
        List<StockAttrDetails> loserList = new Stack<>();
        for (String stockName:stockNames){
//            if (!stockName.equals("BRNL.NS"))
//                continue;
            List<String[]> stockHistoryData = StockUtil.loadStockData(stockName);
            stockHistoryData = stockHistoryData.subList(previousDays-1, stockHistoryData.size()-1);
            List<String[]> prevHistoryData = stockHistoryData;
            prevHistoryData = prevHistoryData.subList(1, prevHistoryData.size()-1);
            StockAttrDetails stockAttrDetail = StockUtil.prepareStockAttributeData(stockName, stockHistoryData.get(1),stockHistoryData.get(0));
            if (stockAttrDetail.getPrice() <=100)
                continue;
            Map<String, Object> candlePatternsDetail = CandleUtil.checkBearishStockPatterns(stockName, prevHistoryData);
//            if(candlePatternsDetail.get("candleTypesOccur") ==null) {
//                candlePatternsDetail = CandleUtil.checkBullishStockPatterns(stockName, prevHistoryData);
//            }
//            stockAttrDetailsList.add(stockAttrDetail);
            stockAttrDetail.setPrevDaycandleType(candlePatternsDetail.get("candleTypesOccur")==null? "":candlePatternsDetail.get("candleTypesOccur").toString());
            if (stockAttrDetail.getGain()>0.0)
                gainerList.add(stockAttrDetail);
            else if (stockAttrDetail.getLose()>=0.0)
                loserList.add(stockAttrDetail);
        }
        gainerList = StockUtil.sortStockBasedOnGainerOrLoser(gainerList,"GAINERS");
        loserList = StockUtil.sortStockBasedOnGainerOrLoser(loserList, "LOSERS");
        gainerList = gainerList.size()>5?gainerList.subList(0,5):gainerList;
        loserList = loserList.size()>5?loserList.subList(0,5):loserList;
        System.out.println("-----------------------");
        for (StockAttrDetails sad:gainerList)
            System.out.println(sad);
        System.out.println("-----------------------");
        for (StockAttrDetails sad:loserList)
            System.out.println(sad);
        System.out.println("-----------------------");
    }

    private static void stockRefineWithVolumeData() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\stock";
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            fileLocation = fileLocation + "\\" + files.get(0);
            System.out.println("Reading from-" + fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            for (String[] dt: stockData){
//                Map<String, String> volumeDetails = StockUtil.getVolumeDetails();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void findIfStockSideWays() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\stock";
        List<String> stocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int i = 2;
            fileLocation = fileLocation + "\\" + files.get(i);
            System.out.println("Reading from-" + fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            for (String[] dt: stockData){
//                Map<String, String> volumeDetails = StockUtil.getVolumeDetails();
                String name = dt[0].split("= ")[1];
//                if (!name.equals("ADANIPOWER.NS"))
//                    continue;
                List<String[]> historyData = StockUtil.loadStockData(name);
                historyData = historyData.subList(i,historyData.size()-1);
                boolean flag = findIfStockSideWays(name, historyData);
                if (flag) {
                    stocks.add(name);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(stocks);

    }

    public static boolean findIfStockSideWays(String stockName, List<String[]> historyData) {
//        String stockName = "";
        boolean flag = false;
        if (historyData == null)
            historyData = StockUtil.loadStockData(stockName);
        int days = 9;
        int candleCount = 0;
        int notContinue = 0;
        CandleStick todayCandle = CandleUtil.prepareCandleData(historyData.get(1), historyData.get(0));
        CandleStick prevCandle = CandleUtil.prepareCandleData(historyData.get(2), historyData.get(1));
        double high = todayCandle.getHigh()< prevCandle.getHigh()? prevCandle.getHigh() : todayCandle.getHigh();
        double low = todayCandle.getLow()< prevCandle.getLow()? todayCandle.getLow() : prevCandle.getLow();
        for (int i=2;i<=days;i++){
            CandleStick cs = CandleUtil.prepareCandleData(historyData.get(i+1), historyData.get(i));
            if (cs.getHigh()<=high && cs.getLow()>=low) {
                candleCount++;
                notContinue = 0;
            }else {
                notContinue++;
            }
            if (notContinue >=3)
                break;
        }
        if (candleCount==4)
            flag = true;
        return flag;
    }
    public static void getHighVolumeStocks(int prevDays) {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\all_stock_candle\\stock";
        List<String> stocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int i = 2;
            fileLocation = fileLocation + "\\" + files.get(prevDays);
            System.out.println("Reading from-" + fileLocation);
            List<String[]> stockData = StockUtil.readFileData(fileLocation);
            for (String[] dt: stockData){
                String name = dt[0].split("= ")[1];
//                if (!name.equals("ADANIPOWER.NS"))
//                    continue;
                List<String[]> historyData = StockUtil.loadStockData(name);
                historyData = historyData.subList(prevDays,historyData.size()-1);
                Map<String, String> volumeDetails = StockUtil.getVolumeDetails(name, historyData, 30);
                if (Integer.parseInt(volumeDetails.get("highVolPos")) <=1 ) {
                    stocks.add(name);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(stocks);
    }

    public static void main(String[] args) {
//        findIfStockSideWays();
//        findTopGainerAndLoser(2);
        getHighVolumeStocks(1);
//        stockRefineWithVolumeData();
    }
}