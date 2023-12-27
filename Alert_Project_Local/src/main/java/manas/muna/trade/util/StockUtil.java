package manas.muna.trade.util;

import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ComparisonChain;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import manas.muna.trade.jobs.CalculateFuturePrediction;
import manas.muna.trade.jobs.CalculateProfitAndStoreJob;
import manas.muna.trade.jobs.ReadResultsDateDataJob;
import manas.muna.trade.vo.EmaChangeDetails;
import manas.muna.trade.vo.OptionStockDetails;
import manas.muna.trade.vo.StockAttrDetails;
import manas.muna.trade.vo.StockDetails;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;

import java.io.*;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StockUtil {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static List<StockDetails> listStockDetailsToSendMailForBothIndicator = new ArrayList<>();
    private static List<StockDetails> listStockDetailsToSendMailForEMA8And3 = new ArrayList<>();
    private static List<StockDetails> listStockDetailsToSendMailForDEMA9And5 = new ArrayList<>();
    private static List<StockDetails> listStockDetailsToSendMailFor83IndOnlyUpDown = new ArrayList<>();
    private static List<StockDetails> listStockVolumeCheckOnly = new ArrayList<>();
    private static List<StockDetails> listStockNewImplAdded = new ArrayList<>();
    private static List<StockDetails> listTrendStocks = new ArrayList<>();
    public static String[] loadStockNames() {
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
            p.getProperty("stock_list");
        }catch (Exception e){
            e.printStackTrace();
        }
        return p.getProperty("stock_list").split(",");
    }

    public static String[] loadTestStockNames() {
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
        }catch (Exception e){
            e.printStackTrace();
        }
        return p.getProperty("test_stock_list").split(",");
//        return p.getProperty("d_stock_list").split(",");
    }

    public static Set<String> loadAllStockNames() {
        String[] keys1 = {"a_stock_list","b_stock_list","c_stock_list","d_stock_list","e_stock_list","f_stock_list","g_stock_list","h_stock_list","i_stock_list"
                ,"j_stock_list","k_stock_list","l_stock_list","m_stock_list","n_stock_list","o_stock_list","p_stock_list","q_stock_list","r_stock_list"
                ,"s_stock_list","t_stock_list","u_stock_list","v_stock_list","w_stock_list","x_stock_list","y_stock_list","z_stock_list"};
//        String[] keys = {"index_list","a_stock_list","b_stock_list","c_stock_list"};
//        String[] keys = {"index_list"};
        String[] old_keys = {"index_list","a_stock_list","b_stock_list","c_stock_list","d_stock_list","e_stock_list_part"};
        String[] new_keys = {"index_list","a_stock_list","b_stock_list","c_stock_list","d_stock_list","x_stock_list","y_stock_list","z_stock_list"};
        String[] keys = new String[old_keys.length + new_keys.length];
        // Copying elements from array1
        System.arraycopy(old_keys, 0, keys, 0, old_keys.length);
        // Copying elements from array2
        System.arraycopy(new_keys, 0, keys, old_keys.length, new_keys.length);
        Set<String> list = new HashSet<>();
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
            p.getProperty("stock_list");
        }catch (Exception e){
            e.printStackTrace();
        }
        for(String key : new_keys){
            list.addAll(Arrays.asList(p.getProperty(key).split(",")));
        }

        return list;
    }

    public static String[] loadBuyStockNames() {
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\buy-stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
            p.getProperty("buy_stock_list");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(StringUtils.isEmpty(p.getProperty("buy_stock_list"))){
            return new String[0];
        }else {
            return p.getProperty("buy_stock_list").split(",");
        }
    }

    public static String[] loadTestBuyStockNames() {
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\buy-stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
            p.getProperty("test_buy_stock_list");
        }catch (Exception e){
            e.printStackTrace();
        }
