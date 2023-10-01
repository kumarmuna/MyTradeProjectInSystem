package manas.muna.trade.jobs;

import manas.muna.trade.util.SendMail;
import manas.muna.trade.util.StockPropertiesUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.OptionStockDetails;
import manas.muna.trade.vo.StockDetails;

import java.nio.file.Files;
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
import java.util.stream.Collectors;

import static java.time.DayOfWeek.THURSDAY;
import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static java.time.temporal.TemporalAdjusters.lastInMonth;

public class RunOptionTrade {
    static boolean checkIfNearToExpiryDateNeed = true;
    public static List<String> optionStocks = new ArrayList<>();

    public static void main(String[] args){
        List<String> st = new ArrayList<>();
//        st.add("ASIANPAINT.NS");
        calculateOptionLogic(st);
    }
    public static void calculateOptionLogic(List<String> stockNames) {
        checkIfNearToExpiryDateNeed = false;
        Set<String> optionStockNames = StockUtil.loadOptionStockNames();
        Set<String> names;
        if (stockNames.size()==0) {
            names = optionStockNames;
        }else {
            names = new HashSet<>(stockNames);
        }
//        String[] names = StockUtil.loadTestStockNames();
        List<String> avlbStockFileNames = stockAvailableDataNames();
        List<OptionStockDetails> optionList = new ArrayList<>();
        for (String stockName : names){
            String stCheck = stockName;
            if (stockName.indexOf(".") >= 0)
                stCheck = stockName.substring(0, stockName.indexOf("."));
            if (optionStockNames.contains(stCheck)) {
                if (stockName.contains(".NS") && !stockName.contains(".NS.csv"))
                    stockName = stockName + ".csv";
                else if (!stockName.contains(".NS.csv"))
                    stockName = stockName + ".NS.csv";

                if (avlbStockFileNames.contains(stockName)) {
                    System.out.println("Match " + stockName);
                    stockName = stockName.substring(0, stockName.lastIndexOf('.'));
                    List<String[]> datas = StockUtil.loadStockData(stockName);
                    List<String[]> emaDatas = StockUtil.loadEmaData(stockName);
                    Map<String, Boolean> emaIndicator = calculateEmaOptionData(emaDatas, stockName);
                    Map<String, String> finalIndicator = calculateHistoryOptionData(datas, emaIndicator, stockName, optionList);
                    optionList = StockUtil.sortStockDataBasedOnVolumeSizeThenCompareDaysForOption(optionList);

//            Map<String, String> finalIndicator = checkStockOptionTradeStatus(historyDataIndicator);
//                verifyAndSenfNotification(finalIndicator);
                    System.out.println("test");
                }
//                System.out.println("Stock data not available/No CE,PE Eligible :" + stockName);
            }
        }
        prepareNotificationMailAnsSend(optionList);
    }

    private static void prepareNotificationMailAnsSend(List<OptionStockDetails> optionList) {
        List<OptionStockDetails> ceOptionList = new ArrayList<>();
        List<OptionStockDetails> peOptionList = new ArrayList<>();
        StringBuilder ceMessage = new StringBuilder();
        StringBuilder peMessage = new StringBuilder();
        for (OptionStockDetails osd : optionList){
            if (osd.getIsCEPE().equalsIgnoreCase("CE")) {
                ceOptionList.add(osd);
                ceMessage.append(osd.toString());
                ceMessage.append(System.lineSeparator());
            }else if (osd.getIsCEPE().equalsIgnoreCase("PE")) {
                peOptionList.add(osd);
                peMessage.append(osd.toString());
                peMessage.append(System.lineSeparator());
            }
        }
        if (!ceOptionList.isEmpty()){
            SendMail.sendMail(ceMessage.toString(), "", "CE Option Call Stocks Details");
        }
        if (!peOptionList.isEmpty()){
            SendMail.sendMail(peMessage.toString(), "", "PE Option Call Stocks Details");
        }
    }

    private static List<String> stockAvailableDataNames() {
        List<String> files = new ArrayList<>();
        try {
            String readFileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data";
            files = Files.list(Paths.get(readFileLocation))
                    .map(path -> path.getFileName().toFile().getName()).collect(Collectors.toList());
            for (String file : files) {
            }
        }catch (Exception e){
            System.out.println("Exception occur during file name read");
        }
        return files;
    }

//    public static void calculateOptionLogicForStock(String stockName) {
//        if(StockPropertiesUtil.getOptionStockSymbol().contains(stockName)) {
//            if(stockName.indexOf('.') == -1)
//                stockName = stockName+".NS";
//            List<String[]> datas = StockUtil.loadStockData(stockName);
//            List<String[]> emaDatas = StockUtil.loadEmaData(stockName);
//            Map<String, Boolean> emaIndicator = calculateEmaOptionData(emaDatas, stockName);
//            Map<String, String> finalIndicator = calculateHistoryOptionData(datas, emaIndicator, stockName);
//            verifyAndSenfNotification(finalIndicator);
////            System.out.println("test");
//        }else{
//            System.out.println(stockName +" Stock is positive but this is not a option stock");
//        }
//    }

