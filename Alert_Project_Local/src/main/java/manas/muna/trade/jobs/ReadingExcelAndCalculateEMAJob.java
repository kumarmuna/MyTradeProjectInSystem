package manas.muna.trade.jobs;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
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
//        for (String stockName : StockUtil.loadTestStockNames()) {
            for (String stockName : StockUtil.loadAllStockNames()) {
                List<String[]> historyData = StockUtil.loadStockData(stockName);
//                if (!historyData.get(0)[0].equals(DateUtil.getYesterdayDate())){
//                if (!historyData.get(0)[0].equals("2024-01-20")){
//                    continue;
//                }
            System.out.println("Loading for.... "+stockName);
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv");
            Path path1 = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv");
            Map<String, Double> yesterdayEMA = null;
            Map<String, Double> todaysEMA = null;
            if (StockUtil.checkNewAddedstock(stockName)){
                yesterdayEMA = StockUtil.readPreviousDayEmaModify(path1.toString(),stockName);
                todaysEMA = readCVSDataModify(path.toString(), yesterdayEMA.get("EMA8"),yesterdayEMA.get("EMA3"),yesterdayEMA.get("EMA26"),yesterdayEMA.get("EMA12"));
                storeTodaysEmaModify(path1.toString(), todaysEMA, stockName);
            }else{
                yesterdayEMA = StockUtil.readPreviousDayEma(path1.toString(),stockName);
                if(StockUtil.checkStockToRunForMACD(stockName)) {
                    todaysEMA = readCVSData(path.toString(), 0.0, 0.0, 0.0,yesterdayEMA.get("EMA8"),yesterdayEMA.get("EMA3"),yesterdayEMA.get("EMA26"),yesterdayEMA.get("EMA12"));
                }else if (StockUtil.checkStockToRun(stockName)){
                    todaysEMA = readCVSData(path.toString(), yesterdayEMA.get("DEMA30"), yesterdayEMA.get("DEMA9"), yesterdayEMA.get("DEMA5"),yesterdayEMA.get("EMA8"),yesterdayEMA.get("EMA3"),0.0,0.0);
                } else {
                    todaysEMA = readCVSData(path.toString(), yesterdayEMA.get("EMA30"), yesterdayEMA.get("EMA9"),0.0,0.0,0.0,0.0,0.0);
                }

                storeTodaysEma(path1.toString(), todaysEMA, stockName);
            }


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
            Map<String, Double> yesterdayEMA = null;
            Map<String, Double> todaysEMA = null;
            if (StockUtil.checkNewAddedstock(stockName)){
                yesterdayEMA = StockUtil.readPreviousDayEmaModify(path1.toString(),stockName);
                todaysEMA = readCVSDataModify(path.toString(), yesterdayEMA.get("EMA8"),yesterdayEMA.get("EMA3"),yesterdayEMA.get("EMA26"),yesterdayEMA.get("EMA12"));
                storeTodaysEmaModify(path1.toString(), todaysEMA, stockName);
            }else{
                yesterdayEMA = StockUtil.readPreviousDayEma(path1.toString(),stockName);
                if(StockUtil.checkStockToRunForMACD(stockName)) {
                    todaysEMA = readCVSData(path.toString(), 0.0, 0.0, 0.0,yesterdayEMA.get("EMA8"),yesterdayEMA.get("EMA3"),yesterdayEMA.get("EMA26"),yesterdayEMA.get("EMA12"));
                }else if (StockUtil.checkStockToRun(stockName)){
                    todaysEMA = readCVSData(path.toString(), yesterdayEMA.get("DEMA30"), yesterdayEMA.get("DEMA9"), yesterdayEMA.get("DEMA5"),yesterdayEMA.get("EMA8"),yesterdayEMA.get("EMA3"),0.0,0.0);
                } else {
                    todaysEMA = readCVSData(path.toString(), yesterdayEMA.get("EMA30"), yesterdayEMA.get("EMA9"),0.0,0.0,0.0,0.0,0.0);
                }

                storeTodaysEma(path1.toString(), todaysEMA, stockName);
            }

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

    private static void storeTodaysEmaModify(String filePath, Map<String, Double> todaysEMA, String stockName) {
        {
            double ema8 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA8"));
            double ema3 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA3"));
            double ema26 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA26"));
            double ema12 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA12"));

            File file = new File(filePath);
            String[] header ;
            boolean headerFlag = false;
            if (ema12 != 0.0 && ema26 != 0.0) {
                headerFlag = true;
                header = new String[]{"EMA8", "EMA3", "EMA26", "EMA12", "MA100", "MA5"};
            }else
                header = new String[]{"EMA8", "EMA3","MA100","MA5"};
            try {
                if (!file.exists()){
                    file.createNewFile();
                }
                FileReader filereader = new FileReader(file);

                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                List<String[]> allData = csvReader.readAll();
                FileWriter outputfile = new FileWriter(file, false);
                CSVWriter writer = new CSVWriter(outputfile);
                if(allData.size() > 27) {
                    allData = allData.stream().limit(27).collect(Collectors.toList());
                }
                List<String[]> stockHistoryData = StockUtil.loadStockData(stockName);
                String[] data;
                if (headerFlag)
                    data = new String[]{Double.toString(ema8), Double.toString(ema3),Double.toString(ema26), Double.toString(ema12),
                            String.valueOf(StockUtil.calculateMovingAvg(100, stockHistoryData)),
                        String.valueOf(StockUtil.calculateMovingAvg(5, stockHistoryData))};
                else
                    data = new String[]{Double.toString(ema8), Double.toString(ema3),
                            String.valueOf(StockUtil.calculateMovingAvg(100, stockHistoryData)),
                            String.valueOf(StockUtil.calculateMovingAvg(5, stockHistoryData))};

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
    }
    private static void storeTodaysEma(String filePath, Map<String, Double> todaysEMA, String stockName) {
        double ema5 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA5"));
        double ema9 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA9"));
        double ema30 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA30"));
        double ema8 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA8"));
        double ema3 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA3"));
        double ema26 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA26"));
        double ema12 = StockUtil.convertDoubleToTwoPrecision(todaysEMA.get("todaysEMA12"));
        //calculate DEMA using EMA
//        ema9 = (2*(ema9*9))-(ema9*(ema9*9));
//        ema30 = (2*(ema30*30))-(ema30*(ema30*30));

        File file = new File(filePath);
        String[] header;
        if (StockUtil.checkStockToRun(stockName) || StockUtil.checkStockToRunForMACD(stockName)) {
            if(StockUtil.checkStockToRunForMACD(stockName)) {
                header = new String[]{"EMA8", "EMA3","EMA26","EMA12"};
            }else if (!StockUtil.checkOnly83(stockName)) {
                header = new String[]{"DEMA30", "DEMA9", "DEMA5", "EMA8", "EMA3"};
            }else
                header = new String[]{"EMA8", "EMA3"};
        }else
            header = new String[]{"EMA30", "EMA9"};
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            FileReader filereader = new FileReader(file);

            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            FileWriter outputfile = new FileWriter(file, false);
            CSVWriter writer = new CSVWriter(outputfile);
            if(allData.size() > 27) {
                allData = allData.stream().limit(27).collect(Collectors.toList());
            }
            List<String[]> stockHistoryData = StockUtil.loadStockData(stockName);
            String[] data;
            if (StockUtil.checkStockToRun(stockName) || StockUtil.checkStockToRunForMACD(stockName))
                if (StockUtil.checkStockToRunForMACD(stockName)) {
                    data = new String[]{Double.toString(ema8),Double.toString(ema3),Double.toString(ema26),Double.toString(ema12),
                            String.valueOf(StockUtil.calculateMovingAvg(100, stockHistoryData)),String.valueOf(StockUtil.calculateMovingAvg(5, stockHistoryData))
                    };
                }else if (!StockUtil.checkOnly83(stockName))
                    data = new String[]{Double.toString(ema30), Double.toString(ema9),Double.toString(ema5),Double.toString(ema8),Double.toString(ema3),
                            String.valueOf(StockUtil.calculateMovingAvg(100, stockHistoryData)),String.valueOf(StockUtil.calculateMovingAvg(5, stockHistoryData))
                    };
                else
                    data = new String[]{Double.toString(ema8),Double.toString(ema3),
                            String.valueOf(StockUtil.calculateMovingAvg(100, stockHistoryData)),String.valueOf(StockUtil.calculateMovingAvg(5, stockHistoryData))
                    };
            else
                data = new String[]{Double.toString(ema30), Double.toString(ema9),
                        String.valueOf(StockUtil.calculateMovingAvg(100, stockHistoryData)),String.valueOf(StockUtil.calculateMovingAvg(5, stockHistoryData))
                };

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

    private static Map<String, Double> readCVSDataModify(String file, double EMA8, double EMA3, double EMA26, double EMA12){
        {
            double ema8 = 0;
            double ema3 = 0;
            double ema26 = 0;
            double ema12 = 0;
            double multiplier8 = StockUtil.convertDoubleToTwoPrecision(2.0/(8+1));
            double multiplier3 = StockUtil.convertDoubleToTwoPrecision(2.0/(3+1));
            double multiplier26 = StockUtil.convertDoubleToTwoPrecision(2.0/(26+1));
            double multiplier12 = StockUtil.convertDoubleToTwoPrecision(2.0/(12+1));
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

                int sz = 0;
                for (String[] row : allData) {
                    if (!row[4].equals("null")){
                        if (EMA8 != 0.0){
                            ema8 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - EMA8) * multiplier8) + EMA8;
                        }
                        if (EMA3 != 0.0){
                            ema3 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - EMA3) * multiplier3) + EMA3;
                        }
                        if (EMA26 != 0.0){
                            ema26 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - EMA26) * multiplier26) + EMA26;
                        }
                        if (EMA12 != 0.0){
                            ema12 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - EMA12) * multiplier12) + EMA12;
                        }
                    }
                    sz++;
                    break;
                }
                todaysEMA.put("todaysEMA8", ema8);
                todaysEMA.put("todaysEMA3", ema3);
                todaysEMA.put("todaysEMA26", ema26);
                todaysEMA.put("todaysEMA12", ema12);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return todaysEMA;
        }
    }
    private static Map<String, Double> readCVSData(String file, double prevDayDEma30, double prevDayDEma9,double prevDayDEma5, double prevDayEma8, double prevDayEma3, double prevDayEma26, double prevDayEma12) {
        double ema30 = 0;
        double ema9 = 0;
        double ema9_1 = 0;
        double ema9_2 = 0;
        double ema30_1 = 0;
        double ema30_2 = 0;
        double ema5 = 0;
        double ema5_1 = 0;
        double ema5_2 = 0;
        double ema8 = 0;
        double ema3 = 0;
        double ema26 = 0;
        double ema12 = 0;
        double multiplier30 = StockUtil.convertDoubleToTwoPrecision(2.0/(30+1));
        double multiplier9 = StockUtil.convertDoubleToTwoPrecision(2.0/(9+1));
        double multiplier5 = StockUtil.convertDoubleToTwoPrecision(2.0/(5+1));
        double multiplier8 = StockUtil.convertDoubleToTwoPrecision(2.0/(8+1));
        double multiplier3 = StockUtil.convertDoubleToTwoPrecision(2.0/(3+1));
        double multiplier26 = StockUtil.convertDoubleToTwoPrecision(2.0/(26+1));
        double multiplier12 = StockUtil.convertDoubleToTwoPrecision(2.0/(12+1));
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
                    ema9_1 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayDEma9) * multiplier9) + prevDayDEma9;
                    ema9_2 = ((StockUtil.convertDoubleToTwoPrecision(ema9_1) - prevDayDEma9) * multiplier9) + prevDayDEma9;