//        if(StringUtils.isEmpty(p.getProperty("buy_stock_list"))){
//            return new String[0];
//        }else {
//            return p.getProperty("buy_stock_list").split(",");
//        }
        return p.getProperty("test_buy_stock_list").split(",");
    }

    public static String[] loadBuyStockFileData(String propsKey) {
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\buy-stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
        }catch (Exception e){
            e.printStackTrace();
        }
        return p.getProperty(propsKey).split(",");
    }

    public static Map<String, String> readEmaDataModify(String stockEmaDataLoad, String stockName) {
        {
            Map<String, String> notificationData = new HashMap<>();
            try {
                int countDay = 0;
                int stockIsGreen = 0;
                int stockIsRed = 0;
                int ema8_3_stockIsGreen = 0;
                int ema8_3_stockIsRed = 0;
                File file = new File(stockEmaDataLoad);
                if (!file.exists())
                    file.createNewFile();
                FileReader filereader = new FileReader(file);
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(1)
                        .build();
                List<String[]> allData = csvReader.readAll();
                //get Stock history data
                List<String[]> stData = StockUtil.loadStockData(stockName);
                //check is stock RED/GREEN
                Map<String, Object> marketData = checkStockGreenOrRed(allData, stockName, stData);
                //TODO it will check based on enabled added in properties
                Map<String,Boolean> fiveDatHighLowData = checkBreakLastFiveDaysHighLow(stockName);
                Map<String,String> isVolumeHigh = checkVolumeSize(stockName);
                Map<String, Boolean> isCandleRedOrGreen = checkCandleRedOrGreen(stockName, stData);
                boolean marketMoveDiff = checkMarketMovementCheckForDay(stData);
                Map<String, Boolean> isCloseLessThanPreviousDay = checkCloseLessThanPreviousDay(stData, isCandleRedOrGreen);
                boolean ifAlreadyNotMoved10Percent = checkIfalreadyMoved10Percent(stData, isCandleRedOrGreen);
                boolean isCandleHeadTailLooksGood = checkHigherLessThanLowerOfCandle(stData, isCandleRedOrGreen);//hammer pattern TODO modify for red
//                isCandleHeadTailLooksGood = true;//need to modify
                Map<String, Boolean> isMA100_5_Cross = validateMA100And5(allData);
                checkIndicatorStatusAndSetNotificationData(marketData, notificationData, fiveDatHighLowData, isVolumeHigh ,stockName,
                        isCandleRedOrGreen, marketMoveDiff, isCloseLessThanPreviousDay, isCandleHeadTailLooksGood, ifAlreadyNotMoved10Percent, isMA100_5_Cross);
            }catch (Exception e){
                System.out.println("Error................."+stockName);
                e.printStackTrace();
            }
            return notificationData;
        }
    }
    public static Map<String, String> readEmaData(String stockEmaDataLoad, String stockName) {
        Map<String, String> notificationData = new HashMap<>();
        try {
            int countDay = 0;
            int stockIsGreen = 0;
            int stockIsRed = 0;
            int ema30_9_stockIsGreen = 0;
            int ema30_9_stockIsRed = 0;
            int ema9_5_stockIsGreen = 0;
            int ema9_5_stockIsRed = 0;
            int ema8_3_stockIsGreen = 0;
            int ema8_3_stockIsRed = 0;
            File file = new File(stockEmaDataLoad);
            if (!file.exists())
                file.createNewFile();
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            //get Stock history data
            List<String[]> stData = StockUtil.loadStockData(stockName);
            //check is stock RED/GREEN
            Map<String, Object> marketData = checkStockGreenOrRed(allData, stockName, stData);
            stockIsGreen = (int) marketData.get("stockIsGreen");
            stockIsRed = (int) marketData.get("stockIsRed");
            ema30_9_stockIsGreen = (int) marketData.get("ema30_9_stockIsGreen");
            ema30_9_stockIsRed = (int) marketData.get("ema30_9_stockIsRed");
            ema9_5_stockIsGreen = (int) marketData.get("ema9_5_stockIsGreen");
            ema9_5_stockIsRed = (int) marketData.get("ema9_5_stockIsRed");
            ema8_3_stockIsGreen = (int) marketData.get("ema8_3_stockIsGreen");
            ema8_3_stockIsRed = (int) marketData.get("ema8_3_stockIsRed");
            //TODO it will check based on enabled added in properties
            Map<String,Boolean> fiveDatHighLowData = checkBreakLastFiveDaysHighLow(stockName);
            Map<String,String> isVolumeHigh = checkVolumeSize(stockName);
            Map<String, Boolean> isCandleRedOrGreen = checkCandleRedOrGreen(stockName, stData);
            boolean marketMoveDiff = checkMarketMovementCheckForDay(stData);
            Map<String, Boolean> isCloseLessThanPreviousDay = checkCloseLessThanPreviousDay(stData, isCandleRedOrGreen);
            boolean ifAlreadyNotMoved10Percent = checkIfalreadyMoved10Percent(stData, isCandleRedOrGreen);
            boolean isCandleHeadTailLooksGood = checkHigherLessThanLowerOfCandle(stData, isCandleRedOrGreen);//hammer pattern TODO modify for red
            Map<String, Boolean> isMA100_5_Cross = validateMA100And5(allData);
            checkIndicatorStatusAndSetNotificationData(marketData, notificationData, fiveDatHighLowData, isVolumeHigh ,stockName,
                    isCandleRedOrGreen, marketMoveDiff, isCloseLessThanPreviousDay, isCandleHeadTailLooksGood, ifAlreadyNotMoved10Percent, isMA100_5_Cross);
        }catch (Exception e){
            System.out.println("Error................."+stockName);
            e.printStackTrace();
        }
        return notificationData;
    }

    private static boolean checkIfalreadyMoved10Percent(List<String[]> stData, Map<String, Boolean> isCandleRedOrGreen) {
        boolean isAlreadyMoved = true;
        double totalMov = 0.0;
        double totalPrice = 0.0;
        int index = 0;
        for (int i=0; i<5; i++){
            String[] prevData = stData.get(i);
            double close = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(prevData[4]));
            double open = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(prevData[1]));
            index = i;
            if (isCandleRedOrGreen.get("isCandleRed")) {
                if (close < open){
                    double pClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i+1)[4]));
                    totalMov = totalMov + (pClose - close);
                }else
                    break;
            }else if (isCandleRedOrGreen.get("isCandleGreen")){
                if (close > open) {
                    double pClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i + 1)[4]));
                    totalMov = totalMov + (close - pClose);
                } else
                    break;
            }
        }
        totalPrice = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(index+1)[4]));

        double prct = StockUtil.calculatePercantage(totalMov, totalPrice);
        if (prct >= 7.50)
            isAlreadyMoved = false;

        if(isAlreadyMoved){
            String[] todayData = stData.get(0);
            double todClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todayData[4]));
            double yClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(1)[4]));
            double todayMove = todClose - yClose;
            double todPrct = StockUtil.calculatePercantage(todayMove, yClose);
            if (todPrct >= 5.00)
                isAlreadyMoved = false;
        }

        return isAlreadyMoved;
    }

    //TODO handle for RED also
    private static boolean checkHigherLessThanLowerOfCandle(List<String[]> stData, Map<String, Boolean> isCandleRedOrGreen) {
        boolean flag = true;
        String[] todaysDt = stData.get(0);
        double tail = 0.0;
        double head = 0.0;
        if (StockPropertiesUtil.booleanIndicators.get("checkHigherLessThanLowerOfCandle")){
            if (Double.parseDouble(todaysDt[1]) < Double.parseDouble(todaysDt[4])) {
                head = Double.parseDouble(todaysDt[2]) - Double.parseDouble(todaysDt[4]);
                tail = Double.parseDouble(todaysDt[1]) - Double.parseDouble(todaysDt[3]);
            }else{
                head = Double.parseDouble(todaysDt[2]) - Double.parseDouble(todaysDt[1]);
                tail = Double.parseDouble(todaysDt[4]) - Double.parseDouble(todaysDt[3]);
            }
            if(isCandleRedOrGreen.get("isCandleGreen")) {
                double ht = (head - tail);
                if ((tail <= 0.50) || ht > 1.50)
                    flag = false;
            }else if(isCandleRedOrGreen.get("isCandleRed")) {
                double ht = (tail - head);
                if ((head <= 0.50) || ht > 1.50)
                    flag = false;
            }
        }
        if (isCandleRedOrGreen.get("isCandleGreen")) {
            //if head tail fail -> then check last 3 days high
            if (!flag & tail >= 0.50) {
                double tClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysDt[4]));
                for (int i = 1; i < 2; i++) {
                    double high = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[2]));
                    if (tClose < high) {
                        flag = false;
                        break;
                    } else
                        flag = true;
                }
            } else if (tail >= 0.50) {
                double tClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysDt[4]));
                double high = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(1)[2]));
                if (tClose < high)
                    flag = false;
                //extra check if not cross last 3 days
                for (int i = 1; i < 4; i++) {
                    double openCloseHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[1]) < Double.parseDouble(stData.get(i)[4]) ?
                            Double.parseDouble(stData.get(i)[4]) : Double.parseDouble(stData.get(i)[1]));
                    if (tClose < openCloseHigh) {
                        flag = false;
                        break;
                    } else
                        flag = true;
                }
            }
        }else if (isCandleRedOrGreen.get("isCandleRed")) {
            //if head tail fail -> then check last 3 days high
            if (!flag & tail >= 0.50) {
                double tClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysDt[4]));
                for (int i = 1; i < 5; i++) {
                    double low = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[3]));
                    if (tClose < low) {
                        flag = false;
                        break;
                    } else
                        flag = true;
                }
            } else if (tail >= 0.50) {
                double tClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysDt[4]));
                double high = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(1)[2]));
                if (tClose > high)
                    flag = false;
                //extra check if not cross last 3 days
                for (int i = 1; i < 4; i++) {
                    double openCloseHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[2]) < Double.parseDouble(stData.get(i)[4]) ?
                            Double.parseDouble(stData.get(i)[4]) : Double.parseDouble(stData.get(i)[2]));
                    if (tClose > openCloseHigh) {
                        flag = false;
                        break;
                    } else
                        flag = true;
                }
            }
        }
        return flag;
    }

    private static Map<String, Boolean> checkCloseLessThanPreviousDay(List<String[]> stData, Map<String, Boolean> isCandleGreenOrRed) {
        Map<String, Boolean> flag = new HashMap<>();
        flag.put("isRed", false);
        flag.put("isGreen",false);
        if (StockPropertiesUtil.getBooleanIndicatorProps().get("checkCloseLessThanPreviousDay")){
            double todayClose = Double.parseDouble(stData.get(0)[4]);
            double yesData = 0.0;
            for (int i=1; i<stData.size();i++){
                if (stData.get(i)[1]!=null){
                    String[] dt = stData.get(i);
                    if (isCandleGreenOrRed.get("isCandleGreen"))
                        yesData = Double.parseDouble(dt[1]) < Double.parseDouble(dt[4]) ? Double.parseDouble(dt[4]) : Double.parseDouble(dt[1]);
                    else if (isCandleGreenOrRed.get("isCandleRed"))
                        yesData = Double.parseDouble(dt[1]) < Double.parseDouble(dt[4]) ? Double.parseDouble(dt[1]) : Double.parseDouble(dt[4]);
                    break;
                }
            }
            if (isCandleGreenOrRed.get("isCandleGreen")) {
                if (todayClose <= yesData) {
                    flag.put("isRed", false);
                    flag.put("isGreen", true);
                }
            }else if (isCandleGreenOrRed.get("isCandleRed")) {
                if (todayClose >= yesData) {
                    flag.put("isRed", true);
                    flag.put("isGreen", false);
                }
            }

        }
        return flag;
    }

    private static boolean checkMarketMovementCheckForDay(List<String[]> stData) {
        boolean flag = true;
        if (StockPropertiesUtil.getBooleanIndicatorProps().get("checkMarketMovementCheckForDay")) {
            String[] todaysData = stData.get(0);
            double dif = Double.parseDouble(todaysData[1]) < Double.parseDouble(todaysData[4]) ?
                    Double.parseDouble(todaysData[4]) - Double.parseDouble(todaysData[1]) :
                            Double.parseDouble(todaysData[1]) - Double.parseDouble(todaysData[4]);
            if (dif < 1.00) {
                flag = false;
            }
            if(!flag){
                double prcnt = StockUtil.calculatePercantage(dif, Double.parseDouble(todaysData[4]));
                if (prcnt > 0.65)
                    flag = true;
            }
        }
        return flag;
    }

    private static Map<String, Boolean> checkCandleRedOrGreen(String stockName, List<String[]> stData) {
        Map<String, Boolean> candleCheckDt = new HashMap<>();
        if (StockPropertiesUtil.getBooleanIndicatorProps().get("checkCandleRedOrGreen")){
            String[] todaysData = stData.get(0);
            if (Double.parseDouble(todaysData[1]) <= Double.parseDouble(todaysData[4])){
                candleCheckDt.put("isCandleRed", false);
                candleCheckDt.put("isCandleGreen", true);
            }else{
                candleCheckDt.put("isCandleRed", true);
                candleCheckDt.put("isCandleGreen", false);
            }
        }else {
            candleCheckDt.put("isCandleRed", true);
            candleCheckDt.put("isCandleGreen", true);
        }
        return candleCheckDt;
    }

    private static void checkAndValidateStockdataAndSetNotification(Map<String, Object> stockDetailsData) {
        System.out.println("inside checkAndValidateStockdataAndSetNotification");
        List<StockDetails> listStockDetailsToSendMailForEMA8And3 = sortStockDataBasedOnCompareDaysThenVolume(getListStockDetailsToSendMailForEMA8And3());
//        List<StockDetails> listStockDetailsToSendMailForDMA9And5 = sortStockDataBasedOnVolumeSizeThenCompareDays(getListStockDetailsToSendMailForDEMA9And5());
//        List<StockDetails> listStockDetailsToSendMailForBoth = sortStockDataBasedOnVolumeSizeThenCompareDays(getListStockDetailsToSendMailForBothIndicator());
        List<StockDetails> listStockDetailsOfCross = sortStockDataBasedOnVolumeSizeThenCompareDays(getListStockDetailsOfCross());

        stockDetailsData.put("stockEma8And3", listStockDetailsToSendMailForEMA8And3);
//        stockDetailsData.put("stockDEmndicator", listStockDetailsToSendMailForBoth);
        stockDetailsData.put("stockDetailsOfCross", listStockDetailsOfCross);
        System.out.println("end checkAndValidateStockdataAndSetNotification");
    }

    public static List<StockDetails> sortStockDataBasedOnVolumeSizeThenCompareDays(List<StockDetails> listStockDetailsToSendMail) {
        if (!listStockDetailsToSendMail.isEmpty()) {
            //Sorting first based or Green and Red
            listStockDetailsToSendMail.sort((o1, o2) -> o1.getIsGreenRed().compareTo(o2.getIsGreenRed()));
            listStockDetailsToSendMail = separateGreenAndRedStockThenSortBasedOnVolume(listStockDetailsToSendMail);
            //Sort based on no of days volume higher
//        Collections.sort(listStockDetailsToSendMailForEMA8And3, new Comparator<StockDetails>() {
//            @Override
//            public int compare(StockDetails s1, StockDetails s2) {
//                return s2.getHighVolumeCompareDays() - s1.getHighVolumeCompareDays();
//            }
//        });
        }
        return listStockDetailsToSendMail;
    }

    public static List<StockDetails> sortStockDataBasedOnCompareDays(List<StockDetails> listStockDetailsToSendMail) {
        if (!listStockDetailsToSendMail.isEmpty()) {
            //Sorting first based or Green and Red
            listStockDetailsToSendMail.sort((o1, o2) -> o1.getIsGreenRed().compareTo(o2.getIsGreenRed()));
//            listStockDetailsToSendMail = separateGreenAndRedStockThenSortBasedOnVolume(listStockDetailsToSendMail);
            //Sort based on no of days volume higher
        Collections.sort(listStockDetailsToSendMail, new Comparator<StockDetails>() {
            @Override
            public int compare(StockDetails s1, StockDetails s2) {
                return s2.getHighVolumeCompareDays() - s1.getHighVolumeCompareDays();
            }
        });
        }
        return listStockDetailsToSendMail;
    }

    public static List<StockDetails> sortStockDataBasedOnCompareDaysThenVolume(List<StockDetails> listStockDetailsToSendMail) {
        if (!listStockDetailsToSendMail.isEmpty()) {
            //Sorting first based or Green and Red
            listStockDetailsToSendMail.sort((o1, o2) -> o1.getIsGreenRed().compareTo(o2.getIsGreenRed()));
//            listStockDetailsToSendMail = separateGreenAndRedStockThenSortBasedOnVolume(listStockDetailsToSendMail);
            //Sort based on no of days volume higher
            Collections.sort(listStockDetailsToSendMail, new Comparator<StockDetails>() {
                @Override
                public int compare(StockDetails s1, StockDetails s2) {
//                    int val = Integer.compare(s2.getHighVolumeCompareDays(), s1.getHighVolumeCompareDays());
//                    if (val != 0){
//                        return val;
//                    }
//                    return Integer.compare(s2.getVolume(), s1.getVolume());
                    return ComparisonChain.start()
                            .compare(s1.getIsGreenRed(), s2.getIsGreenRed())
                            .compare(s1.getHighVolumeCompareDays(), s2.getHighVolumeCompareDays())
                            .compare(s2.getVolume(), s1.getVolume())
                            .result();
                }
            });
        }
        return listStockDetailsToSendMail;
    }

    public static List<OptionStockDetails> sortStockDataBasedOnVolumeSizeThenCompareDaysForOption(List<OptionStockDetails> listStockDetailsToSendMail) {
        if (!listStockDetailsToSendMail.isEmpty()) {
            //Sorting first based or Green and Red
            listStockDetailsToSendMail.sort((o1, o2) -> o1.getIsGreenRed().compareTo(o2.getIsGreenRed()));
            listStockDetailsToSendMail = separateGreenAndRedStockThenSortBasedOnVolumeForOption(listStockDetailsToSendMail);
            //Sort based on no of days volume higher
//        Collections.sort(listStockDetailsToSendMailForEMA8And3, new Comparator<StockDetails>() {
//            @Override
//            public int compare(StockDetails s1, StockDetails s2) {
//                return s2.getHighVolumeCompareDays() - s1.getHighVolumeCompareDays();
//            }
//        });
        }
        return listStockDetailsToSendMail;
    }

    public static List<StockDetails> separateGreenAndRedStockThenSortBasedOnVolume(List<StockDetails> listStockDetailsToSendMail) {
        List<StockDetails> listStockToTrade = new ArrayList<>();
        List<StockDetails> listGreenStock = new ArrayList<>();
        List<StockDetails> listRedStock = new ArrayList<>();
        for (StockDetails sd : listStockDetailsToSendMail){
            if (sd.getIsGreenRed().equals("GREEN"))
                listGreenStock.add(sd);
            if (sd.getIsGreenRed().equals("RED"))
                listRedStock.add(sd);
        }
        //Sort based on Volume size
        Collections.sort(listGreenStock, new Comparator<StockDetails>() {
            @Override
            public int compare(StockDetails s1, StockDetails s2) {
                return s2.getVolume() - s1.getVolume();
            }
        });
        Collections.sort(listRedStock, new Comparator<StockDetails>() {
            @Override
            public int compare(StockDetails s1, StockDetails s2) {
                return s2.getVolume() - s1.getVolume();
            }
        });
//        listGreenStock.sort(Comparator.comparingInt(StockDetails::getHighVolumeCompareDays));
////                .thenComparing((StockDetails::getVolume)));
//        listRedStock.sort(Comparator.comparingInt(StockDetails::getHighVolumeCompareDays));
////                .thenComparing((StockDetails::getVolume)));
        listStockToTrade.addAll(listGreenStock);
        listStockToTrade.addAll(listRedStock);
        return listStockToTrade;
    }

    public static List<StockDetails> separateGreenAndRedStockThenSortBasedOnTrenddays(List<StockDetails> listStockDetailsToSendMail) {
        List<StockDetails> listStockToTrade = new ArrayList<>();
        List<StockDetails> listGreenStock = new ArrayList<>();
        List<StockDetails> listRedStock = new ArrayList<>();
        for (StockDetails sd : listStockDetailsToSendMail){
            if (sd.getIsGreenRed().equals("GREEN"))
                listGreenStock.add(sd);
            if (sd.getIsGreenRed().equals("RED"))
                listRedStock.add(sd);
        }
        //Sort based on Volume size
        Collections.sort(listGreenStock, new Comparator<StockDetails>() {
            @Override
            public int compare(StockDetails s1, StockDetails s2) {
                int i =  s2.getTrendDays() - s1.getTrendDays();
                if (i==0){
                    return s2.getHighVolumeCompareDays()-s1.getHighVolumeCompareDays();
                }else
                    return i;
            }
        });
        Collections.sort(listRedStock, new Comparator<StockDetails>() {
            @Override
            public int compare(StockDetails s1, StockDetails s2) {
                int i = s2.getTrendDays() - s1.getTrendDays();
                if (i==0){
                    return s2.getHighVolumeCompareDays()-s1.getHighVolumeCompareDays();
                }else
                    return i;
            }
        });
        listStockToTrade.addAll(listGreenStock);
        listStockToTrade.addAll(listRedStock);
        return listStockToTrade;
    }

    public static List<OptionStockDetails> separateGreenAndRedStockThenSortBasedOnVolumeForOption(List<OptionStockDetails> listStockDetailsToSendMail) {
        List<OptionStockDetails> listStockToTrade = new ArrayList<>();
        List<OptionStockDetails> listGreenStock = new ArrayList<>();
        List<OptionStockDetails> listRedStock = new ArrayList<>();
        for (OptionStockDetails sd : listStockDetailsToSendMail){
            if (sd.getIsGreenRed().equals("GREEN"))
                listGreenStock.add(sd);
            if (sd.getIsGreenRed().equals("RED"))
                listRedStock.add(sd);
        }
        //Sort based on Volume size
        Collections.sort(listGreenStock, new Comparator<OptionStockDetails>() {
            @Override
            public int compare(OptionStockDetails s1, OptionStockDetails s2) {
                return s2.getVolume() - s1.getVolume();
            }
        });
        Collections.sort(listRedStock, new Comparator<OptionStockDetails>() {
            @Override
            public int compare(OptionStockDetails s1, OptionStockDetails s2) {
                return s2.getVolume() - s1.getVolume();
            }
        });
        listStockToTrade.addAll(listGreenStock);
        listStockToTrade.addAll(listRedStock);
        return listStockToTrade;
    }

    public static Map<String, String> checkVolumeSize(String stockName) {
        Map<String, String> todayVolumeHigh = new HashMap<>();
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("volumeCheckIndicator")) {
            int yesVolume = 0;
            int todayVolume = 0;
            int days = 0;
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\" + stockName + ".csv");
            try {
                FileReader filereader = new FileReader(path.toString());
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(1)
                        .build();
                List<String[]> allData = csvReader.readAll();
                Collections.reverse(allData);
                if (!allData.get(0)[6].equals("null")) {
                    todayVolume = Integer.parseInt(allData.get(0)[6]);
                }
                for (int i = 1; i <= 5; i++) {
                    if (!allData.get(i)[6].equals("null")) {
                        yesVolume = Integer.parseInt(allData.get(i)[6]);
                        if (todayVolume < yesVolume)
                            break;
                        days++;
                    }
//                    if (!allData.get(i)[6].equals("null")) {
//                        if (todayVolume > Integer.parseInt(allData.get(i)[6]))
//                            days++;
//                        else if (todayVolume < Integer.parseInt(allData.get(i)[6]))
//                            break;
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            todayVolumeHigh.put("isVolumeHigh", todayVolume >= yesVolume? "true" : "false");
            todayVolumeHigh.put("todaysVolume", String.valueOf(todayVolume));
            todayVolumeHigh.put("compareDays", String.valueOf(days));
        }else{
            todayVolumeHigh.put("isVolumeHigh", "true");
            todayVolumeHigh.put("todaysVolume", String.valueOf(0));
            todayVolumeHigh.put("compareDays", String.valueOf(0));
        }

        return todayVolumeHigh;
    }

    private static void checkIndicatorStatusAndSetNotificationData(Map<String, Object> marketData, Map<String, String> notificationData,
                                                                   Map<String,Boolean> fiveDatHighLowData, Map<String, String> isVolumeHigh,
                                                                   String stockName, Map<String, Boolean> isCandleRedOrGreen, Boolean marketMoveDiff,
                                                                   Map<String, Boolean> isCloseLessThanPreviousDay, Boolean isCandleHeadTailLooksGood,
                                                                   Boolean ifAlreadyNotMoved10Percent,Map<String, Boolean> isMA100_5_Cross) {
        int ema30_9_stockIsGreen = (int) marketData.get("ema30_9_stockIsGreen");
        int ema30_9_stockIsRed = (int) marketData.get("ema30_9_stockIsRed");
        int ema9_5_stockIsGreen = (int) marketData.get("ema9_5_stockIsGreen");
        int ema9_5_stockIsRed = (int) marketData.get("ema9_5_stockIsRed");
        int ema8_3_stockIsGreen = (int) marketData.get("ema8_3_stockIsGreen");
        int ema8_3_stockIsRed = (int) marketData.get("ema8_3_stockIsRed");
        int minEmaGreenRedCheckCount = StockPropertiesUtil.getIntegerIndicatorProps().get("minEmaGreenRedCheckCount");
        int maxEmaGreenRedCheckCount = StockPropertiesUtil.getIntegerIndicatorProps().get("maxEmaGreenRedCheckCount");
        //TODO Disabling both indicator add list
        boolean disableBothList = false;
        //add this to mail all stock cross ema 8 & 3 but no indicator chcek
        if (marketData.get("marketMovement").equals("Green") && isCandleRedOrGreen.get("isCandleGreen")
                && isCandleRedOrGreen.get("isCandleGreen") && marketMoveDiff
//                && isCandleHeadTailLooksGood
//                && isCloseLessThanPreviousDay
//                && ifAlreadyNotMoved10Percent
                && isHigherVolumeThannDays(stockName, 13)
                && ((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount))
        && isMA100_5_Cross.get("buy")){
            StockDetails sd = StockDetails.builder()
                    .isGreenRed("GREEN")
                    .stockName(stockName)
                    .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
                    .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
                    .build();
            StockUtil.addStockDetailsToSendMailFor83IndOnlyUpDown(sd);
        }else if(marketData.get("marketMovement").equals("Red")
//                    && isCandleRedOrGreen.get("isCandleRed")
                && isHigherVolumeThannDays(stockName, 13)
                && ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <= maxEmaGreenRedCheckCount))
//                && isMA100_5_Cross.get("sell")
        ){
            StockDetails sd = StockDetails.builder()
                    .isGreenRed("RED")
                    .stockName(stockName)
                    .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
                    .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
                    .build();
            StockUtil.addStockDetailsToSendMailFor83IndOnlyUpDown(sd);
        }
