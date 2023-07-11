package manas.muna.trade.jobs;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import manas.muna.trade.util.StockUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadingExcelAndCalculateEMAJob {
//    public static void main(String args[]) {
//        for (String stockName : StockUtil.loadStockNames()) {
//            System.out.print("Loading for.... "+stockName);
//            Map<String, Double> todaysEMA = readCVSData("D:\\share-market\\history_data\\"+stockName+".csv", 51.83, 51.90);
//            storeTodaysEma("D:\\share-market\\history_ema_data\\"+stockName+".csv", todaysEMA);
//        }
//    }

    public static void execute() {
        System.out.println("ReadingExcelAndCalculateEMAJob started.......");
//        for (String stockName : StockUtil.loadStockNames()) {
            for (String stockName : StockUtil.loadAllStockNames()) {
            System.out.println("Loading for.... "+stockName);
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
            Path path1 = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
            Map<String, Double> yesterdayEMA = StockUtil.readPreviousDayEma(path1.toString());
            Map<String, Double> todaysEMA = readCVSData(path.toString(), yesterdayEMA.get("EMA30"), yesterdayEMA.get("EMA9"));
            storeTodaysEma(path1.toString(), todaysEMA);

//            Double todaysClose = readCVSData(path.toString());
//            Path path2 = Paths.get("D:\\share-market\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
//            storeTodaysClose(path2.toString(), todaysClose);
        }
        System.out.println("ReadingExcelAndCalculateEMAJob end.......");
    }

    public static void testexecute() {
        System.out.println("ReadingExcelAndCalculateEMAJob started.......");
        for (String stockName : StockUtil.loadTestStockNames()) {
//        for (String stockName : StockUtil.loadAllStockNames()) {
            System.out.println("Loading for.... "+stockName);
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
            Path path1 = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
            Map<String, Double> yesterdayEMA = StockUtil.readPreviousDayEma(path1.toString());
            Map<String, Double> todaysEMA = readCVSData(path.toString(), yesterdayEMA.get("EMA30"), yesterdayEMA.get("EMA9"));
            storeTodaysEma(path1.toString(), todaysEMA);

//            Double todaysClose = readCVSData(path.toString());
//            Path path2 = Paths.get("D:\\share-market\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
//            storeTodaysClose(path2.toString(), todaysClose);
        }
        System.out.println("ReadingExcelAndCalculateEMAJob end.......");
    }
    private static void storeTodaysClose(String path, Double todaysClose) {

        File file = new File(path);
        String[] header = {"PREVIOUS","TODAY"};
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            FileReader filereader = new FileReader(file);

            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            FileWriter outputfile = new FileWriter(file, false);
            CSVWriter writer = new CSVWriter(outputfile);
            String updatePerv = allData.get(0)[1];
            String[] data = {updatePerv, Double.toString(todaysClose)};
            allData.add(0,data);
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

    private static void storeTodaysEma(String filePath, Map<String, Double> todaysEMA) {
        double ema9 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA9"));
        double ema30 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA30"));
        //calculate DEMA using EMA
//        ema9 = (2*(ema9*9))-(ema9*(ema9*9));
//        ema30 = (2*(ema30*30))-(ema30*(ema30*30));

        File file = new File(filePath);
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
            if(allData.size() > 4) {
                allData = allData.stream().limit(4).collect(Collectors.toList());
            }
            String[] data = {Double.toString(ema30), Double.toString(ema9)};
            allData.add(0,data);
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

    private static Map<String, Double> readCVSData(String file, double prevDayEma30, double prevDayEma9) {
        double ema30 = 0;
        double ema9 = 0;
        double ema9_1 = 0;
        double ema9_2 = 0;
        double ema30_1 = 0;
        double ema30_2 = 0;
        double multiplier30 = StockUtil.convertDoubleToTwoPrecision(2.0/(30+1));
        double multiplier9 = StockUtil.convertDoubleToTwoPrecision(2.0/(9+1));
        Map<String, Double> todaysEMA = new HashMap<>();
        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            Collections.reverse(allData);
            //4th index column is Close
            int count = 0;
            double sum = 0;

//            for (String[] row : allData) {
//                if (count <= 30){
//                //System.out.print(row[4]);
//                if (!row[4].equals("null")){
//                    sum = sum + Double.parseDouble(row[4]);
//                    if (count == 9){
//                        ema9 = (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(0)[4])) - prevDayEma9) * multiplier9 + prevDayEma9;
//                        System.out.println("Ema9 "+ema9);
//                    }
//                    count++;
//                 }
//                }else
//                    break;
//            }
//            ema30 = (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(0)[4])) - prevDayEma30) * multiplier30 + prevDayEma30;
//            System.out.println("Ema30 "+ema30);
//

            int sz = 0;
            for (String[] row : allData) {
                if (!row[4].equals("null")){
                    ema9_1 = (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayEma9) * multiplier9 + prevDayEma9;
                    ema9_2 = (StockUtil.convertDoubleToTwoPrecision(ema9_1) - prevDayEma9) * multiplier9 + prevDayEma9;
                    ema9 = (2 * ema9_1) - ema9_2;
//                    double EMA = (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) * multiplier9) + prevDayEma9 * (1-multiplier9);
//                    System.out.println("Ema test "+EMA);
                    ema30_1 = (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayEma30) * multiplier30 + prevDayEma30;
                    ema30_2 = (StockUtil.convertDoubleToTwoPrecision(ema30_1) - prevDayEma30) * multiplier30 + prevDayEma30;
                    ema30 = (2 * ema30_1) - ema30_2;
//                    double EMA3 = (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) * multiplier30) + prevDayEma30 * (1-multiplier30);
//                    System.out.println("Ema test 3 "+EMA3);
                }
                sz++;
                break;
            }
            todaysEMA.put("todaysEMA30", ema30);
            todaysEMA.put("todaysEMA9", ema9);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return todaysEMA;
    }

    private static Double readCVSData(String file) {
        double todaysClose = 0.0;
        try {
            FileReader filereader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            Collections.reverse(allData);
            //4th index column is Close
            todaysClose = Double.parseDouble(allData.get(0)[4]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return todaysClose;
    }
}