//                    ema9_2 = ((StockUtil.convertDoubleToTwoPrecision(ema9_1) - ema9_1) * multiplier9) + ema9_1;
                    ema9 = (2 * ema9_1) - ema9_2;
//                    ema9 = ema9_1;
//                    double EMA = (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) * multiplier9) + prevDayEma9 * (1-multiplier9);
//                    System.out.println("Ema test "+EMA);
                    ema30_1 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayDEma30) * multiplier30) + prevDayDEma30;
                    ema30_2 = ((StockUtil.convertDoubleToTwoPrecision(ema30_1) - prevDayDEma30) * multiplier30) + prevDayDEma30;
//                    ema30_2 = ((StockUtil.convertDoubleToTwoPrecision(ema30_1) - ema30_1) * multiplier30) + ema30_1;
                    ema30 = (2 * ema30_1) - ema30_2;
//                    ema30 = ema30_1;
//                    double EMA3 = (StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) * multiplier30) + prevDayEma30 * (1-multiplier30);
//                    System.out.println("Ema test 3 "+EMA3);
                    if (prevDayDEma5 != 0.0) {
                        ema5_1 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayDEma5) * multiplier5) + prevDayDEma5;
                        ema5_2 = ((StockUtil.convertDoubleToTwoPrecision(ema5_1) - prevDayDEma5) * multiplier5) + prevDayDEma5;