//        if (marketData.get("marketMovement").equals("Red") && (ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount)){
//            StockUtil.addStockDetailsToSendMailFor83IndOnlyUpDown(StockDetails.builder()
//                    .isGreenRed("RED")
//                    .stockName(stockName)
//                    .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
//                    .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
//                    .build());
//        }

        if (marketData.get("marketMovement").equals("Green") && Boolean.parseBoolean(isVolumeHigh.get("isVolumeHigh"))
//                && isCandleRedOrGreen.get("isCandleGreen")
                && marketMoveDiff && isCandleHeadTailLooksGood && !isCloseLessThanPreviousDay.get("isGreen")
                && ((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount)
//                ||(ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount)
        )){
            if(disableBothList && (ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount)
                    && (ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount)){
                StockUtil.addStockDetailsToSendMailForBothIndicator(StockDetails.builder()
                                .isGreenRed("GREEN")
                                .stockName(stockName)
                                .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
                                .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
                        .build());
                //TODO will uncomment later
//                CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName);
//                if (fiveDatHighLowData.get("fiveDayHigh")){ //checkCandelHIghLowGap()
//                    notificationData.put("stockIsGreen", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Stock " + stockName + " is green last 3 days, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "GREEN: BOTH This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
//            }else if(!(ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount)
//                    && (ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount)){
            }
            if((ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount)){
                if (StockUtil.checkCandleConditions(stockName, "GREEN") && !StockUtil.isCrossOverHappenWithinDays(stockName, "GREEN", 3)
                && ifAlreadyNotMoved10Percent){
                StockUtil.addStockDetailsToSendMailForDEMA9And5(StockDetails.builder()
                        .isGreenRed("GREEN")
                        .stockName(stockName)
                        .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
                        .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
                        .build());
                }
                //TODO will uncomment later
//                CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName);
//                if (fiveDatHighLowData.get("fiveDayHigh")){
//                    notificationData.put("stockIsGreen", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Stock " + stockName + " is green last 3 days, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "GREEN: DEMA_9_5 This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
//            }else if((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount)
//                    && !(ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount)){
            }
            if((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <= maxEmaGreenRedCheckCount)){
                if (StockUtil.checkCandleConditions(stockName, "GREEN") && !StockUtil.isCrossOverHappenWithinDays(stockName, "GREEN",3)
                && ifAlreadyNotMoved10Percent) {
                    StockDetails sd = StockDetails.builder()
                            .isGreenRed("GREEN")
                            .stockName(stockName)
                            .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
                            .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
                            .build();
                    StockUtil.addStockDetailsToSendMailForEMA8And3(sd);
                }
                //TODO will uncomment later
//                CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName);
//                if (fiveDatHighLowData.get("fiveDayHigh")){
//                    notificationData.put("stockIsGreen", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Stock " + stockName + " is green last 3 days, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "GREEN: EMA_8_3 This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
            }
        }
//        if (marketData.get("marketMovement").equals("Green") && ema8_3_stockIsGreen >= 1 && ema8_3_stockIsGreen <3){
//            CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName);
//            if (fiveDatHighLowData.get("fiveDayHigh")){
//                notificationData.put("stockIsGreen", "true");
//                notificationData.put("stockName", stockName);
//                String msg = "Stock " + stockName + " is green last 3 days, Have a look once.";
//                notificationData.put("msg", msg);
//                String subject = "GREEN: This is " + stockName + " Stock Alert.....";
//                notificationData.put("subject", subject);
//            }
//        }
        if (marketData.get("marketMovement").equals("Red")
//                && Boolean.parseBoolean(isVolumeHigh.get("isVolumeHigh"))
                && !isCloseLessThanPreviousDay.get("isRed")
                && ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount)
//                ||(ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount)
                && marketMoveDiff
                && isCandleHeadTailLooksGood
                && !isCloseLessThanPreviousDay.get("isRed")
        )){
            if (disableBothList && (ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount) && (ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount)){
                StockUtil.addStockDetailsToSendMailForBothIndicator(StockDetails.builder()
                        .isGreenRed("RED")
                        .stockName(stockName)
                        .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
                        .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
                        .build());
                //TODO will uncomment later
//                CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(stockName);
//                if (fiveDatHighLowData.get("fiveDayLow")){
//                    notificationData.put("stockIsRed", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Stock " + stockName + " is RED last 3 days, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "RED: BOTH This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
//            }else if ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount)
//                    && !(ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount)){
            }
            if ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <=maxEmaGreenRedCheckCount)){
                if (StockUtil.checkCandleConditions(stockName, "RED") && !StockUtil.isCrossOverHappenWithinDays(stockName, "RED",3)) {
                    StockUtil.addStockDetailsToSendMailForEMA8And3(StockDetails.builder()
                            .isGreenRed("RED")
                            .stockName(stockName)
                            .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
                            .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
                            .build());
                }
                //TODO will uncomment later
//                CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(stockName);
//                if (fiveDatHighLowData.get("fiveDayLow")){
//                    notificationData.put("stockIsRed", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Stock " + stockName + " is RED last 3 days, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "RED: EMA_8_3 This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
//            }else if (!(ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount)
//                    && (ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount)){
            }
            if ((ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount)){
                if (StockUtil.checkCandleConditions(stockName, "RED") && !StockUtil.isCrossOverHappenWithinDays(stockName, "RED",3)) {
                    StockUtil.addStockDetailsToSendMailForDEMA9And5(StockDetails.builder()
                            .isGreenRed("RED")
                            .stockName(stockName)
                            .volume(Integer.parseInt(isVolumeHigh.get("todaysVolume")))
                            .highVolumeCompareDays(Integer.parseInt(isVolumeHigh.get("compareDays")))
                            .build());
                }
                //TODO will uncomment later
//                CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(stockName);
//                if (fiveDatHighLowData.get("fiveDayLow")){
//                    notificationData.put("stockIsRed", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Stock " + stockName + " is RED last 3 days, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "RED: DEMA_9_5 This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
            }
        }
//        if (marketData.get("marketMovement").equals("Red") && ema8_3_stockIsRed >= 1 && ema8_3_stockIsRed <3){
//            CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(stockName);
//            if (fiveDatHighLowData.get("fiveDayLow")){
//                notificationData.put("stockIsRed", "true");
//                notificationData.put("stockName", stockName);
//                String msg = "Stock " + stockName + " is RED last 3 days, Have a look once.";
//                notificationData.put("msg", msg);
//                String subject = "RED: This is " + stockName + " Stock Alert.....";
//                notificationData.put("subject", subject);
//            }
//        }
    }

    private static boolean isHigherVolumeThannDays(String stockName, int days) {
        boolean isHigher = true;
        List<String[]> data = loadStockData(stockName);
        int vol = Integer.parseInt(data.get(0)[6]);
        for (int i=1; i<=days; i++){
            if (vol < Integer.parseInt(data.get(i)[6]))
                isHigher = false;
        }
        return isHigher;
    }

    public static Map<String, Object> checkStockGreenOrRed(List<String[]> allData, String stockName, List<String[]> stockHData) {
        LinkedList<String> linkedList = new LinkedList();
        LinkedList<String> ema30_9_linkedList = new LinkedList();
        LinkedList<String> ema9_5_linkedList = new LinkedList();
        LinkedList<String> ema8_3_linkedList = new LinkedList();
        Map<String, Object> marketData = new HashMap<>();
        boolean greenContinue = true;
        boolean redContinue = true;
        boolean ema30_9_redContinue = true;
        boolean ema30_9_greenContinue = true;
        boolean ema9_5_redContinue = true;
        boolean ema9_5_greenContinue = true;
        boolean ema8_3_redContinue = true;
        boolean ema8_3_greenContinue = true;
        int stockIsGreen = 0;
        int stockIsRed = 0;
        int ema30_9_stockIsGreen = 0;
        int ema30_9_stockIsRed = 0;
        int ema9_5_stockIsGreen = 0;
        int ema9_5_stockIsRed = 0;
        int ema8_3_stockIsGreen = 0;
        int ema8_3_stockIsRed = 0;
        boolean Dema30_9_green  = false;
        boolean Dema30_9_red = false;
        boolean Dema9_5_green = false;
        boolean Dema9_5_red = false;
        boolean ema8_3_green = false;
        boolean ema8_3_red = false;
        String marketMovement = "";
        int firstIndex = 0;
        if (StockUtil.checkStockToRun(stockName)){
            for (String[] data : allData) {
                if (!data[1].equals("null")) {
                    double ema30 = 0.0;
                    double ema9 = 0.0;
                    double ema5 = 0.0;
                    double ema8 = 0.0;
                    double ema3 = 0.0;
                    if (firstIndex == 0 && StockPropertiesUtil.booleanIndicators.get("checkTradeWithoutDoublePrecision")) {
                        if(StockUtil.checkNewAddedstock(stockName)) {
                            ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                            ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                            ema30 = 0;
                            ema9 = 0;
                            ema5 = 0;
                        }else{
                        if (StockUtil.checkOnly83(stockName)){
                            ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                            ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                        }else {
                            ema30 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                            ema9 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                            ema5 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[2]));
                            ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[3]));
                            ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[4]));
                        }}
                    }else{
                        if(StockUtil.checkNewAddedstock(stockName)) {
                            ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                            ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                            ema30 = 0;
                            ema9 = 0;
                            ema5 = 0;
                        }else{
                        if (StockUtil.checkOnly83(stockName)) {
                            ema8 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                            ema3 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                        }else {
                            ema30 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                            ema9 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                            ema5 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[2]));
                            ema8 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[3]));
                            ema3 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[4]));
                        }}
                    }
                    checkConditionsAndUpdateList(ema30, ema9, ema5, ema8, ema3, ema30_9_linkedList, ema9_5_linkedList, ema8_3_linkedList, linkedList, stockHData);
//                    if(ema30 < ema9 || ema9 < ema5 || ema8 > ema3){
//                        linkedList.add("G");
//                    }else if (ema30 > ema9 || ema9 > ema5 || ema8 < ema3){
//                        linkedList.add("R");
//                    }
                }
                if (firstIndex==15)
                    break;
                firstIndex++;
            }
        }else {
            for (String[] data : allData) {
                if (!data[1].equals("null")) {
                    if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]))
                            < StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]))) {
                        linkedList.add("G");
                    } else {
                        linkedList.add("R");
                    }
                }
            }
        }
