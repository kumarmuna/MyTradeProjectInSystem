package manas.muna.trade.jobs;

import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.Converter;
import manas.muna.trade.util.StockUtil;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.nio.charset.Charset;

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
//        clearHistoryFolder();
        Thread.sleep(1000);
        for(String stockName : StockUtil.loadAllStockNames()){
//            List<String[]> dt = StockUtil.loadStockData(stockName);
//            if(dt.size()!=0)
//                continue;
//        for(String stockName : StockUtil.loadTestStockNames()){
//        for (String stockName : StockUtil.loadStockNames()) {
            System.out.println("Loading for.... "+stockName);
            loadStockHistoryExcel(stockName);
            Thread.sleep(500);
            loadStockHistoryWeeklyExcel(stockName);
            Thread.sleep(500);
            loadStockHistoryMonthlyExcel(stockName);
            Thread.sleep(500);
        }
        System.out.println("StoreStockHistoryToCvsJob started.......");
        System.out.println("Removing Null data from History Data started.......");
//        Thread.sleep(500);
        removeNullDataFromHIstory();
        System.out.println("Removing Null data from History Data End.......");
    }

    public static void testexecute() throws Exception{
        System.out.println("StoreStockHistoryToCvsJob started.......");
//        clearHistoryFolder();
        Thread.sleep(1000);
//        for(String stockName : StockUtil.loadTestStockNames()){
        for (String stockName : StockUtil.loadAllStockNames()) {
            System.out.println("Loading for.... "+stockName);
//            loadStockHistoryExcel(stockName);
            StoreStockHistoryToCvsJob.loadStockHistoryWeeklyExcel(stockName);
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

    public static void loadStockHistoryExcel(String stockName) {
//        String mainURL = "https://uk.finance.yahoo.com/quote/"+symbol+"/history";
//        String baseUrl = "https://query1.finance.yahoo.com/v7/finance/download/";
//        StringBuilder url = new StringBuilder();
//        url.append(baseUrl);
//        url.append(stockName);
//        url.append("?");
//        url.append("period1="+getEndtTime());
//        url.append("&period2="+getStartTime());
//        url.append("&interval=1d&events=history&includeAdjustedClose=true");
//        String url_string = url.toString();
        String url = "https://query1.finance.yahoo.com/v7/finance/chart/"+stockName+"?range=6mo&interval=1d&indicators=quote&includeTimestamps=true";
        stockName = stockName.replace(".NS", ".BSE");
        String alphavantageUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+stockName+"&outputsize=full&apikey=PVL3HV3MXZ00XQBU";
        stockName = stockName.replace(".BSE", ".NS");
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
        try {
            System.out.println(url);
//            InputStream is = new URL(url).openStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = Converter.readAll(rd);
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
//                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705)")
//                    .headers("Content-Type", "application/json","User-Agent","curl/7.68.0\r\n")
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            String jsonText = response.body();
            Converter.convertJsonToCsv(jsonText, path, "", "Time Series (Daily)");
        }catch (Exception e){
            CandleUtil.filedStockNames.add(stockName);
            e.printStackTrace();
        }
//        try (BufferedInputStream in = new BufferedInputStream(new URL(url.toString()).openStream());
//             FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
//            byte dataBuffer[] = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
//                fileOutputStream.write(dataBuffer, 0, bytesRead);
//            }
//            fileOutputStream.flush();
//            Thread.sleep(4000);
//        } catch (Exception e) {
//            CandleUtil.filedStockNames.add(stockName);
//            e.printStackTrace();
//        }
    }

    public static void loadStockHistoryWeeklyExcel(String stockName) {
        String baseUrl = "https://query2.finance.yahoo.com/v10/finance/download/";
//        StringBuilder url = new StringBuilder();
//        url.append(baseUrl);
//        url.append(stockName);
//        url.append("?");
//        url.append("period1="+getEndtTimeForWK());
////        url.append("&period2="+getStartTime());
//        url.append("&period2="+getWeekStartTime());
//        url.append("&interval=1wk&events=history&includeAdjustedClose=true");
        String url = "https://query1.finance.yahoo.com/v7/finance/chart/"+stockName+"?range=6mo&interval=1wk&indicators=quote&includeTimestamps=true";
        stockName = stockName.replace(".NS", ".BSE");
        String alphavantageUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol="+stockName+"&apikey=PVL3HV3MXZ00XQBU";
        stockName = stockName.replace(".BSE", ".NS");
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data_weekly\\"+stockName+".csv");
        try {
//            InputStream is = new URL(url).openStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = Converter.readAll(rd);
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
//                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705)")
//                    .headers("Content-Type", "application/json","User-Agent","curl/7.68.0\r\n")
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            String jsonText = response.body();
            Converter.convertJsonToCsv(jsonText, path, "", "Weekly Time Series");
        }catch (Exception e){
            CandleUtil.filedStockNames.add(stockName);
            e.printStackTrace();
        }
//        try (BufferedInputStream in = new BufferedInputStream(new URL(url.toString()).openStream());
//             FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
//            byte dataBuffer[] = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
//                fileOutputStream.write(dataBuffer, 0, bytesRead);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void loadStockHistoryMonthlyExcel(String stockName) {
//        String baseUrl = "https://query1.finance.yahoo.com/v7/finance/download/";
//        StringBuilder url = new StringBuilder();
//        url.append(baseUrl);
//        url.append(stockName);
//        url.append("?");
//        url.append("period1="+getEndtTimeForMonthly());
//        url.append("&period2="+getStartTime());
//        url.append("&interval=1mo&events=history&includeAdjustedClose=true");
//        String url_string = url.toString();
        String url = "https://query1.finance.yahoo.com/v7/finance/chart/"+stockName+"?range=6mo&interval=1mo&indicators=quote&includeTimestamps=true";
        stockName = stockName.replace(".NS", ".BSE");
        String alphavantageUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol="+stockName+"&apikey=PVL3HV3MXZ00XQBU";
        stockName = stockName.replace(".BSE", ".NS");
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data_monthly\\"+stockName+".csv");
        try {
//            InputStream is = new URL(url).openStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = Converter.readAll(rd);
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
//                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705)")
//                    .headers("Content-Type", "application/json","User-Agent","curl/7.68.0\r\n")
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            String jsonText = response.body();
            Converter.convertJsonToCsv(jsonText, path, "", "Monthly Time Series");
        }catch (Exception e){
            CandleUtil.filedStockNames.add(stockName);
            e.printStackTrace();
        }
//        try (BufferedInputStream in = new BufferedInputStream(new URL(url.toString()).openStream());
//             FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
//            byte dataBuffer[] = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
//                fileOutputStream.write(dataBuffer, 0, bytesRead);
//            }
//        } catch (Exception e) {
//            System.out.println(url.toString());
//            e.printStackTrace();
//        }
    }

    private static Calendar getCurrentDate(){
        Date dateNow = new Date();
        //let's date is 18th then -minus 3 days means 14th
//        Date daysAgo = new DateTime(dateNow).minusDays(1).toDate();
        //comment below one when running for any date manually
        Date daysAgo = new DateTime(dateNow).plusDays(1).toDate();
//        Date daysAgo = new DateTime(dateNow).toDate();
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
//        calendar.set(2024,3,24);
        String datePattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        Date dateTime = calendar.getTime();
        String dateTimeIn24Hrs = simpleDateFormat.format(dateTime);
        System.out.println("Start:"+dateTimeIn24Hrs);
        String date = dateTimeIn24Hrs;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(date, formatter);
        return dt.toEpochSecond(ZoneOffset.UTC);
    }

    static Long getWeekStartTime() {
        Calendar calendar = getCurrentDate();
        String datePattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        Date dateTime = calendar.getTime();
        String dateTimeIn24Hrs = simpleDateFormat.format(dateTime);
        System.out.println(dateTimeIn24Hrs);
        String date = dateTimeIn24Hrs;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(date, formatter);
        if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            dt = dt.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        }
        return dt.toEpochSecond(ZoneOffset.UTC);
    }

    private static Long getEndtTime() {
        Calendar calendar = getCurrentDate();
        String datePattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        calendar.add(Calendar.DATE, -150);
//        calendar.set(2024, 0,1);
        Date dateTime = calendar.getTime();
        String dateTimeIn24Hrs = simpleDateFormat.format(dateTime);
        System.out.println("End:"+dateTimeIn24Hrs);
        String date = dateTimeIn24Hrs;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(date, formatter);
        return dt.toEpochSecond(ZoneOffset.UTC);
    }

    private static Long getEndtTimeForWK() {
        Calendar calendar = getCurrentDate();
        String datePattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        calendar.add(Calendar.DATE, -250);
//        calendar.set(2024, 0,1);
        Date dateTime = calendar.getTime();
        String dateTimeIn24Hrs = simpleDateFormat.format(dateTime);
        System.out.println("End:"+dateTimeIn24Hrs);
        String date = dateTimeIn24Hrs;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(date, formatter);
        return dt.toEpochSecond(ZoneOffset.UTC);
    }

    private static Long getEndtTimeForMonthly() {
        Calendar calendar = getCurrentDate();
        String datePattern = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        calendar.add(Calendar.DATE, -365);
//        calendar.set(2024, 0,1);
        Date dateTime = calendar.getTime();
        String dateTimeIn24Hrs = simpleDateFormat.format(dateTime);
        System.out.println("End:"+dateTimeIn24Hrs);
        String date = dateTimeIn24Hrs;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dt = LocalDateTime.parse(date, formatter);
        return dt.toEpochSecond(ZoneOffset.UTC);
    }

    private static void removeNullDataFromHIstory() {
        System.out.println("Started to remove null values from History Data");
        String path = "";
        for (String stockName : StockUtil.loadAllStockNames()) {
            path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv";
            System.out.println("Loading for.... "+stockName);
            StockUtil.removeNullDataUpdateFile(stockName, path);
        }
        System.out.println("End to remove null values from History Data");
    }
}
