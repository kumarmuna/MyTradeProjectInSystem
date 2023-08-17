package manas.muna.trade.util;

import com.fasterxml.jackson.databind.ObjectReader;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import manas.muna.trade.jobs.CalculateProfitAndStoreJob;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StockUtil {
    private static final DecimalFormat df = new DecimalFormat("0.00");
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
        String[] keys = {"index_list","c_stock_list","d_stock_list","e_stock_list_part"};
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
        for(String key : keys){
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
        if(StringUtils.isEmpty(p.getProperty("buy_stock_list"))){
            return new String[0];
        }else {
            return p.getProperty("buy_stock_list").split(",");
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
            Map<String, Object> marketData = checkStockGreenOrRed(allData, stockName);
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
            Map<String,Boolean> isVolumeHigh = checkVolumeSize(stockName);
            checkIndicatorStatusAndSetNotificationData(marketData, notificationData, fiveDatHighLowData, isVolumeHigh ,stockName);
//            if (marketData.get("marketMovement").equals("Green") && ema8_3_stockIsGreen >= 1 && ema8_3_stockIsGreen <3){
//                CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName);
////                if (fiveDatHighLowData.get("fiveDayHigh") && StockUtil.extraCheckToBuyOrNot(stockName)) {
////            if (stockIsGreen >= 1 && stockIsGreen <=3 && fiveDatHighLowData.get("fiveDayHigh") && StockUtil.extraCheckToBuyOrNot(stockName)){
//                if (fiveDatHighLowData.get("fiveDayHigh")){
//                    notificationData.put("stockIsGreen", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Stock " + stockName + " is green last 3 days, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "GREEN: This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
//            }
//            if (marketData.get("marketMovement").equals("Red") && ema8_3_stockIsRed >= 1 && ema8_3_stockIsRed <3){
//                CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(stockName);
////                if (fiveDatHighLowData.get("fiveDayLow") && StockUtil.extraCheckToBuyOrNot(stockName)){
////            if (stockIsRed >= 1 && stockIsRed <=3 && fiveDatHighLowData.get("fiveDayLow") && StockUtil.extraCheckToBuyOrNot(stockName)){
//                if (fiveDatHighLowData.get("fiveDayLow")){
//                    notificationData.put("stockIsRed", "true");
//                    notificationData.put("stockName", stockName);
//                    String msg = "Stock " + stockName + " is RED last 3 days, Have a look once.";
//                    notificationData.put("msg", msg);
//                    String subject = "RED: This is " + stockName + " Stock Alert.....";
//                    notificationData.put("subject", subject);
//                }
//            }
        }catch (Exception e){
            System.out.println("Error................."+stockName);
            e.printStackTrace();
        }
        return notificationData;
    }

    private static Map<String, Boolean> checkVolumeSize(String stockName) {
        Map<String, Boolean> todayVolumeHigh = new HashMap<>();
        if(StockPropertiesUtil.getBooleanIndicatorProps().get("volumeCheckIndicator")) {
            double yesVolume = 0.0;
            double todayVolume = 0.0;
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
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            todayVolumeHigh.put("isVolumeHigh", todayVolume > yesVolume);
        }else{
            todayVolumeHigh.put("isVolumeHigh", true);
        }

        return todayVolumeHigh;
    }

    private static void checkIndicatorStatusAndSetNotificationData(Map<String, Object> marketData, Map<String, String> notificationData,
                                                                   Map<String,Boolean> fiveDatHighLowData, Map<String, Boolean> isVolumeHigh,
                                                                   String stockName) {
        int ema30_9_stockIsGreen = (int) marketData.get("ema30_9_stockIsGreen");
        int ema30_9_stockIsRed = (int) marketData.get("ema30_9_stockIsRed");
        int ema9_5_stockIsGreen = (int) marketData.get("ema9_5_stockIsGreen");
        int ema9_5_stockIsRed = (int) marketData.get("ema9_5_stockIsRed");
        int ema8_3_stockIsGreen = (int) marketData.get("ema8_3_stockIsGreen");
        int ema8_3_stockIsRed = (int) marketData.get("ema8_3_stockIsRed");
        int minEmaGreenRedCheckCount = StockPropertiesUtil.getIntegerIndicatorProps().get("minEmaGreenRedCheckCount");
        int maxEmaGreenRedCheckCount = StockPropertiesUtil.getIntegerIndicatorProps().get("maxEmaGreenRedCheckCount");
        if (marketData.get("marketMovement").equals("Green") && isVolumeHigh.get("isVolumeHigh") && ((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount) ||
                (ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount))){
            if((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount) && (ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount)){
                CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName);
                if (fiveDatHighLowData.get("fiveDayHigh")){ //checkCandelHIghLowGap()
                    notificationData.put("stockIsGreen", "true");
                    notificationData.put("stockName", stockName);
                    String msg = "Stock " + stockName + " is green last 3 days, Have a look once.";
                    notificationData.put("msg", msg);
                    String subject = "GREEN: BOTH This is " + stockName + " Stock Alert.....";
                    notificationData.put("subject", subject);
                }
            }else if(!(ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount) && (ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount)){
                CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName);
                if (fiveDatHighLowData.get("fiveDayHigh")){
                    notificationData.put("stockIsGreen", "true");
                    notificationData.put("stockName", stockName);
                    String msg = "Stock " + stockName + " is green last 3 days, Have a look once.";
                    notificationData.put("msg", msg);
                    String subject = "GREEN: DEMA_9_5 This is " + stockName + " Stock Alert.....";
                    notificationData.put("subject", subject);
                }
            }else if((ema8_3_stockIsGreen >= minEmaGreenRedCheckCount && ema8_3_stockIsGreen <maxEmaGreenRedCheckCount) && !(ema9_5_stockIsGreen >= minEmaGreenRedCheckCount && ema9_5_stockIsGreen <maxEmaGreenRedCheckCount)){
                CalculateProfitAndStoreJob.addStockDataForProfitCalculate(stockName);
                if (fiveDatHighLowData.get("fiveDayHigh")){
                    notificationData.put("stockIsGreen", "true");
                    notificationData.put("stockName", stockName);
                    String msg = "Stock " + stockName + " is green last 3 days, Have a look once.";
                    notificationData.put("msg", msg);
                    String subject = "GREEN: EMA_8_3 This is " + stockName + " Stock Alert.....";
                    notificationData.put("subject", subject);
                }
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
        if (marketData.get("marketMovement").equals("Red") && isVolumeHigh.get("isVolumeHigh") && ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount) ||
                (ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount))){
            if ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount) && (ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount)){
                CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(stockName);
                if (fiveDatHighLowData.get("fiveDayLow")){
                    notificationData.put("stockIsRed", "true");
                    notificationData.put("stockName", stockName);
                    String msg = "Stock " + stockName + " is RED last 3 days, Have a look once.";
                    notificationData.put("msg", msg);
                    String subject = "RED: BOTH This is " + stockName + " Stock Alert.....";
                    notificationData.put("subject", subject);
                }
            }else if ((ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount) && !(ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount)){
                CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(stockName);
                if (fiveDatHighLowData.get("fiveDayLow")){
                    notificationData.put("stockIsRed", "true");
                    notificationData.put("stockName", stockName);
                    String msg = "Stock " + stockName + " is RED last 3 days, Have a look once.";
                    notificationData.put("msg", msg);
                    String subject = "RED: EMA_8_3 This is " + stockName + " Stock Alert.....";
                    notificationData.put("subject", subject);
                }
            }else if (!(ema8_3_stockIsRed >= minEmaGreenRedCheckCount && ema8_3_stockIsRed <maxEmaGreenRedCheckCount) && (ema9_5_stockIsRed >= minEmaGreenRedCheckCount && ema9_5_stockIsRed <maxEmaGreenRedCheckCount)){
                CalculateProfitAndStoreJob.calculateAndUpdateProfitDetails(stockName);
                if (fiveDatHighLowData.get("fiveDayLow")){
                    notificationData.put("stockIsRed", "true");
                    notificationData.put("stockName", stockName);
                    String msg = "Stock " + stockName + " is RED last 3 days, Have a look once.";
                    notificationData.put("msg", msg);
                    String subject = "RED: DEMA_9_5 This is " + stockName + " Stock Alert.....";
                    notificationData.put("subject", subject);
                }
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

    public static Map<String, Object> checkStockGreenOrRed(List<String[]> allData, String stockName) {
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
        String marketMovement = "";
        if (StockUtil.checkStockToRun(stockName)){
            for (String[] data : allData) {
                if (!data[1].equals("null")) {
                    double ema30 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]));
                    double ema9 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]));
                    double ema5 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[2]));
                    double ema8 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[3]));
                    double ema3 = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[4]));
                    checkConditionsAndUpdateList(ema30, ema9, ema5, ema8, ema3, ema30_9_linkedList, ema9_5_linkedList, ema8_3_linkedList, linkedList);