//        for (int i=0; i<linkedList.size();i++){
//            if(linkedList.get(i).equals("R") && redContinue){
//                if (stockIsGreen!=0)
//                    greenContinue = false;
//                if (StringUtils.isEmpty(marketMovement))
//                    marketMovement = "Red";
//                stockIsRed++;
//            }else if (linkedList.get(i).equals("G") && greenContinue){
//                if (stockIsRed!=0)
//                    redContinue = false;
//                if (StringUtils.isEmpty(marketMovement))
//                    marketMovement = "Green";
//                stockIsGreen++;
//            }
//        }

        for (int i=0; i<ema8_3_linkedList.size();i++){
            if(ema8_3_linkedList.get(i).equals("R") && ema8_3_redContinue && !ema8_3_red){
                if (ema8_3_stockIsGreen!=0)
                    ema8_3_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema8_3_stockIsRed++;
                ema8_3_green = true;
            }else if (ema8_3_linkedList.get(i).equals("G") && ema8_3_greenContinue && !ema8_3_green){
                if (ema8_3_stockIsRed!=0)
                    ema8_3_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema8_3_stockIsGreen++;
                ema8_3_red = true;
            }else if(ema8_3_linkedList.get(i).equals("R") && ema8_3_stockIsGreen != 0)
                break;
            else if(ema8_3_linkedList.get(i).equals("G") && ema8_3_stockIsRed != 0)
                break;
        }

        for (int i=0; i<ema9_5_linkedList.size();i++){
            if(ema9_5_linkedList.get(i).equals("R") && ema9_5_redContinue && !Dema9_5_red){
                if (ema9_5_stockIsGreen!=0)
                    ema9_5_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema9_5_stockIsRed++;
                Dema9_5_green = true;
            }else if (ema9_5_linkedList.get(i).equals("G") && ema9_5_greenContinue && !Dema9_5_green){
                if (ema9_5_stockIsRed!=0)
                    ema9_5_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema9_5_stockIsGreen++;
                Dema9_5_red = true;
            }else if(ema9_5_linkedList.get(i).equals("R") && ema9_5_stockIsGreen != 0)
                break;
            else if(ema9_5_linkedList.get(i).equals("G") && ema9_5_stockIsRed != 0)
                break;
        }
        for (int i=0; i<ema30_9_linkedList.size();i++){
            if(ema30_9_linkedList.get(i).equals("R") && ema30_9_redContinue && !Dema30_9_red){
                if (ema30_9_stockIsGreen!=0)
                    ema30_9_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema30_9_stockIsRed++;
                Dema30_9_green = true;
            }else if (ema30_9_linkedList.get(i).equals("G") && ema30_9_greenContinue && !Dema30_9_green){
                if (ema30_9_stockIsRed!=0)
                    ema30_9_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema30_9_stockIsGreen++;
                Dema30_9_red = true;
            }else if(ema30_9_linkedList.get(i).equals("R") && ema30_9_stockIsGreen != 0)
                break;
            else if(ema30_9_linkedList.get(i).equals("G") && ema30_9_stockIsRed != 0)
                break;
        }

        marketData.put("marketMovement",marketMovement);
        marketData.put("stockIsGreen",stockIsGreen);
        marketData.put("stockIsRed",stockIsRed);
        marketData.put("ema8_3_stockIsGreen",ema8_3_stockIsGreen);
        marketData.put("ema8_3_stockIsRed",ema8_3_stockIsRed);
        marketData.put("ema9_5_stockIsGreen",ema9_5_stockIsGreen);
        marketData.put("ema9_5_stockIsRed",ema9_5_stockIsRed);
        marketData.put("ema30_9_stockIsGreen",ema30_9_stockIsGreen);
        marketData.put("ema30_9_stockIsRed",ema30_9_stockIsRed);
        return marketData;
    }

    private static void checkConditionsAndUpdateList(double ema30, double ema9, double ema5, double ema8, double ema3,
                                                     LinkedList<String> ema30_9_linkedList, LinkedList<String> ema9_5_linkedList,
                                                     LinkedList<String> ema8_3_linkedList, LinkedList<String> linkedList, List<String[]> stockHData) {
        if (ema30<ema9){
            ema30_9_linkedList.add("G");
        }else if (ema30 > ema9){
            ema30_9_linkedList.add("R");
        }
        if (ema9 < ema5){
            ema9_5_linkedList.add("G");
        }else if (ema9 > ema5){
            ema9_5_linkedList.add("R");
        }else if (ema9==ema5){
            String[] sdata = stockHData.get(ema9_5_linkedList.size());
            if (ema9_5_linkedList.size()==0){
                if (Double.parseDouble(sdata[1]) <= Double.parseDouble(sdata[4])) {
                    ema9_5_linkedList.add("G");
                }else
                    ema9_5_linkedList.add("R");
            }else{
                ema9_5_linkedList.add(ema9_5_linkedList.get(ema9_5_linkedList.size()-1));
            }
        }
        if (ema8 > ema3){
            ema8_3_linkedList.add("R");
        }else if (ema8 < ema3){
            ema8_3_linkedList.add("G");
        }else if (ema8 == ema3){
            String[] sdata = stockHData.get(ema8_3_linkedList.size());
            if (ema8_3_linkedList.size()==0){
                if (Double.parseDouble(sdata[1]) <= Double.parseDouble(sdata[4])) {
                    ema8_3_linkedList.add("G");
                }else
                    ema8_3_linkedList.add("R");
            }else{
                ema8_3_linkedList.add(ema8_3_linkedList.get(ema8_3_linkedList.size()-1));
            }
        }

    }

    private static Map<String,Boolean> checkBreakLastFiveDaysHighLow(String stockName) {
        boolean breakHigh = false;
        Map<String, Boolean> fiveDayHighLow = new HashMap<>();
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("fiveDayHighLowCheckIndicator")) {
            double todaysClose = 0.0;
            double fiveDayHigh = 0.0;
            double fiveDayLow = 0.0;
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\" + stockName + ".csv");
            try {
                FileReader filereader = new FileReader(path.toString());
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(1)
                        .build();
                List<String[]> allData = csvReader.readAll();
                Collections.reverse(allData);
                if (!allData.get(0)[4].equals("null")) {
                    todaysClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(0)[4]));
                }
                double hc;
                for (int i = 1; i <= 5; i++) {
                    if (!allData.get(i)[4].equals("null")) {
                        if (!allData.get(i)[4].equals("null")) {
                            if (Double.parseDouble(allData.get(i)[4]) < Double.parseDouble(allData.get(i)[1])) {
                                hc = Double.parseDouble(allData.get(i)[1]);
                            } else {
                                hc = Double.parseDouble(allData.get(i)[4]);
                            }
                            if (fiveDayHigh < StockUtil.convertDoubleToTwoPrecision(hc))
                                fiveDayHigh = StockUtil.convertDoubleToTwoPrecision(hc);
                            if (fiveDayLow ==0 || fiveDayLow > StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(i)[3])))
                                fiveDayLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(i)[3]));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (todaysClose >= fiveDayHigh) {
                fiveDayHighLow.put("fiveDayHigh", true);
            } else {
                fiveDayHighLow.put("fiveDayHigh", false);
            }
            if (todaysClose <= fiveDayLow) {
                fiveDayHighLow.put("fiveDayLow", true);
            } else {
                fiveDayHighLow.put("fiveDayLow", false);
            }
        }else{
            fiveDayHighLow.put("fiveDayHigh", true);
            fiveDayHighLow.put("fiveDayLow", true);
        }

        return fiveDayHighLow;
    }

    public static Map<String, String> readEmaBuyStok(String stockEmaDataLoad, String stockName) {
        Map<String, String> notificationData = new HashMap<>();
        try {
            File file = new File(stockEmaDataLoad);
            if (!file.exists())
                file.createNewFile();
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            String[] data = allData.get(0);
            if (!data[1].equals("null")) {
                if ((int)StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[3])) >=
                        (int)StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[4]))){
                    notificationData.put("stockIsRed", "true");
                    notificationData.put("stockName", stockName);
                    String msg = "Your Buy Stock " + stockName + "'s EMA is RED, Have a look once.";
                    notificationData.put("msg", msg);
                    String subject = "RED: This is " + stockName + " Stock Alert.....";
                    notificationData.put("subject", subject);
                }
