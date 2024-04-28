package manas.muna.trade.jobs;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import manas.muna.trade.util.StockUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadResultsDateDataJob {
    public static void main(String[] args) {
        validateIsStockResultDateRecently("PAKKA");
    }

    static Map<String, String> resultCalander = new HashMap<>();
    static {
        List<String[]> data = readCSVData("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\Results-2024.csv");
        for (String[] dt : data){
            resultCalander.put(dt[1], dt[2]);
        }
    }

    public static Map<String, String> getResultCalander(){
        return resultCalander;
    }
    public static boolean validateIsStockResultDateRecently(String stockName){
        boolean flag = false;
        Map<String, String> resCalander = getResultCalander();
        if (resCalander.get(stockName) != null) {
            String today = new SimpleDateFormat("dd MMMM yyyy").format(new Date());
            long daysDiff = StockUtil.daysGapInTwoDates(today, resCalander.get(stockName));
            if (daysDiff <= 2)
                flag = true;

        }
        return flag;
    }
    public static List<String[]> readCSVData(String path) {
        List<String[]> allData = new ArrayList<>();
        try {
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            FileReader filereader = new FileReader(file);

            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            allData = csvReader.readAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        return allData;
    }
}
