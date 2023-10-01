package manas.muna.trade.jobs;

import manas.muna.trade.util.StockUtil;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class StoreStockHistoryToCvsJob {
//    public static void main(String args[]) {
//
////        https://query1.finance.yahoo.com/v7/finance/download/ITC.NS?period1=1629072000&period2=1663372800&interval=1d&events=history&includeAdjustedClose=true
//
//        clearHistoryFolder();
//        for (String stockName : StockUtil.loadStockNames()) {
//            System.out.println("Loading for.... "+stockName);
//            loadStockHistoryExcel(stockName);
//        }
//    }

    public static void execute() throws Exception{
        System.out.println("StoreStockHistoryToCvsJob started.......");
        clearHistoryFolder();
        Thread.sleep(1000);
        for(String stockName : StockUtil.loadAllStockNames()){
//        for (String stockName : StockUtil.loadStockNames()) {
            System.out.println("Loading for.... "+stockName);
            loadStockHistoryExcel(stockName);
        }
        System.out.println("StoreStockHistoryToCvsJob started.......");
    }

    public static void testexecute() throws Exception{
        System.out.println("StoreStockHistoryToCvsJob started.......");
//        clearHistoryFolder();
        Thread.sleep(1000);
        for(String stockName : StockUtil.loadTestStockNames()){
//        for (String stockName : StockUtil.loadStockNames()) {
            System.out.println("Loading for.... "+stockName);
            loadStockHistoryExcel(stockName);
        }
        System.out.println("StoreStockHistoryToCvsJob started.......");
    }

    private static void clearHistoryFolder() {
        try {
            //File file = new File("D:\\share-market\\history_data");
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data");
            FileUtils.cleanDirectory(path.toFile());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void loadStockHistoryExcel(String stockName) {
        String baseUrl = "https://query1.finance.yahoo.com/v7/finance/download/";
        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        url.append(stockName);
        url.append("?");
        url.append("period1="+getEndtTime());
        url.append("&period2="+getStartTime());
        url.append("&interval=1d&events=history&includeAdjustedClose=true");
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
//        System.out.println("URL = "+url);
//        URL url1 = null;
//        try (BufferedInputStream in = new BufferedInputStream(new URL(url.toString()).openStream());
//             FileOutputStream fileOutputStream = new FileOutputStream("D:\\share-market\\history_data\\"+stockName+".csv")) {
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

    private static Calendar getCurrentDate(){
        Date dateNow = new Date();
        //let's date is 18th then -minus 3 days means 14th
//        Date daysAgo = new DateTime(dateNow).minusDays(0).toDate();
        //comment below one when running for any date manually
        Date daysAgo = new DateTime(dateNow).plusDays(1).toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(daysAgo);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private static Long getStartTime() {
        Calendar calendar = getCurrentDate();
        String datePattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        Date dateTime = calendar.getTime();
        String dateTimeIn24Hrs = simpleDateFormat.format(dateTime);
        System.out.println(dateTimeIn24Hrs);
        String date = dateTimeIn24Hrs;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(date, formatter);
        return dt.toEpochSecond(ZoneOffset.UTC);
    }

    private static Long getEndtTime() {
        Calendar calendar = getCurrentDate();
        String datePattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        calendar.add(Calendar.DATE, -45);
        Date dateTime = calendar.getTime();
        String dateTimeIn24Hrs = simpleDateFormat.format(dateTime);
        System.out.println(dateTimeIn24Hrs);
        String date = dateTimeIn24Hrs;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(date, formatter);
        return dt.toEpochSecond(ZoneOffset.UTC);
    }
}