//                if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]))
//                        >= StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]))) {
//                    notificationData.put("stockIsRed", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Your Buy Stock " + stockName + "'s EMA is RED, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "RED: This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return notificationData;
    }


    public static Map<String, Double> readPreviousDayEmaModify(String stockEmaDataLoad, String stockName) {
        {
            Map<String, Double> yesterdayEMA = new HashMap<>();
            try {
                File file = new File(stockEmaDataLoad);
                if (!file.exists())
                    file.createNewFile();
                FileReader filereader = new FileReader(file);

                CSVReader csvReader = new CSVReaderBuilder(filereader)
//                        .withSkipLines(1)
                        .build();
                List<String[]> allData = csvReader.readAll();
                if (allData.size() != 0){
                    String[] data = allData.get(0);
                    String[] data1 = allData.get(1);
                    if(data.length == 6){
                        yesterdayEMA.put("EMA8", Double.parseDouble(data1[0]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data1[1]));
                        yesterdayEMA.put("EMA26", Double.parseDouble(data1[2]));
                        yesterdayEMA.put("EMA12",Double.parseDouble(data1[3]));
                    }else if(data.length == 4 && data[data.length-1].equals("MA5")){
                        yesterdayEMA.put("EMA8", Double.parseDouble(data1[0]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data1[1]));
                        yesterdayEMA.put("EMA26", 0.0);
                        yesterdayEMA.put("EMA12", 0.0);
                    }else if(data.length == 4 && data[data.length-1].equals("EMA12")){
                        yesterdayEMA.put("EMA8", Double.parseDouble(data1[0]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data1[1]));
                        yesterdayEMA.put("EMA26", Double.parseDouble(data1[2]));
                        yesterdayEMA.put("EMA12", Double.parseDouble(data1[3]));
                    }else if (data.length == 2){
                        yesterdayEMA.put("EMA8", Double.parseDouble(data1[0]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data1[1]));
                        yesterdayEMA.put("EMA26", 0.0);
                        yesterdayEMA.put("EMA12", 0.0);
                    }
                }else {
                    yesterdayEMA.put("EMA8", 0.0);
                    yesterdayEMA.put("EMA3", 0.0);
                    yesterdayEMA.put("EMA26", 0.0);
                    yesterdayEMA.put("EMA12", 0.0);
                    yesterdayEMA.put("MA100",0.0);
                    yesterdayEMA.put("MA5",0.0);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return yesterdayEMA;
        }
    }
    public static Map<String, Double> readPreviousDayEma(String stockEmaDataLoad, String stockName) {
        Map<String, Double> yesterdayEMA = new HashMap<>();
        try {
            File file = new File(stockEmaDataLoad);
            if (!file.exists())
                file.createNewFile();
            FileReader filereader = new FileReader(file);

            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            if (StockUtil.checkStockToRun(stockName) || StockUtil.checkStockToRunForMACD(stockName)){
                if (allData.size() != 0) {
                    String[] data = allData.get(0);
                    if (data.length == 5) {
                        yesterdayEMA.put("DEMA30", Double.parseDouble(data[0]));
                        yesterdayEMA.put("DEMA9", Double.parseDouble(data[1]));
                        yesterdayEMA.put("DEMA5", Double.parseDouble(data[2]));
                        yesterdayEMA.put("EMA8", Double.parseDouble(data[3]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data[4]));
                    }else if (data.length == 7) {
                        yesterdayEMA.put("DEMA30", Double.parseDouble(data[0]));
                        yesterdayEMA.put("DEMA9", Double.parseDouble(data[1]));
                        yesterdayEMA.put("DEMA5", Double.parseDouble(data[2]));
                        yesterdayEMA.put("EMA8", Double.parseDouble(data[3]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data[4]));
                        yesterdayEMA.put("MA100",Double.parseDouble(data[5]));
                        yesterdayEMA.put("MA5",Double.parseDouble(data[6]));
                    }else if(data.length == 4){
                        yesterdayEMA.put("EMA8", Double.parseDouble(data[0]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data[1]));
                        yesterdayEMA.put("EMA26", Double.parseDouble(data[2]));
                        yesterdayEMA.put("EMA12", Double.parseDouble(data[3]));
                    }else if(data.length == 6){
                        yesterdayEMA.put("EMA8", Double.parseDouble(data[0]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data[1]));
                        yesterdayEMA.put("EMA26", Double.parseDouble(data[2]));
                        yesterdayEMA.put("EMA12", Double.parseDouble(data[3]));
                        yesterdayEMA.put("MA100",Double.parseDouble(data[4]));
                        yesterdayEMA.put("MA5",Double.parseDouble(data[5]));
                    }else {
                        yesterdayEMA.put("EMA8", Double.parseDouble(data[0]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data[1]));
                        yesterdayEMA.put("DEMA30", 0.0);
                        yesterdayEMA.put("DEMA9", 0.0);
                        yesterdayEMA.put("DEMA5", 0.0);
                    }
                } else {
                    yesterdayEMA.put("DEMA30", 0.0);
                    yesterdayEMA.put("DEMA9", 0.0);
                    yesterdayEMA.put("DEMA5", 0.0);
                    yesterdayEMA.put("EMA8", 0.0);
                    yesterdayEMA.put("EMA3", 0.0);
                }
            }else {
                if (allData.size() != 0) {
                    String[] data = allData.get(0);
                    if (StockUtil.checkOnly83(stockName)){
                        yesterdayEMA.put("EMA8", Double.parseDouble(data[0]));
                        yesterdayEMA.put("EMA3", Double.parseDouble(data[1]));
                    }else {
                        yesterdayEMA.put("EMA30", Double.parseDouble(data[0]));
                        yesterdayEMA.put("EMA9", Double.parseDouble(data[1]));
                    }
                } else {
                    yesterdayEMA.put("EMA30", 0.0);
                    yesterdayEMA.put("EMA9", 0.0);
                    yesterdayEMA.put("EMA8", 0.0);
                    yesterdayEMA.put("EMA3", 0.0);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return yesterdayEMA;
    }

    public static Map<String, String> readPreviousdayClose(String path, String stockName) {
        Map<String, String> notificationCloseData = new HashMap<>();
        try {
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            Collections.reverse(allData);
            String[] todaysData = allData.get(0);
            String[] yesDayData = null;
            for (int i=1;i < allData.size()-1; i++){
                if(!allData.get(i)[4].equals("null")){
                    yesDayData = allData.get(i);
                    break;
                }
            }
            if (!todaysData[4].equals("null")) {
//                double yesDayOpenCloseLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[4])) < StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[1])) ?
//                        StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[4])) : StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[1]));
                double yesDayLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[3])) - 1;
                if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysData[4]))
                        < yesDayLow) {
                    notificationCloseData.put("isRedToday", "true");
                    notificationCloseData.put("stockName", stockName);
                    String msg = "Your Buy Stock " + stockName + "'s Today's close down, Have a look once.";
                    notificationCloseData.put("msg", msg);
                    String subject = "RED: This is " + stockName + " Stock Alert.....";
                    notificationCloseData.put("subject", subject);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return notificationCloseData;
    }

    public static double convertDoubleToTwoPrecision(double price) {
        return Double.parseDouble(df.format(price));
    }

    public static double roundUpBasedOnPrecision(double price) {
        DecimalFormat decfor = new DecimalFormat("0.00");
        decfor.setRoundingMode(RoundingMode.DOWN);
        int number = (int) price;
        double decimal = price - number;
        if (decimal >= 0.50)
            number++;
        return number;
    }

    //we can return true when we did not find any stock to trade after filter this
    public static boolean extraCheckToBuyOrNot(String stockName){
        boolean flag = false;
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("extraCheckIfPreviousMonthSupportIndicator")) {
            double open = 0.0;
            double high = 0.0;
            double low = 0.0;
            double close = 0.0;
            double prevOpen = 0.0;
            double prevHigh = 0.0;
            double prevLow = 0.0;
            double prevClose = 0.0;
            double highDiff = 0.0;
            double lowDiff = 0.0;
            long todaysMovePercent = 0;
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\" + stockName + ".csv");
            try {
                FileReader filereader = new FileReader(path.toString());
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(1)
                        .build();
                List<String[]> allData = csvReader.readAll();
                Collections.reverse(allData);
                close = Double.parseDouble(allData.get(0)[4]);
                open = Double.parseDouble(allData.get(0)[1]);
                high = Double.parseDouble(allData.get(0)[2]);
                low = Double.parseDouble(allData.get(0)[3]);
                prevClose = Double.parseDouble(allData.get(0)[4]);
                prevOpen = Double.parseDouble(allData.get(0)[1]);
                prevHigh = Double.parseDouble(allData.get(0)[2]);
                prevLow = Double.parseDouble(allData.get(0)[3]);
                if (open < close) {
                    highDiff = high - close;
                    lowDiff = open - low;
                    todaysMovePercent = Math.round(((close - open) / prevClose) * 100);
                } else {
                    highDiff = high - open;
                    lowDiff = close - low;
                }
                if (prevHigh < low || (highDiff < lowDiff || todaysMovePercent > 3)) {
                    flag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
            flag = true;
        return flag;
    }

    public static List<String[]> loadEmaData(String stockName) {
        List<String[]> allData = new ArrayList<>();
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
        try {
            FileReader filereader = new FileReader(path.toString());
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            allData = csvReader.readAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        return allData;
    }

    public static void deleteRecordFromEmaData(String stockName, int rowCount) {
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
        File file = new File(path.toString());
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            FileReader filereader = new FileReader(file);

            CSVReader csvReader = new CSVReaderBuilder(filereader).build();
            List<String[]> allData = csvReader.readAll();
            FileWriter outputfile = new FileWriter(file, false);
            CSVWriter writer = new CSVWriter(outputfile);
            String[] header = allData.get(0);
            allData.remove(0);
            for (int i=0; i<rowCount; i++)
                allData.remove(i);
            allData.add(0,header);
            for (String[] dt : allData){
                writer.writeNext(dt);
            }
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateExceutiondate() {
        Path filePath = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\job_run_date\\daily.txt");
        File file = new File(filePath.toString());
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            FileReader filereader = new FileReader(file);
//
            CSVReader csvReader = new CSVReaderBuilder(filereader).build();
            List<String[]> allData = csvReader.readAll();
            FileWriter outputfile = new FileWriter(file, false);
            CSVWriter writer = new CSVWriter(outputfile);
            String [] dt = new String[2];
            dt[0] = "Execution date : ";
            dt[1] = new Date().toString();
            allData.add(dt);
            for(String[] d : allData) {
                writer.writeNext(d);
            }
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> loadStockData(String stockName) {
        List<String[]> allData = new ArrayList<>();
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
        try {
            FileReader filereader = new FileReader(path.toString());
            CSVReader csvReader = new CSVReaderBuilder(filereader)
//                    .withSkipLines(1)
                    .build();
            allData = csvReader.readAll();
            Collections.reverse(allData);
            filereader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return allData;
    }

    public static List<String[]> loadStockDataUsingPath(String path) {
        List<String[]> allData = new ArrayList<>();
        try {
            FileReader filereader = new FileReader(path);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
//                    .withSkipLines(1)
                    .build();
            allData = csvReader.readAll();
            Collections.reverse(allData);
            filereader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return allData;
    }

    public static boolean isExecutionDataAvailableCorrect() {
        int reduceDay = -1;
        boolean flag = true;
        Set<String> stocks = loadAllStockNames();
        List<String[]> datas = loadStockData(stocks.toArray()[1].toString());
        Date myDate = new Date();
        Format f = new SimpleDateFormat("EEEE");
        String str = f.format(new Date());
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(cal.DATE, -1);
        if (str.equals("Monday")){
            cal.add(cal.DATE, -2);
        }
        String todayDate = sm.format(cal.getTime());
        System.out.println("System Date : "+ todayDate);
        System.out.println("History Data Date : "+ datas.get(0)[0]);
        if (todayDate.equals(datas.get(0)[0])){
            flag = true;
        }
        return flag;
    }

    public static boolean checkDateAnddata(String dt) {
        boolean flag = false;
        List<String[]> stockData = StockUtil.loadStockData("^NSEI");
        String[] stockToddData = stockData.get(0);
        String sDate = stockToddData[0];
        try {
            if ((new SimpleDateFormat("yyyy-MM-dd").parse(dt).getTime() - new SimpleDateFormat("yyyy-MM-dd").parse(sDate).getTime() != 0)
            || stockToddData[1]== "null")
                flag = true;
        }catch (Exception e){
            System.out.println("Check Data is old not new Data");
            flag = false;
        }
        return flag;
    }

    public static Set<String> loadIndexStockNames() {
        String[] keys = {"index_list"};
        Set<String> list = new HashSet<>();
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(String key : keys){
            list.addAll(Arrays.asList(p.getProperty(key).split(",")));
        }

        return list;
    }

    public static List<String[]> readFileData(String filePath) {
        List<String[]> allData = null;
        try {
            File file = new File(filePath);
            if (!file.exists())
                file.createNewFile();
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .build();
            allData = csvReader.readAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        return allData;
    }

    public static String getDateWithFormat(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dt = "";
         try{
         dt = sdf.format(sdf.parse(date));
         }catch (Exception e){
             dt = sdf.format(new Date());
         }
        return dt;
    }

    public static String getDateWithFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date getDateFromString(String date, String format) {
        Date dt = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            dt = sdf.parse(date);
        }catch (Exception e){
            System.out.println("unparsable date: "+date+" to this format "+format);
        }
        return dt;
    }

    public static void loadStockHistoryData(String stockName, long startDate, long endDate) {
        String baseUrl = "https://query1.finance.yahoo.com/v7/finance/download/";
        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        url.append(stockName);
        url.append("?");
        url.append("period1="+startDate);
        url.append("&period2="+endDate);
        url.append("&interval=1mo&events=history&includeAdjustedClose=true");
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\temp\\"+stockName+".csv");
        try {
            File fl = new File(path.toString());
            if (!fl.exists()) {
                fl.createNewFile();
            }
        }catch (Exception e)
        {e.printStackTrace();}
        try (BufferedInputStream in = new BufferedInputStream(new URL(url.toString()).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<String> loadOptionStockNames() {
        String[] keys = {"option_stock_symbol"};
        Set<String> list = new HashSet<>();
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\option_stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(String key : keys){
            list.addAll(Arrays.asList(p.getProperty(key).split(",")));
        }

        return list;
    }

    public static Map<String, Object> checkOptionIndexTradeEligibility(List<String[]> emaDatas, String stockName) {
        LinkedList<String> optionIndexEligibleData = new LinkedList<>();
        for (int i=1; i<emaDatas.size();i++){
            String[] currEma = emaDatas.get(i-1);
            String[] prevEma = emaDatas.get(i);
            double currEma30 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(currEma[0]));
            double currEma9 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(currEma[1]));
            double currEma5 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(currEma[2]));

            double prevEma30 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(prevEma[0]));
            double prevEma9 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(prevEma[1]));
            double prevEma5 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(prevEma[2]));
//            if (currEma30 < currEma9 || )
        }
        return null;
    }

    public static long daysGapInTwoDates(String today, String s) {
        long daysDiff = 0;
        try {
            Date resDay = new SimpleDateFormat("dd MMMM yyyy").parse(s);
            Date tody = new SimpleDateFormat("dd MMMM yyyy").parse(today);
            long dateBeforeInMs = tody.getTime();
            long dateAfterInMs = resDay.getTime();
            long timeDiff = Math.abs(dateAfterInMs - dateBeforeInMs);
            daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            if (daysDiff < 0)
                daysDiff = daysDiff * -1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return daysDiff;
    }

    public static long getDaysDiff(Date d1, Date d2){
        long difference_In_Time
                = d2.getTime() - d1.getTime();
        long difference_In_Days
                = (difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365;
        return difference_In_Days;
    }

    public static boolean checkStockToRun(String stockName) {
        boolean flag = false;
        if (
//                stockName.equals("^NSEI") || stockName.equals("^NSEBANK") ||
                        stockName.equals(")")
                || Character.compare(stockName.charAt(0),'D')==0 || Character.compare(stockName.charAt(0),'E')==0
                || Character.compare(stockName.charAt(0),'C')==0 || Character.compare(stockName.charAt(0),'B')==0
                || Character.compare(stockName.charAt(0),'A')==0
//                || Character.compare(stockName.charAt(0),'3')==0
//                                || Character.compare(stockName.charAt(0),'5')==0
//                || Character.compare(stockName.charAt(0),'2')==0
//                || Character.compare(stockName.charAt(0),'6')==0
                )
            flag = true;
        return flag;
    }

    public static boolean checkStockToRunForMACD(String stockName) {
        boolean flag = false;
        if (stockName.equals("^NSEI") || stockName.equals("^NSEBANK")
//                || stockName.equals(")")
//                || Character.compare(stockName.charAt(0),'D')==0 || Character.compare(stockName.charAt(0),'E')==0
//                || Character.compare(stockName.charAt(0),'C')==0 || Character.compare(stockName.charAt(0),'B')==0
//                || Character.compare(stockName.charAt(0),'A')==0
//                || Character.compare(stockName.charAt(0),'3')==0
//                || Character.compare(stockName.charAt(0),'5')==0
//                || Character.compare(stockName.charAt(0),'2')==0
//                || Character.compare(stockName.charAt(0),'6')==0
        )
            flag = true;
        return flag;
    }
    public static boolean checkNewAddedstock(String stockName) {
        boolean flag = false;
        if (stockName.equals("^NSEI") || stockName.equals("^NSEBANK")
                || Character.compare(stockName.charAt(0),'Z')==0
                || Character.compare(stockName.charAt(0),'Y')==0
                || Character.compare(stockName.charAt(0),'X')==0
                || Character.compare(stockName.charAt(0),'D')==0
                || Character.compare(stockName.charAt(0),'C')==0
                || Character.compare(stockName.charAt(0),'B')==0
                || Character.compare(stockName.charAt(0),'A')==0
                || Character.compare(stockName.charAt(0),'3')==0
                || Character.compare(stockName.charAt(0),'5')==0
                || Character.compare(stockName.charAt(0),'2')==0
                || Character.compare(stockName.charAt(0),'6')==0
        )
            flag = true;
        return flag;
    }

    public static boolean checkOnly83(String stockName) {
        boolean flag = false;
        if (Character.compare(stockName.charAt(0),'A')==0 || Character.compare(stockName.charAt(0),'3')==0
        || Character.compare(stockName.charAt(0),'5')==0 || Character.compare(stockName.charAt(0),'2')==0
        || Character.compare(stockName.charAt(0),'6')==0)
            flag = true;
        return flag;
    }

    public static List<StockDetails> getListStockDetailsToSendMailForBothIndicator(){
        return  listStockDetailsToSendMailForBothIndicator;
    }

    public static List<StockDetails> getListStockDetailsOfCross(){
        return  listStockDetailsToSendMailFor83IndOnlyUpDown;
    }

    public static List<StockDetails> getListTrendStockDetails(){
        return  listTrendStocks;
    }

    public static void addStockDetailsToSendMailForBothIndicator(StockDetails sd) {
        listStockDetailsToSendMailForBothIndicator.add(StockDetails.builder()
                        .stockName(sd.getStockName())
                        .isGreenRed(sd.getIsGreenRed())
                        .volume(sd.getVolume())
                        .build());
    }

    public static void addStockDetailsToSendMailFor83IndOnlyUpDown(StockDetails sd) {
        listStockDetailsToSendMailFor83IndOnlyUpDown.add(StockDetails.builder()
                .stockName(sd.getStockName())
                .isGreenRed(sd.getIsGreenRed())
                .volume(sd.getVolume())
                .highVolumeCompareDays(sd.getHighVolumeCompareDays())
                .build());
    }

    public static void addTrensStockDetails(StockDetails sd) {
        listTrendStocks.add(StockDetails.builder()
                .stockName(sd.getStockName())
                .isGreenRed(sd.getIsGreenRed())
                .volume(sd.getVolume())
                .highVolumeCompareDays(sd.getHighVolumeCompareDays())
                .trendDays(sd.getTrendDays())
                .build());
    }

    public static List<StockDetails> getListStockDetailsToSendMailForEMA8And3(){
        return  listStockDetailsToSendMailForEMA8And3;
    }

    public static void addStockDetailsToSendMailForEMA8And3(StockDetails sd) {
        listStockDetailsToSendMailForEMA8And3.add(StockDetails.builder()
                .stockName(sd.getStockName())
                .isGreenRed(sd.getIsGreenRed())
                .volume(sd.getVolume())
                .highVolumeCompareDays(sd.getHighVolumeCompareDays())
                .build());
    }

    public static List<StockDetails> getListStockDetailsToSendMailForDEMA9And5(){
        return  listStockDetailsToSendMailForDEMA9And5;
    }

    public static void addStockDetailsToSendMailForDEMA9And5(StockDetails sd) {
        listStockDetailsToSendMailForDEMA9And5.add(StockDetails.builder()
                .stockName(sd.getStockName())
                .isGreenRed(sd.getIsGreenRed())
                .volume(sd.getVolume())
                .build());
    }

    public static Map<String, String> getStoCKTradeDetailsAndPrepareNotificationMessage() {
        Map<String, Object> tradeStockDetails = new HashMap<>();
        Map<String, String> mailNotification = new HashMap<>();
        checkAndValidateStockdataAndSetNotification(tradeStockDetails);
        List<StockDetails> stockEma8And3 = (List<StockDetails>) tradeStockDetails.get("stockEma8And3");
//        List<StockDetails> stockDEma9And5 = (List<StockDetails>) tradeStockDetails.get("stockDEma9And5");
//        List<StockDetails> stockBothIndicator = (List<StockDetails>) tradeStockDetails.get("stockBothIndicator");

        List<StockDetails> stockCrossDetails = (List<StockDetails>) tradeStockDetails.get("stockDetailsOfCross");
        System.out.println("-------------will do some exgtra check-----------------");
//        System.out.println("Ema83");
//        for (StockDetails ss : stockEma8And3)
//            System.out.println(ss.toString());
//        System.out.println("Dma95");
//        for (StockDetails ss : stockDEma9And5)
//            System.out.println(ss.toString());
//        System.out.println("Both");
//        for (StockDetails ss : stockBothIndicator)
//            System.out.println(ss.toString());
//        System.out.println("-------------test-----------------");
        mailNotification.put("isStockAvl", "false");
        mailNotification.put("stockSubject","Stock Details To Trade");
//        if (!stockEma8And3.isEmpty() || !stockDEma9And5.isEmpty() || !stockBothIndicator.isEmpty()){
        if (!stockEma8And3.isEmpty()){
            List<StockDetails> list = StockUtil.filterRedFromList(stockEma8And3);
            list = StockUtil.firstCheckList(list, "GREEN");
//            System.out.println(stockBothIndicator);
            System.out.println(stockEma8And3);
//            System.out.println(stockDEma9And5);
            mailNotification.put("isStockAvl", "true");
            mailNotification.put("stockMsg",prepareMessage(list,"EMA83IND"));
        }
//        if (!stockCrossDetails.isEmpty() && (mailNotification.get("stockMsg") != null && mailNotification.get("stockMsg").equals("Msg not available"))) {
//            mailNotification.put("isStockAvl", "true");
//            mailNotification.put("stockMsg",prepareMessage(stockCrossDetails,"onlycrossover"));
//        }
        if (!stockCrossDetails.isEmpty()){
            System.out.println("Checking for duplicate for all 83 cross stock");
            List<StockDetails> updatedStock = new CopyOnWriteArrayList(stockCrossDetails);
            mailNotification.put("isStockAvl", "true");
            for (int i=0; i<stockEma8And3.size(); i++){
                for (StockDetails ss: updatedStock){
                    if(stockCrossDetails.get(i).getStockName().equalsIgnoreCase(ss.getStockName()))
                        updatedStock.remove(updatedStock.indexOf(ss));
                }
            }
//            for (int i=1; i< (stockEma8And3.size() <  stockCrossDetails.size()? stockCrossDetails.size():stockEma8And3.size()); i++) {
//                if (!st.getStockName().equalsIgnoreCase(stockEma8And3.get(i).getStockName())){
//                    updatedStock.add(st);
//                }
//            }
            System.out.println("sorting all stock");
            updatedStock = StockUtil.sortStockDataBasedOnCompareDays(updatedStock);
//            updatedStock = StockUtil.firstCheckList(updatedStock, ""); //TODO need to implement stockDirection
            StringBuilder sb = new StringBuilder();
            sb.append(mailNotification.get("stockMsg") != null ? mailNotification.get("stockMsg") : "");
            String msg = prepareMessage(updatedStock,"onlycrossover");
            sb.append(msg);
            mailNotification.put("stockMsg", sb.toString());
        }
        if (mailNotification.get("stockMsg").isEmpty() || mailNotification.get("stockMsg") == null)
            mailNotification.put("isStockAvl", "false");

//        mailNotification.put("isStockEma8And3Avl", "false");
//        mailNotification.put("isStockDEma9And5Avl", "false");
//        mailNotification.put("isStockBothIndicatorAvl", "false");
//        if (!stockEma8And3.isEmpty()){
//            mailNotification.put("isStockEma8And3Avl", "true");
//            mailNotification.put("stockEma8And3Msg",prepareMessage(stockEma8And3));
//            mailNotification.put("stockEma8And3Subject","Ema8And3 Indicator Stock Details To Trade");
//        }
//        if (!stockDEma9And5.isEmpty()){
//            mailNotification.put("isStockDEma9And5Avl", "true");
//            mailNotification.put("stockDEma9And5Msg",prepareMessage(stockDEma9And5));
//            mailNotification.put("stockDEma9And5Subject","DEma9And5 Indicator Stock Details To Trade");
//        }
//        if (!stockBothIndicator.isEmpty()) {
//            mailNotification.put("isStockBothIndicatorAvl", "true");
//            mailNotification.put("stockBothIndicatorMsg", prepareMessage(stockBothIndicator));
//            mailNotification.put("stockBothIndicatorSubject", "Both Indicator Stock Details To Trade");
//        }

        return mailNotification;
    }

    private static String prepareMessageForAll(List<StockDetails> stockBothIndicator, List<StockDetails> stockDEma9And5, List<StockDetails> stockEma8And3) {
        stockBothIndicator = filterRedFromList(stockBothIndicator);
        stockDEma9And5 = filterRedFromList(stockDEma9And5);
        stockEma8And3 = filterRedFromList(stockEma8And3);
        StringBuilder sb = new StringBuilder();
        if(!stockBothIndicator.isEmpty() && !stockDEma9And5.isEmpty() && !stockEma8And3.isEmpty()) {
            sb.append("-------Both Indicator Stocks------");
            sb.append(System.lineSeparator());
            sb.append(prepareMessage(stockBothIndicator, ""));
            sb.append(System.lineSeparator());
            sb.append("-------9AND5 Indicator Stocks------");
            sb.append(System.lineSeparator());
            sb.append(prepareMessage(stockDEma9And5,""));
            sb.append(System.lineSeparator());
            sb.append("-------8AND3 Indicator Stocks------");
            sb.append(System.lineSeparator());
            sb.append(prepareMessage(stockEma8And3,""));
        }else
            sb.append("Msg not available");
        return sb.toString();
    }

    private static List<StockDetails> filterRedFromList(List<StockDetails> list) {
        List<StockDetails> lst = new ArrayList<>();
        for (StockDetails sd: list){
            if(sd.getIsGreenRed().equalsIgnoreCase("GREEN")){
                lst.add(sd);
            }
//            if(sd.getIsGreenRed().equalsIgnoreCase("RED")){
//                lst.add(sd);
//            }
        }

        return lst;
    }

    private static String prepareMessage(List<StockDetails> stockData, String stockIndicator) {
        StringBuffer sb = new StringBuffer();
        if (!stockIndicator.equalsIgnoreCase("onlycrossover"))
            stockData = filterRedFromList(stockData);
        if (!stockData.isEmpty() && stockIndicator.equalsIgnoreCase("EMA83IND")) {
            sb.append("............EMA83IND..........");
            sb.append(System.lineSeparator());
        }
        if (!stockData.isEmpty() && stockIndicator.equalsIgnoreCase("onlycrossover")) {
            sb.append("............onlycrossover..........");
            sb.append(System.lineSeparator());
        }
        for (StockDetails sd: stockData) {
//            CalculateProfitAndStoreJob.addStockDataForProfitCalculate(sd.getStockName());
            sb.append(sd.toString());
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    public static boolean checkIfStockIsSideWays(String stockName, List<String[]> sData, int daysToCalculateRSI){
        boolean flag = true;
        String rsi_data_loc = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_rsi\\"+daysToCalculateRSI+"days_rsi\\"+stockName;
        File fl = new File(rsi_data_loc);
        if (!fl.exists()){
            try {
                fl.createNewFile();
                List<String[]> rsiData = readAndPreapreRSIData(sData, daysToCalculateRSI, "new", null);
                FileWriter myWriter = new FileWriter(rsi_data_loc);
                myWriter.write("\"DATE\",\"GAIN\",\"LOSS\",\"AVG_GAIN\",\"AVG_LOSS\",\"RS\",\"30-RSI\"");
                for (String[] rs : rsiData){
                    myWriter.write(rs.toString());
                }
                myWriter.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try {
                FileReader fr = new FileReader(fl);
                CSVReader rd = new CSVReader(fr);
                List<String[]> rowsData = rd.readAll();
                String[] header = rowsData.get(0);
                String[] prevDayRSIData = rowsData.get(1);
                List<String[]> rsiData = readAndPreapreRSIData(sData, daysToCalculateRSI, "exists", prevDayRSIData);
                //need to add
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return flag;
    }

    //TODO Impl in-progress and need to test when find sideway stock
    private static List<String[]> readAndPreapreRSIData(List<String[]> sData, int daysToCalculateRSI, String isNewFile, String[] prevDayRSIData) {
        List<String[]> rsiList = new ArrayList<>();
        if (isNewFile.equalsIgnoreCase("New")) {
            sData = sData.subList(0, 30);
            Collections.reverse(sData);
            String gain = "";
            String loss = "";
            double avg_gain = 0.0;
            double avg_loss = 0.0;
            String rs = "";
            String rsi = "";
            double sum_gain = 0.0;
            double sum_loss = 0.0;
            String[] dt = new String[6];
            dt[0] = sData.get(0)[0];
            rsiList.add(dt);
            for (int i = 1; i <= daysToCalculateRSI; i++) {
                String[] sdt = sData.get(i);
                double yesClose = Double.parseDouble(sdt[4]);
                double todClose = Double.parseDouble(sData.get(0)[4]);
                if (yesClose < todClose) {
                    gain = String.valueOf(yesClose - todClose);

                } else if (yesClose > todClose) {
                    loss = String.valueOf(yesClose - todClose);
                } else {
                    gain = "0";
                    loss = "0";
                }
                avg_gain = Double.parseDouble(gain) / daysToCalculateRSI;
                avg_loss = Double.parseDouble(loss) / daysToCalculateRSI;
                double RS = avg_gain / avg_loss;
                dt[0] = sdt[0];
                dt[1] = gain;
                dt[2] = loss;
                dt[3] = String.valueOf(avg_gain);
                dt[4] = String.valueOf(avg_loss);
                dt[5] = String.valueOf(RS);
                double RSI = (100 - (100 / (1 + RS)));
                dt[6] = String.valueOf(RSI);

                rsiList.add(dt);
            }
        }else if (isNewFile.equalsIgnoreCase("Exists")){
            String[] dt = new String[6];
            String[] todaysData = sData.get(0);
            dt[0] = todaysData[0];
            String[] yesData = new String[6];
            for (int i = 1; i <= daysToCalculateRSI; i++) {
                if (!(sData.get(i)[1].isEmpty() || sData.get(i)[1]==null)){
                    yesData = sData.get(i);
                    break;
                }
            }
            double tClose = Double.parseDouble(todaysData[4]);
            double yClose = Double.parseDouble(yesData[4]);
            double gain = 0.0;
            double loss = 0.0;
            if (tClose < yClose) {
                gain = 0;
                loss = yClose - tClose;
            }else if (tClose > yClose){
                gain = tClose - yClose;
                loss = 0;
            }else {
                gain = 0;
                loss = 0;
             }
            double avgGain = ((Double.parseDouble(prevDayRSIData[3]) * (daysToCalculateRSI-1))+ gain)/daysToCalculateRSI;
            double avgLoss = ((Double.parseDouble(prevDayRSIData[3]) * (daysToCalculateRSI-1))+ loss)/daysToCalculateRSI;
            double RS = avgGain/avgLoss;
            double RSI = (100 - (100/(1+ RS)));
            dt[1] = String.valueOf(gain);
            dt[2] = String.valueOf(loss);
            dt[3] = String.valueOf(avgGain);
            dt[4] = String.valueOf(avgLoss);
            dt[5] = String.valueOf(RS);
            dt[6] = String.valueOf(RSI);
        }
        return rsiList;
    }

    //TODO need to modify for RED , this added only accepeted for GREEN
    public static boolean checkCandleConditions(String stockName, String marketMov) {
        boolean flag = true;
        List<String[]> stData = StockUtil.loadStockData(stockName);
        List<String[]> stEma = StockUtil.loadEmaData(stockName);
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("checkCandleConditions")) {
            String[] tData = stData.get(0);
            double tOpen = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(tData[1]));
            double tClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(tData[4]));
            if (marketMov.equalsIgnoreCase("GREEN")) {
                double td = tClose - tOpen;
                if (td < 0)
                    td = td * -1;
                double prctnt = calculatePercantage(td, tClose);
                if (prctnt > 4) {
                    if (!isHigherThan4days(tClose, stData))
                        flag = false;
                }
            }else if (marketMov.equalsIgnoreCase("RED")){
                double td = tOpen-tClose;
                if (td < 0)
                    td = td * -1;
                double prctnt = calculatePercantage(td, tOpen);
                if (prctnt > 4) {
                    if (!isLowerThan4days(tClose, stData))
                        flag = false;
                }
            }
            //If how many days ago was indicator
        }
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("checkIfLowerThanEma") && flag){
            String[] toDayEma = stEma.get(0);
            String[] tData = stData.get(0);
            String[] pData = stData.get(1);
            double ema8 = Double.parseDouble(toDayEma[0]);
//            if (StockUtil.checkOnly83(stockName))
//                ema8 = Double.parseDouble(toDayEma[0]);
//            else
//                ema8 = Double.parseDouble(toDayEma[3]);
            double tHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(tData[2]));
            double tClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(tData[4]));
            int diff = (int) (tHigh - ema8);
            if (diff < 0)
                diff = diff * -1;
            int presDayLow = (int) Double.parseDouble(pData[3]);
            if (marketMov.equalsIgnoreCase("GREEN")){
                if (presDayLow > (int)tHigh) {
                    if (calculatePercantage(diff, (int) tClose) > 2.5) {
                        flag = false;
                    }
                }
            }else if (marketMov.equalsIgnoreCase("RED")){
                if (presDayLow < (int)tHigh) {
                    if (calculatePercantage(diff, (int) tClose) > 2.5) {
                        flag = false;
                    }
                }
            }
        }
        return flag;
    }

    public static boolean isHigherThan4days(double todayClose, List<String[]> stockData) {
        boolean flag = true;
        for (int i=1; i<5; i++){
            String[] sDt = stockData.get(i);
            double high = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(sDt[2]));
            if (high > todayClose ) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static boolean isLowerThan4days(double todayClose, List<String[]> stockData) {
        boolean flag = true;
        for (int i=1; i<5; i++){
            String[] sDt = stockData.get(i);
            double low = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(sDt[3]));
            if (low < todayClose ) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static Date addDaysToDate(Date targetDt, long daysDiff, String format) {
        LocalDate date = LocalDate.parse(new SimpleDateFormat(format).format(targetDt));
        LocalDate date2 = date.plusDays(daysDiff);

        return java.sql.Date.valueOf(date2);
    }

    public static boolean isWeekEnd(Date date)
    {
        String dayOfWeek = convertToLocalDateViaInstant(date).getDayOfWeek().toString();
        if("SATURDAY".equalsIgnoreCase(dayOfWeek)||
                "SUNDAY".equalsIgnoreCase(dayOfWeek))
        {
            return true;
        }
        return false;
    }

    public static boolean isNSEHoliday(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dt = sdf.format(date);
        List<String> holidatList = Arrays.asList("26-01-2023","07-03-2023","30-03-2023","04-04-2023","07-04-2023",
                "14-04-2023","01-05-2023","29-06-2023","15-07-2023","19-09-2023","02-10-2023","24-10-2023",
                "14-11-2023","27-11-2023","25-12-2023");
        if(holidatList.contains(dt))
        {
            return true;
        }
        return false;
    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

    public static StockDetails prepareCandleData(String[] str) {
        return StockDetails.builder()
                .stockName(str[0].split("= ")[1])
                .volume(Integer.parseInt(str[1].split("= ")[1]))
                .isGreenRed(str[2].split("= ")[1])
                .candleTypesOccur(str[3].split("= ")[1])
                .build();
    }

    public boolean isMarketUpDownTrendCheck(String marketExcMov) {
        boolean flag = true;
        if (StockPropertiesUtil.getBooleanIndicatorProps().get("isMarketUpDownTrendCheck")){
            if (marketExcMov.equalsIgnoreCase("GREEN")){
                //check if market up-trend
            }else if (marketExcMov.equalsIgnoreCase("RED")){
                //check if market down-trend
            }
        }
        return flag;
    }

    public static double calculatePercantage(double deriveVal, double totalVal) {
        double div = deriveVal/totalVal;
        return div * 100;
    }

    private static double calculateValueFromPercnt(double percentage, double totalVal) {
        return percentage/100 * totalVal;
    }

    public static double calculateTarget(double whatPerntCalculate, double totalAlreadyMoved, double closeValOfStock){
        double calculateTotalPercntValue = calculateValueFromPercnt(whatPerntCalculate, closeValOfStock);
        double target = calculateTotalPercntValue < totalAlreadyMoved ? 0.0 : calculateTotalPercntValue - totalAlreadyMoved;
        return convertDoubleToTwoPrecision(target);
    }

    public static boolean isCrossOverHappenWithinDays(String stockName, String movement, int crossOverdays) {
        boolean flag = false;
        if (StockPropertiesUtil.getBooleanIndicatorProps().get("isCrossOverHappenWithinFiveDays")) {
            List<String[]> stEma = StockUtil.loadEmaData(stockName);
            crossOverdays = stEma.size() < crossOverdays ? stEma.size(): crossOverdays;
            for (int i = 1; i < crossOverdays; i++) {
                String[] dt = stEma.get(i);
                double ema8 = 0.0;
                double ema3 = 0.0;
                if (StockUtil.checkNewAddedstock(stockName)){
                    ema8 = Double.parseDouble(dt[0]);
                    ema3 = Double.parseDouble(dt[1]);
                }else{
                if (StockUtil.checkOnly83(stockName)) {
                    ema8 = Double.parseDouble(dt[0]);
                    ema3 = Double.parseDouble(dt[1]);
                }else{
                    ema8 = Double.parseDouble(dt[3]);
                    ema3 = Double.parseDouble(dt[4]);
                }}
                if (movement.equalsIgnoreCase("GREEN")) {
                    if (ema8 <= ema3) {
                        flag = true;
                        break;
                    }
                } else if (movement.equalsIgnoreCase("RED")) {
                    if (ema8 >= ema3) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    public static boolean isMarketSideWaysForWeek(String stockName) {
        boolean flag = false;
        if (StockPropertiesUtil.getBooleanIndicatorProps().get("isMarketSideWaysForWeekCheck")){
            List<String[]> stData = StockUtil.loadStockData(stockName);
            LinkedList<Boolean> stTrend = new LinkedList<>();
            for (int i=2; i<7; i++){
                String[] fdt = stData.get(i);
                String[] sdt = stData.get(i-1);
                double fHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(fdt[2]));
                double fLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(fdt[3]));
                double sHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(sdt[2]));
                double sLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(sdt[3]));

            }

        }
        return flag;
    }

    public static List<StockDetails> firstCheckList(List<StockDetails> list, String stockDirection) {
        List<StockDetails> stList = new ArrayList<>();
        if (list.size() == 0)
            return stList;
        int daysCheck = (int)getDaysDiff(getLastMonthFirstDayDate(), new Date());
        double totalMove = 0.0;
        double closeVal = 0.0;
        double todayHigh = 0.0;
        double todayClose = 0.0;
        double todayOpen = 0.0;
        StringBuilder target = new StringBuilder();
        StockDetails stDetails = null;
        double fivePrcntTrg = 0.0;
        double tenPrcntTrg = 0.0;
        try {
            //if stock already moved(open+high) 5.50% plus then ignore
            if (stockDirection.equalsIgnoreCase("GREEN")) {
                for (StockDetails stock : list) {
                    List<String[]> stData = StockUtil.loadStockData(stock.getStockName());
                    List<String[]> emaData = StockUtil.loadEmaData(stock.getStockName());
                    todayHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(0)[2]));
                    todayClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(0)[4]));
                    todayOpen = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(0)[1]));
                    for (int i = 0; i <= daysCheck; i++) {
                        if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[1])) <
                                StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[4]))) {
//                        totalMove = totalMove + (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[2])) -
//                                StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i+1)[4])));
                            closeVal = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i + 1)[4]));
                        } else
                            break;
                    }
                    totalMove = StockUtil.convertDoubleToTwoPrecision(todayHigh - closeVal);
                    double percnt = StockUtil.calculatePercantage(totalMove, closeVal);
                    stDetails = StockDetails.builder()
                            .isGreenRed(stock.getIsGreenRed())
                            .stockName(stock.getStockName())
                            .volume(stock.getVolume())
                            .highVolumeCompareDays(stock.getHighVolumeCompareDays())
                            .percentageMoved(StockUtil.convertDoubleToTwoPrecision(percnt))
                            .build();
                    Map<String, Object> targetPredictionData = CalculateFuturePrediction.calculateTargetForStock(stock.getStockName());
                    fivePrcntTrg = StockUtil.calculateTarget(5.0, totalMove, closeVal);
                    tenPrcntTrg = StockUtil.calculateTarget(10.0, totalMove, closeVal);
                    if ((boolean) targetPredictionData.get("isMarketUpTrend")) {
                        List<StockAttrDetails> listDt = (List<StockAttrDetails>) targetPredictionData.get("movCheckStockList");
                        if (listDt.get(0).getPrice() > todayClose)
                            stDetails.setTradeCondition("Mrkt UpTrend and expected prc= " + targetPredictionData.get("expectedPriceToday") + " and it will go up");
                    } else if (!(boolean) targetPredictionData.get("isMarketUpTrend")) {
                        if (StockUtil.convertDoubleToTwoPrecision(todayClose) < Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())
                                && StockUtil.convertDoubleToTwoPrecision(todayHigh) >= Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())) {
                            System.out.println(stock.getStockName() + " It may be reverse....");
                            stDetails.setTradeCondition("Mrkt DownTrend and expected prc= " + targetPredictionData.get("expectedPriceToday") + " but its high already touch so it may reverse");
                        } else if (StockUtil.convertDoubleToTwoPrecision(todayClose) >= Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())) {
                            if (todayOpen > Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())) {
                                stDetails.setTradeCondition("Mrkt DownTrend and close price cross expected prc= " + (double) targetPredictionData.get("expectedPriceToday") + " so its already breakout done");
                            } else {
                                stDetails.setTradeCondition("Mrkt DownTrend and close price cross expected prc= " + (double) targetPredictionData.get("expectedPriceToday") + " so its breakout stage");
                                stDetails.setTarget("1st 5%/2nd 10% trgt= " + (fivePrcntTrg == 0.0 ? "0 : " + tenPrcntTrg : fivePrcntTrg + ":0"));
                            }
                        } else if (StockUtil.convertDoubleToTwoPrecision(todayClose) < Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())) {
                            stDetails.setTradeCondition("Mrkt DownTrend and close price did not reach target price=" + (double) targetPredictionData.get("expectedPriceToday") + " so it will reach to that");
                            stDetails.setTarget("1st 5% trgt= " + (fivePrcntTrg < Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString()) ? fivePrcntTrg : Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())));
                        }
                    }
                    //check ema100 + ema5
                    //TODO need to uncomment
