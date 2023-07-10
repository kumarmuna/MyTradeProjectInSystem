package manas.muna.trade.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
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
    }

    public static Set<String> loadAllStockNames() {
        String[] keys1 = {"a_stock_list","b_stock_list","c_stock_list","d_stock_list","e_stock_list","f_stock_list","g_stock_list","h_stock_list","i_stock_list"
                ,"j_stock_list","k_stock_list","l_stock_list","m_stock_list","n_stock_list","o_stock_list","p_stock_list","q_stock_list","r_stock_list"
                ,"s_stock_list","t_stock_list","u_stock_list","v_stock_list","w_stock_list","x_stock_list","y_stock_list","z_stock_list"};
        String[] keys = {"index_list","a_stock_list"};
//        String[] keys = {"b_stock_list"};
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
            File file = new File(stockEmaDataLoad);
            if (!file.exists())
                file.createNewFile();
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            for (String[] data : allData){
//                if (countDay < 3){
                if (!data[1].equals("null")) {
                    if (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[0]))
                            < StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(data[1]))) {
                        stockIsGreen++;
                    } else {
                        stockIsRed++;
                    }
//                    countDay++;
//                }else
//                    break;
                }
            }
            Map<String,Boolean> fiveDatHighLowData = checkBreakLastFiveDaysHighLow(stockName);
            if (stockIsGreen >= 1 && stockIsGreen <=3 && fiveDatHighLowData.get("fiveDayHigh") && StockUtil.extraCheckToBuyOrNot(stockName)){
                notificationData.put("stockIsGreen", "true");
                notificationData.put("stockName", stockName);
                String msg = "Stock "+stockName+" is green last 3 days, Have a look once.";
                notificationData.put("msg", msg);
                String subject = "GREEN: This is "+stockName+" Stock Alert.....";
                notificationData.put("subject", subject);
            }
            if (stockIsRed >= 1 && stockIsRed <=3 && fiveDatHighLowData.get("fiveDayLow")){
                notificationData.put("stockIsRed", "true");
                notificationData.put("stockName", stockName);
                String msg = "Stock "+stockName+" is RED last 3 days, Have a look once.";
                notificationData.put("msg", msg);
                String subject = "RED: This is "+stockName+" Stock Alert.....";
                notificationData.put("subject", subject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return notificationData;
    }

    private static Map<String,Boolean> checkBreakLastFiveDaysHighLow(String stockName) {
        boolean breakHigh = false;
        double todaysClose = 0.0;
        double fiveDayHigh = 0.0;
        double fiveDayLow = 0.0;
        Map<String, Boolean> fiveDayHighLow = new HashMap<>();
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
        try {
            FileReader filereader = new FileReader(path.toString());
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            Collections.reverse(allData);
            if (!allData.get(0)[4].equals("null"))
                todaysClose = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(0)[4]));
            double hc;
            for (int i=1; i<=5 ;i++){
                if (!allData.get(i)[4].equals("null")) {
                    if (!allData.get(i)[4].equals("null")) {
                        if (Double.parseDouble(allData.get(i)[4]) < Double.parseDouble(allData.get(i)[1])) {
                            hc = Double.parseDouble(allData.get(i)[1]);
                        } else {
                            hc = Double.parseDouble(allData.get(i)[4]);
                        }
                        if (fiveDayHigh < StockUtil.convertDoubleToTwoPrecision(hc))
                            fiveDayHigh = StockUtil.convertDoubleToTwoPrecision(hc);
                        if (fiveDayLow > StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(i)[3])))
                            fiveDayLow = StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(i)[3]));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (todaysClose >= fiveDayHigh) {
            fiveDayHighLow.put("fiveDayHigh", true);
        }else{
            fiveDayHighLow.put("fiveDayHigh",false);}
        if (todaysClose <= fiveDayLow){
            fiveDayHighLow.put("fiveDayLow", true);}
        else{
            fiveDayHighLow.put("fiveDayLow", false);}

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

    public static Map<String, Double> readPreviousDayEma(String stockEmaDataLoad) {
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
            if (allData.size()!=0) {
                String[] data = allData.get(0);
                yesterdayEMA.put("EMA30", Double.parseDouble(data[0]));
                yesterdayEMA.put("EMA9", Double.parseDouble(data[1]));
            }else {
                yesterdayEMA.put("EMA30", 0.0);
                yesterdayEMA.put("EMA9", 0.0);
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
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
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
            if(open < close){
                highDiff = high-close;
                lowDiff = open-low;
                todaysMovePercent = Math.round(((close-open)/prevClose)*100) ;
            }else {
                highDiff = high-open;
                lowDiff = close-low;
            }
            if (prevHigh < low || (highDiff < lowDiff || todaysMovePercent > 3)){
                flag = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
        boolean flag = false;
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
}
