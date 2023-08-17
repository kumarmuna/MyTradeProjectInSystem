package manas.muna.trade.jobs;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import manas.muna.trade.util.StockUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReadResultsDateDataJob {
    public static void main(String[] args) {
        validateIsStockResultDateRecently("AARTIIND");
    }

    public static boolean validateIsStockResultDateRecently(String stockName){
        boolean flag = false;
        List<String[]> data = readCSVData("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\CF-Event-equities-10-08-2022-to-10-08-2023.csv");
        for (String[] dt : data){
            String today = new Date().toString();
            if (stockName.equalsIgnoreCase(dt[0])){
                long daysDiff = StockUtil.daysGapInTwoDates(today, dt[1]);
                if(daysDiff <=1)
                    flag = true;
            }
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