//                    int movingAvgCheck = list.size() < 3 ? list.size() : 3;
//                    for (int i = 0; i <= movingAvgCheck; i++) {
//                        String[] todaysEmaData = emaData.get(i);
//                        double ema100 = Double.parseDouble(todaysEmaData[todaysEmaData.length - 2]);
//                        double ema5 = Double.parseDouble(todaysEmaData[todaysEmaData.length - 1]);
//                        if (i == 0) {
//                            if (ema100 < ema5) {
//                                stDetails.setEma100_5_cross("Buy");
//                            } else if (ema100 > ema5) {
//                                stDetails.setEma100_5_cross("Sell");
//                            }
//                        }
//                        if (ema100 == ema5 || (ema5 - ema100 < 5) && stDetails.getEma100_5_cross().equals("Buy"))
//                            stDetails.setEma100_5_cross("Strong_Buy");
//                        else if (ema100 == ema5 || (ema100 - ema5 < 5) && stDetails.getEma100_5_cross().equals("Sell"))
//                            stDetails.setEma100_5_cross("Strong_Sell");
//                    }
                    if (ReadResultsDateDataJob.validateIsStockResultDateRecently(stock.getStockName()))
                        stDetails.setIsResultDateNear("Yes");

                    stList.add(stDetails);
//                double fivePrcntTrg = StockUtil.calculateTarget(5.0, totalMove, closeVal);
                }
            } else if (stockDirection.equalsIgnoreCase("RED")) {
                for (StockDetails stock : list) {
                    List<String[]> stData = StockUtil.loadStockData(stock.getStockName());
                    List<String[]> emaData = StockUtil.loadEmaData(stock.getStockName());
                    todayHigh = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(0)[2]));
                    todayClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(0)[4]));
                    todayOpen = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(0)[1]));
                    for (int i = 0; i <= daysCheck; i++) {
                        if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[1])) <
                                StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[4]))) {
//                        totalMove = totalMove + (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i)[2])) -
//                                StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i+1)[4])));
                            closeVal = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stData.get(i + 1)[4]));
                        } else
                            break;
                    }
                    totalMove = StockUtil.convertDoubleToTwoPrecision(todayHigh - closeVal);
                    double percnt = StockUtil.calculatePercantage(totalMove, closeVal);
                    stDetails = StockDetails.builder()
                            .isGreenRed(stock.getIsGreenRed())
                            .stockName(stock.getStockName())
                            .volume(stock.getVolume())
                            .highVolumeCompareDays(stock.getHighVolumeCompareDays())
                            .percentageMoved(StockUtil.convertDoubleToTwoPrecision(percnt))
                            .build();
                    Map<String, Object> targetPredictionData = CalculateFuturePrediction.calculateTargetForStock(stock.getStockName());
                    fivePrcntTrg = StockUtil.calculateTarget(5.0, totalMove, closeVal);
                    tenPrcntTrg = StockUtil.calculateTarget(10.0, totalMove, closeVal);
                    if ((boolean) targetPredictionData.get("isMarketUpTrend")) {
                        List<StockAttrDetails> listDt = (List<StockAttrDetails>) targetPredictionData.get("movCheckStockList");
                        if (listDt.get(0).getPrice() > todayClose)
                            stDetails.setTradeCondition("Mrkt UpTrend and expected prc= " + targetPredictionData.get("expectedPriceToday") + " and it will go up");
                    } else if (!(boolean) targetPredictionData.get("isMarketUpTrend")) {
                        if (StockUtil.convertDoubleToTwoPrecision(todayClose) < Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())
                                && StockUtil.convertDoubleToTwoPrecision(todayHigh) >= Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())) {
                            System.out.println(stock.getStockName() + " It may be reverse....");
                            stDetails.setTradeCondition("Mrkt DownTrend and expected prc= " + targetPredictionData.get("expectedPriceToday") + " but its high already touch so it may reverse");
                        } else if (StockUtil.convertDoubleToTwoPrecision(todayClose) >= Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())) {
                            if (todayOpen > Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())) {
                                stDetails.setTradeCondition("Mrkt DownTrend and close price cross expected prc= " + (double) targetPredictionData.get("expectedPriceToday") + " so its already breakout done");
                            } else {
                                stDetails.setTradeCondition("Mrkt DownTrend and close price cross expected prc= " + (double) targetPredictionData.get("expectedPriceToday") + " so its breakout stage");
                                stDetails.setTarget("1st 5%/2nd 10% trgt= " + (fivePrcntTrg == 0.0 ? "0 : " + tenPrcntTrg : fivePrcntTrg + ":0"));
                            }
                        } else if (StockUtil.convertDoubleToTwoPrecision(todayClose) < Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())) {
                            stDetails.setTradeCondition("Mrkt DownTrend and close price did not reach target price=" + (double) targetPredictionData.get("expectedPriceToday") + " so it will reach to that");
                            stDetails.setTarget("1st 5% trgt= " + (fivePrcntTrg < Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString()) ? fivePrcntTrg : Double.parseDouble(targetPredictionData.get("expectedPriceToday").toString())));
                        }
                    }
                    //check ema100 + ema5
                    //TODO uncomment it