//                    if(ema30 < ema9 || ema9 < ema5 || ema8 > ema3){
//                        linkedList.add("G");
//                    }else if (ema30 > ema9 || ema9 > ema5 || ema8 < ema3){
//                        linkedList.add("R");
//                    }
                }
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
            if(ema8_3_linkedList.get(i).equals("R") && ema8_3_redContinue){
                if (ema8_3_stockIsGreen!=0)
                    ema8_3_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema8_3_stockIsRed++;
            }else if (ema8_3_linkedList.get(i).equals("G") && ema8_3_greenContinue){
                if (ema8_3_stockIsRed!=0)
                    ema8_3_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema8_3_stockIsGreen++;
            }
        }
        for (int i=0; i<ema9_5_linkedList.size();i++){
            if(ema9_5_linkedList.get(i).equals("R") && ema9_5_redContinue){
                if (ema9_5_stockIsGreen!=0)
                    ema9_5_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema9_5_stockIsRed++;
            }else if (ema9_5_linkedList.get(i).equals("G") && ema9_5_greenContinue){
                if (ema9_5_stockIsRed!=0)
                    ema9_5_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema9_5_stockIsGreen++;
            }
        }
        for (int i=0; i<ema30_9_linkedList.size();i++){
            if(ema30_9_linkedList.get(i).equals("R") && ema30_9_redContinue){
                if (ema30_9_stockIsGreen!=0)
                    ema30_9_greenContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Red";
                ema30_9_stockIsRed++;
            }else if (ema30_9_linkedList.get(i).equals("G") && ema30_9_greenContinue){
                if (ema30_9_stockIsRed!=0)
                    ema30_9_redContinue = false;
                if (StringUtils.isEmpty(marketMovement))
                    marketMovement = "Green";
                ema30_9_stockIsGreen++;
            }
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
                                                     LinkedList<String> ema8_3_linkedList, LinkedList<String> linkedList) {
        if (ema30<ema9){
            ema30_9_linkedList.add("G");
        }else if (ema30 > ema9){
            ema30_9_linkedList.add("R");
        }
        if (ema9 < ema5){
            ema9_5_linkedList.add("G");
        }else if (ema9 > ema5){
            ema9_5_linkedList.add("R");
        }
        if (ema8 > ema3){
            ema8_3_linkedList.add("R");
        }else if (ema8 < ema3){
            ema8_3_linkedList.add("G");
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
                if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]))
                        >= StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]))) {
                    notificationData.put("stockIsRed", "true");
                    notificationData.put("stockName", stockName);
                    String msg = "Your Buy Stock " + stockName + "'s EMA is RED, Have a look once.";
                    notificationData.put("msg", msg);
                    String subject = "RED: This is " + stockName + " Stock Alert.....";
                    notificationData.put("subject", subject);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return notificationData;
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
            if (StockUtil.checkStockToRun(stockName)){
                if (allData.size() != 0) {
                    String[] data = allData.get(0);
                    yesterdayEMA.put("DEMA30", Double.parseDouble(data[0]));
                    yesterdayEMA.put("DEMA9", Double.parseDouble(data[1]));
                    yesterdayEMA.put("DEMA5", Double.parseDouble(data[2]));
                    yesterdayEMA.put("EMA8", Double.parseDouble(data[3]));
                    yesterdayEMA.put("EMA3", Double.parseDouble(data[4]));
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
                    yesterdayEMA.put("EMA30", Double.parseDouble(data[0]));
                    yesterdayEMA.put("EMA9", Double.parseDouble(data[1]));
                } else {
                    yesterdayEMA.put("EMA30", 0.0);
                    yesterdayEMA.put("EMA9", 0.0);
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
                double yesDayOpenCloseLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[4])) < StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[1])) ?
                        StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[4])) : StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(yesDayData[1]));
                if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(todaysData[4]))
                        < yesDayOpenCloseLow) {
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
        String[] header = {"EMA30","EMA9"};
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            FileReader filereader = new FileReader(file);

            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            FileWriter outputfile = new FileWriter(file, false);
            CSVWriter writer = new CSVWriter(outputfile);
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

    public static String getDateWithFormat(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public static String getDateWithFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
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
            byte dataBuffer[] = new byte[1024];
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
            Date tody = new SimpleDateFormat("dd/MM/yyyy").parse(today);
            Date resDay = new SimpleDateFormat("dd/MM/yyyy").parse(s);
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

    public static boolean checkStockToRun(String stockName) {
        boolean flag = false;
        if (stockName.equals("^NSEI") || stockName.equals("^NSEBANK")
                || Character.compare(stockName.charAt(0),'D')==0 || Character.compare(stockName.charAt(0),'E')==0
                || Character.compare(stockName.charAt(0),'C')==0)
            flag = true;
        return flag;
    }
}