//                        ema5_2 = ((StockUtil.convertDoubleToTwoPrecision(ema5_1) - ema5_1) * multiplier5) + ema5_1;
                        ema5 = (2 * ema5_1) - ema5_2;
//                        ema5 = ema5_1;
                    }
                    if (prevDayEma8 != 0.0){
                        ema8 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayEma8) * multiplier8) + prevDayEma8;
                    }
                    if (prevDayEma3 != 0.0){
                        ema3 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayEma3) * multiplier3) + prevDayEma3;
                    }
                    if (prevDayEma26 != 0.0){
                        ema26 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayEma26) * multiplier26) + prevDayEma26;
                    }
                    if (prevDayEma12 != 0.0){
                        ema12 = ((StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(allData.get(sz)[4])) - prevDayEma12) * multiplier12) + prevDayEma12;
                    }
                }
                sz++;
                break;
            }
            todaysEMA.put("todaysEMA30", ema30);
            todaysEMA.put("todaysEMA9", ema9);
            todaysEMA.put("todaysEMA5", ema5);
            todaysEMA.put("todaysEMA8", ema8);
            todaysEMA.put("todaysEMA3", ema3);
            todaysEMA.put("todaysEMA26", ema26);
            todaysEMA.put("todaysEMA12", ema12);
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