//                    int movingAvgCheck = list.size() < 3 ? list.size():3;
//                    for (int i = 0; i <= movingAvgCheck; i++) {
//                        String[] todaysEmaData = emaData.get(i);
//                        double ema100 = Double.parseDouble(todaysEmaData[todaysEmaData.length - 2]);
//                        double ema5 = Double.parseDouble(todaysEmaData[todaysEmaData.length - 1]);
//                        if (i == 0) {
//                            if (ema100 < ema5) {
//                                stDetails.setEma100_5_cross("Buy");
//                            } else if (ema100 > ema5) {
//                                stDetails.setEma100_5_cross("Sell");
//                            }
//                        }
//                        if (ema100 == ema5 || (ema5 - ema100 < 5) && stDetails.getEma100_5_cross().equals("Buy"))
//                            stDetails.setEma100_5_cross("Strong_Buy");
//                        else if (ema100 == ema5 || (ema100 - ema5 < 5) && stDetails.getEma100_5_cross().equals("Sell"))
//                            stDetails.setEma100_5_cross("Strong_Sell");
//                    }
                    stList.add(stDetails);
//                double fivePrcntTrg = StockUtil.calculateTarget(5.0, totalMove, closeVal);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stList;
    }

    public static Date getLastMonthFirstDayDate() {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.set(Calendar.DATE, 1);
        aCalendar.add(Calendar.DAY_OF_MONTH, -1);
//        Date lastDateOfPreviousMonth = aCalendar.getTime();
        aCalendar.set(Calendar.DATE, 1);
        return aCalendar.getTime();
    }

    public static double calculateMovingAvg(int daysToCalulate, List<String[]> stockHistoryData){
        double ma = 0.0;
//        List<String[]> stockHistData = loadStockData(stockName);
        double total = 0.0;
        for (int i=0; i<daysToCalulate; i++){
            String[] sData = stockHistoryData.get(i);
            if (sData[0].equals("Date"))
                break;
            double closeData = convertDoubleToTwoPrecision(Double.parseDouble(sData[4]));
            total = total + closeData;
        }
        ma = total / daysToCalulate;
        return convertDoubleToTwoPrecision(ma);
    }

    private static Map<String, Boolean> validateMA100And5(List<String[]> allData) {
        Map<String, Boolean> emaCal = new HashMap<>();
        emaCal.put("buy", false);
        emaCal.put("sell", false);
        emaCal.put("strength_buy", false);
        emaCal.put("strength_sell", false);
        String[] todayEm = allData.get(0);
        double ema100 = 0.0;
        double ema5 = 0.0;

        ema100 = Double.parseDouble(todayEm[todayEm.length-2]);
        ema5 = Double.parseDouble(todayEm[todayEm.length-1]);
        if(ema100 < ema5){
            emaCal.put("buy", true);
        }else if(ema100 > ema5){
            emaCal.put("sell", true);
        }
        if(ema100==ema5 || (ema5-ema100 == 3)){
            emaCal.put("strength_buy", true);
        }else if(ema100==ema5 || (ema100-ema5 == 3)){
            emaCal.put("strength_sell", true);
        }
        return emaCal;
    }

    public static void removeNullDataUpdateFile(String stockName, String path) {
        List<String[]> list = loadStockData(stockName);
        Collections.reverse(list);
        list = checkRemoveNullFromHistoryData(list);
        storeFile(path,list);
    }
    public static List<String[]> checkRemoveNullFromHistoryData(List<String[]> historyData) {
        List<String[]> list = new ArrayList<>();
        for (String[] data : historyData){
            if ((!data[1].equals("null")) && (!data[2].equals("null"))
                    && (!data[3].equals("null")) && (!data[4].equals("null"))
                    && (!data[6].equals("null"))){
                list.add(data);
            }
        }
        return list;
    }

    public static void storeFile(String filePath, List<String[]> data) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter outputfile = new FileWriter(file, false);
            CSVWriter writer = new CSVWriter(outputfile);
            for (String[] dt : data){
                writer.writeNext(dt);
            }
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static EmaChangeDetails isChangeEmaDirection(String stockName) {
        EmaChangeDetails emaChangeDetails = EmaChangeDetails.builder().build();
        emaChangeDetails.setStockName(stockName);
        List<String[]> emaData = loadEmaData(stockName);
        if (emaData.size() == 0) {
            emaChangeDetails.setEligible(false);
            return emaChangeDetails;
        }
        double avgDiff = 0.0;
        double emaTotalDiff = 0.0;
        boolean indicator = false;
        String movement = "";
        if((Double.parseDouble(emaData.get(0)[0]) <
                Double.parseDouble(emaData.get(0)[1]))){
            indicator = true;
            movement = "positive";
        }else if((Double.parseDouble(emaData.get(0)[1]) <
                Double.parseDouble(emaData.get(0)[0]))) {
            indicator = true;
            movement = "negative";
        }
        int i = 1;
        while (indicator && i <=15){
            emaTotalDiff = emaTotalDiff + (Double.parseDouble(emaData.get(i)[0])
                    - Double.parseDouble(emaData.get(i)[1]));
            if((movement.equals("positive") && Double.parseDouble(emaData.get(0)[0]) >
                    Double.parseDouble(emaData.get(0)[1]))){
                indicator = false;
            }else if(movement.equals("negative") && (Double.parseDouble(emaData.get(0)[1]) >
                    Double.parseDouble(emaData.get(0)[0]))) {
                indicator = false;
            }
            i++;
        }
        if (emaTotalDiff < 0) {
            //Its negative
            emaTotalDiff = emaTotalDiff * -1;
        }
        avgDiff = emaTotalDiff / 10;
        emaChangeDetails.setEmaMovingAvg(StockUtil.convertDoubleToTwoPrecision(avgDiff));
        if ((Double.parseDouble(emaData.get(0)[0])
                - Double.parseDouble(emaData.get(0)[1]) < avgDiff)){
            emaChangeDetails.setEligible(true);
        }else if ((Double.parseDouble(emaData.get(0)[1])
                - Double.parseDouble(emaData.get(0)[0]) < avgDiff)){
            emaChangeDetails.setEligible(true);
        }
        return emaChangeDetails;
    }

    public static double getAllLowData(List<String[]> stockData) {
        double low = 99.99;
        for (int i=0; i<stockData.size()-1;i++){
            double dayLow = Double.parseDouble(stockData.get(i)[4]);
            if (dayLow < low)
                low = dayLow;
        }

        return low;
    }

    public static boolean isWeekLowWithin3Days(String stockName) {
        boolean flag = false;
        List<String[]> stockData = loadStockData(stockName);
        double lowP = StockUtil.convertDoubleToTwoPrecision(getAllLowData(stockData));
        for(int i=0; i<4; i++) {
            if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stockData.get(i)[3]))
                    <= lowP) {
                flag = true;
            }
        }
        return flag;
    }

    public static Map<String, String> readEmaDataModifyLogic(String stockEmaDataLoad, String stockName, String indicator) {
        {
            Map<String, String> notificationData = new HashMap<>();
            try {
                int countDay = 0;
                int stockIsGreen = 0;
                int stockIsRed = 0;
                int ema8_3_stockIsGreen = 0;
                int ema8_3_stockIsRed = 0;
                File file = new File(stockEmaDataLoad);
                if (!file.exists())
                    file.createNewFile();
                FileReader filereader = new FileReader(file);
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withSkipLines(1)
                        .build();
                List<String[]> allData = csvReader.readAll();
                //get Stock history data
                List<String[]> stData = StockUtil.loadStockData(stockName);
                //check is stock RED/GREEN
                Map<String, Object> marketData = checkStockGreenOrRed(allData, stockName, stData);
                Map<String,String> volumeCheckData = checkVolumeSize(stockName);
                //TODO it will check based on enabled added in properties
                if (indicator.equals("trendStocks")){
                    checkIndicatorStatusAndSetNotificationMarketMovementData(marketData, notificationData, stockName, volumeCheckData);
                }else {
                    checkIndicatorStatusAndSetNotificationDataLogic(marketData, notificationData, stockName, volumeCheckData);
                }
            }catch (Exception e){
                System.out.println("Error................."+stockName);
                e.printStackTrace();
            }
            return notificationData;
        }
    }

    private static void checkIndicatorStatusAndSetNotificationDataLogic(Map<String, Object> marketData, Map<String, String> notificationData,
                                                                   String stockName,Map<String,String> volumeCheckData) {
        int ema8_3_stockIsGreen = (int) marketData.get("ema8_3_stockIsGreen");
        int ema8_3_stockIsRed = (int) marketData.get("ema8_3_stockIsRed");
        int minEmaGreenRedCheckCount = StockPropertiesUtil.getIntegerIndicatorProps().get("minEmaGreenRedCheckCount");
        int maxEmaGreenRedCheckCount = StockPropertiesUtil.getIntegerIndicatorProps().get("maxEmaGreenRedCheckCount");
        //TODO Disabling both indicator add list

        if (marketData.get("marketMovement").equals("Green")
                && ((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount))){
            if((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <= maxEmaGreenRedCheckCount)
                    && !StockUtil.isCrossOverHappenWithinDaysLogic(stockName, "UP", 5)){
                    StockDetails sd = StockDetails.builder()
                            .isGreenRed("GREEN")
                            .stockName(stockName)
                            .volume(Integer.parseInt(volumeCheckData.get("todaysVolume")))
                            .highVolumeCompareDays(Integer.parseInt(volumeCheckData.get("compareDays")))
                            .build();
                StockUtil.addStockDetailsToSendMailFor83IndOnlyUpDown(sd);
            }
        }

        if (marketData.get("marketMovement").equals("Red")
                && ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount))){
            if ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <=maxEmaGreenRedCheckCount)
                    && !StockUtil.isCrossOverHappenWithinDaysLogic(stockName, "DOWN", 5)){
                StockDetails sd = StockDetails.builder()
                            .isGreenRed("RED")
                            .stockName(stockName)
                            .volume(Integer.parseInt(volumeCheckData.get("todaysVolume")))
                            .highVolumeCompareDays(Integer.parseInt(volumeCheckData.get("compareDays")))
                            .build();
                StockUtil.addStockDetailsToSendMailFor83IndOnlyUpDown(sd);
            }
        }
    }

    private static void checkIndicatorStatusAndSetNotificationMarketMovementData(Map<String, Object> marketData, Map<String, String> notificationData,
                                                                        String stockName,Map<String,String> volumeCheckData) {
        int ema8_3_stockIsGreen = (int) marketData.get("ema8_3_stockIsGreen");
        int ema8_3_stockIsRed = (int) marketData.get("ema8_3_stockIsRed");
        int minEmaGreenRedCheckCountMrktMove = StockPropertiesUtil.getIntegerIndicatorProps().get("minEmaGreenRedCheckCountMrktMove");
        int maxEmaGreenRedCheckCountMrktMove = StockPropertiesUtil.getIntegerIndicatorProps().get("maxEmaGreenRedCheckCountMrktMove");
        //TODO Disabling both indicator add list

        if (marketData.get("marketMovement").equals("Green")
                && ((ema8_3_stockIsGreen >= minEmaGreenRedCheckCountMrktMove && ema8_3_stockIsGreen <maxEmaGreenRedCheckCountMrktMove))){
            if((ema8_3_stockIsGreen >= minEmaGreenRedCheckCountMrktMove && ema8_3_stockIsGreen <= maxEmaGreenRedCheckCountMrktMove)
                    && !StockUtil.isCrossOverHappenWithinDaysLogic(stockName, "DOWN", 5)
            ){
                StockDetails sd = StockDetails.builder()
                        .isGreenRed("GREEN")
                        .stockName(stockName)
                        .volume(Integer.parseInt(volumeCheckData.get("todaysVolume")))
                        .highVolumeCompareDays(Integer.parseInt(volumeCheckData.get("compareDays")))
                        .trendDays(ema8_3_stockIsGreen)
                        .build();
                StockUtil.addTrensStockDetails(sd);
            }
        }

        if (marketData.get("marketMovement").equals("Red")
                && ((ema8_3_stockIsRed >= minEmaGreenRedCheckCountMrktMove && ema8_3_stockIsRed <maxEmaGreenRedCheckCountMrktMove))){
            if ((ema8_3_stockIsRed >= minEmaGreenRedCheckCountMrktMove && ema8_3_stockIsRed <=maxEmaGreenRedCheckCountMrktMove)
//                    && !StockUtil.isCrossOverHappenWithinDaysLogic(stockName, "UP", 5)
            ){
                StockDetails sd = StockDetails.builder()
                        .isGreenRed("RED")
                        .stockName(stockName)
                        .volume(Integer.parseInt(volumeCheckData.get("todaysVolume")))
                        .highVolumeCompareDays(Integer.parseInt(volumeCheckData.get("compareDays")))
                        .trendDays(ema8_3_stockIsRed)
                        .build();
                StockUtil.addTrensStockDetails(sd);
            }
        }
    }

    public static Map<String, Object> checkStockGreenOrRedLogic(List<String[]> allData, String stockName, List<String[]> stockHData) {
        LinkedList<String> linkedList = new LinkedList();
        LinkedList<String> ema30_9_linkedList = new LinkedList();
        LinkedList<String> ema9_5_linkedList = new LinkedList();
        LinkedList<String> ema8_3_linkedList = new LinkedList();
        Map<String, Object> marketData = new HashMap<>();
        boolean greenContinue = true;
        boolean redContinue = true;
        boolean ema30_9_redContinue = true;
        boolean ema30_9_greenContinue = true;
        boolean ema9_5_redContinue = true;
        boolean ema9_5_greenContinue = true;
        boolean ema8_3_redContinue = true;
        boolean ema8_3_greenContinue = true;
        int stockIsGreen = 0;
        int stockIsRed = 0;
        int ema30_9_stockIsGreen = 0;
        int ema30_9_stockIsRed = 0;
        int ema9_5_stockIsGreen = 0;
        int ema9_5_stockIsRed = 0;
        int ema8_3_stockIsGreen = 0;
        int ema8_3_stockIsRed = 0;
        boolean Dema30_9_green  = false;
        boolean Dema30_9_red = false;
        boolean Dema9_5_green = false;
        boolean Dema9_5_red = false;
        boolean ema8_3_green = false;
        boolean ema8_3_red = false;
        String marketMovement = "";
        int firstIndex = 0;
        if (StockUtil.checkStockToRun(stockName)){
            for (String[] data : allData) {
                if (!data[1].equals("null")) {
                    double ema30 = 0.0;
                    double ema9 = 0.0;
                    double ema5 = 0.0;
                    double ema8 = 0.0;
                    double ema3 = 0.0;
                    if (firstIndex == 0 && StockPropertiesUtil.booleanIndicators.get("checkTradeWithoutDoublePrecision")) {
                        if(StockUtil.checkNewAddedstock(stockName)) {
                            ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                            ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                            ema30 = 0;
                            ema9 = 0;
                            ema5 = 0;
                        }else{
                            if (StockUtil.checkOnly83(stockName)){
                                ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                                ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                            }else {
                                ema30 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                                ema9 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                                ema5 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[2]));
                                ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[3]));
                                ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[4]));
                            }}
                    }else{
                        if(StockUtil.checkNewAddedstock(stockName)) {
                            ema8 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                            ema3 = (int) StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                            ema30 = 0;
                            ema9 = 0;
                            ema5 = 0;
                        }else{
                            if (StockUtil.checkOnly83(stockName)) {
                                ema8 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                                ema3 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                            }else {
                                ema30 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                                ema9 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                                ema5 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[2]));
                                ema8 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[3]));
                                ema3 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[4]));
                            }}
                    }
                    checkConditionsAndUpdateList(ema30, ema9, ema5, ema8, ema3, ema30_9_linkedList, ema9_5_linkedList, ema8_3_linkedList, linkedList, stockHData);