    private static Map<String, Boolean> calculateEmaOptionData(List<String[]> emaDatas, String stockName) {
//        System.out.println("Starting calculateEmaOptionData......");
//        Collections.reverse(emaDatas);
        boolean prevPosEma = false;
        boolean prevNegEma = false;
        int positiveMov = 0;
        int negativeMov = 0;
        int pCount = 1;
        int nCount = 1;
        int count = 0;
        LinkedList<String> movement = new LinkedList<>();
        for (String[] emaData : emaDatas){
//            double dema5 = StockUtil.roundUpBasedOnPrecision(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[2])));
//            double dema9 = StockUtil.roundUpBasedOnPrecision(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[1])));
            int ema8 = 0;
            int ema3 = 0;
            if(!StockUtil.checkOnly83(stockName)) {
                ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[3]));
                ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[4]));
            }else {
                ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[0]));
                ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(emaData[1]));
            }
            if (ema8 <= ema3)
                movement.add("G");
            else if(ema8 >= ema3)
                movement.add("R");

//            if(dema5 >= dema9 || ema8 <=ema3) {
//                if (!prevPosEma && count!=0)
//                    positiveMov++;
//                prevPosEma=true;
//                count++;
//            }else if (dema5 <= dema9 || ema8 >=ema3){
//                if (!prevNegEma && count!=0)
//                    negativeMov++;
//                prevNegEma = true;
//                count++;
//            }
        }
        if (movement.get(0).equalsIgnoreCase("G"))
            positiveMov++;
        if (movement.get(0).equalsIgnoreCase("R"))
            negativeMov++;
        for (int i=1; i<movement.size(); i++){
            if (positiveMov != 0){
                if (movement.get(i).equalsIgnoreCase("G"))
                    pCount++;
                else
                    break;
            }
            if (negativeMov != 0){
                if (movement.get(i).equalsIgnoreCase("R"))
                    nCount++;
                else
                    break;
            }
        }
        Map<String,Boolean> emaIndicator = new HashMap<>();
        emaIndicator.put("positiveMov",(positiveMov > 0));
        emaIndicator.put("negativeMov",(negativeMov > 0));
        emaIndicator.put("optionTradeEligible", positiveMov>0 ? pCount <=1 : negativeMov>0 ? nCount <= 1 : false);
//        System.out.println("End calculateEmaOptionData......");
        return emaIndicator;
    }

    private static Map<String,String> calculateHistoryOptionData(List<String[]> datas, Map<String, Boolean> emaIndicator,
                                                                 String stockName, List<OptionStockDetails> optionList) {
//        System.out.println("Starting calculateHistoryOptionData......");
        Map<String,String> finalIndicator = new HashMap<>();
        Map<String, String> volumeDetails = StockUtil.checkVolumeSize(stockName);
        if (emaIndicator.get("optionTradeEligible") && Boolean.parseBoolean(volumeDetails.get("isVolumeHigh"))) {
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
                double todayPrice = todayOpen < todayClose ? todayOpen : todayClose;
                double yesterdayOpen = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesterdaysData[1]));
                double yesterdayClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesterdaysData[4]));
                double yesterdayPrice = yesterdayOpen < yesterdayClose ? yesterdayClose : yesterdayOpen;
                if (emaIndicator.get("negativeMov") && checkIfNearToExpiryDate(stockName, "negativeMov")) {
                    optionList.add(OptionStockDetails.builder()
                            .volume(Integer.parseInt(volumeDetails.get("todaysVolume")))
                            .compareDays(Integer.parseInt(volumeDetails.get("compareDays")))
                            .isCEPE("PE")
                            .stockName(stockName)
                            .isGreenRed("RED")
                            .build());
                }
                if (emaIndicator.get("positiveMov") && checkIfNearToExpiryDate(stockName, "positiveMov")) {
                    optionList.add(OptionStockDetails.builder()
                            .volume(Integer.parseInt(volumeDetails.get("todaysVolume")))
                            .compareDays(Integer.parseInt(volumeDetails.get("compareDays")))
                            .isCEPE("CE")
                            .stockName(stockName)
                            .isGreenRed("GREEN")
                            .build());
                }
            }
        }
//        System.out.println("End calculateHistoryOptionData......");
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
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
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
        boolean flag = true;
        if(checkIfNearToExpiryDateNeed == true) {
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
//                System.out.println("FirstThursday = " + ft);
//                System.out.println("LastThursday = " + lt);
//                System.out.println("Today = " + today);
                long ltDateDiff = today.getTime() - lt.getTime();
                long ftDateDiff = today.getTime() - ft.getTime();
                if (dateDaysDiff(ftDateDiff) <= 3) {
                    flag = isLastMonthSupport(movement, stockName, "firstthursday");
                } else if (dateDaysDiff(ltDateDiff) <= 3) {
                    flag = isLastMonthSupport(movement, stockName, "lastthursday");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