//                    if(ema30 < ema9 || ema9 < ema5 || ema8 > ema3){
//                        linkedList.add("G");
//                    }else if (ema30 > ema9 || ema9 > ema5 || ema8 < ema3){
//                        linkedList.add("R");
//                    }
                }
                if (firstIndex==5)
                    break;
                firstIndex++;
            }
        }else {
            for (String[] data : allData) {
                if (!data[1].equals("null")) {
                    if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]))
                            < StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]))) {
                        linkedList.add("G");
                    } else {
                        linkedList.add("R");
                    }
                }
            }
        }
//        for (int i=0; i<linkedList.size();i++){
//            if(linkedList.get(i).equals("R") && redContinue){
//                if (stockIsGreen!=0)
//                    greenContinue = false;
//                if (StringUtils.isEmpty(marketMovement))
//                    marketMovement = "Red";
//                stockIsRed++;
//            }else if (linkedList.get(i).equals("G") && greenContinue){
//                if (stockIsRed!=0)
//                    redContinue = false;
//                if (StringUtils.isEmpty(marketMovement))
//                    marketMovement = "Green";
//                stockIsGreen++;
//            }
//        }

        for (int i=0; i<ema8_3_linkedList.size();i++){
            if(ema8_3_linkedList.get(i).equals("R") && ema8_3_redContinue && !ema8_3_red){
                if (ema8_3_stockIsGreen!=0)
                    ema8_3_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema8_3_stockIsRed++;
                ema8_3_green = true;
            }else if (ema8_3_linkedList.get(i).equals("G") && ema8_3_greenContinue && !ema8_3_green){
                if (ema8_3_stockIsRed!=0)
                    ema8_3_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema8_3_stockIsGreen++;
                ema8_3_red = true;
            }else if(ema8_3_linkedList.get(i).equals("R") && ema8_3_stockIsGreen != 0)
                break;
            else if(ema8_3_linkedList.get(i).equals("G") && ema8_3_stockIsRed != 0)
                break;
        }

        for (int i=0; i<ema9_5_linkedList.size();i++){
            if(ema9_5_linkedList.get(i).equals("R") && ema9_5_redContinue && !Dema9_5_red){
                if (ema9_5_stockIsGreen!=0)
                    ema9_5_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema9_5_stockIsRed++;
                Dema9_5_green = true;
            }else if (ema9_5_linkedList.get(i).equals("G") && ema9_5_greenContinue && !Dema9_5_green){
                if (ema9_5_stockIsRed!=0)
                    ema9_5_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema9_5_stockIsGreen++;
                Dema9_5_red = true;
            }else if(ema9_5_linkedList.get(i).equals("R") && ema9_5_stockIsGreen != 0)
                break;
            else if(ema9_5_linkedList.get(i).equals("G") && ema9_5_stockIsRed != 0)
                break;
        }
        for (int i=0; i<ema30_9_linkedList.size();i++){
            if(ema30_9_linkedList.get(i).equals("R") && ema30_9_redContinue && !Dema30_9_red){
                if (ema30_9_stockIsGreen!=0)
                    ema30_9_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema30_9_stockIsRed++;
                Dema30_9_green = true;
            }else if (ema30_9_linkedList.get(i).equals("G") && ema30_9_greenContinue && !Dema30_9_green){
                if (ema30_9_stockIsRed!=0)
                    ema30_9_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema30_9_stockIsGreen++;
                Dema30_9_red = true;
            }else if(ema30_9_linkedList.get(i).equals("R") && ema30_9_stockIsGreen != 0)
                break;
            else if(ema30_9_linkedList.get(i).equals("G") && ema30_9_stockIsRed != 0)
                break;
        }

        marketData.put("marketMovement",marketMovement);
        marketData.put("stockIsGreen",stockIsGreen);
        marketData.put("stockIsRed",stockIsRed);
        marketData.put("ema8_3_stockIsGreen",ema8_3_stockIsGreen);
        marketData.put("ema8_3_stockIsRed",ema8_3_stockIsRed);
        marketData.put("ema9_5_stockIsGreen",ema9_5_stockIsGreen);
        marketData.put("ema9_5_stockIsRed",ema9_5_stockIsRed);
        marketData.put("ema30_9_stockIsGreen",ema30_9_stockIsGreen);
        marketData.put("ema30_9_stockIsRed",ema30_9_stockIsRed);
        return marketData;
    }

    public static boolean isCrossOverHappenWithinDaysLogic(String stockName, String movement, int crossOverdays) {
        boolean flag = false;
        if (StockPropertiesUtil.getBooleanIndicatorProps().get("isCrossOverHappenWithinFiveDays")) {
            List<String[]> stockEma = StockUtil.loadEmaData(stockName);
            crossOverdays = stockEma.size() < crossOverdays ? stockEma.size(): crossOverdays;
            for (int i = 1; i < crossOverdays; i++) {
                String[] dt = stockEma.get(i);
                double ema8 = Double.parseDouble(dt[0]);
                double ema3 = Double.parseDouble(dt[1]);
                int ema8_1 = (int)ema8;
                int ema3_1 = (int)ema3;
                if(movement.equals("DOWN") && ema8 > ema3) {
                    flag = true;
                }else if(movement.equals("UP") && ema8 < ema3) {
                    flag = true;
                }else if(ema3_1==ema8_1) {
                    flag = true;
                }
            }
        }
        return flag;
    }
}
