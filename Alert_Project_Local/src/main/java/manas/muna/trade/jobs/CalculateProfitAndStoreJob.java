package manas.muna.trade.jobs;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import manas.muna.trade.util.StockUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CalculateProfitAndStoreJob {
    public static void main(String[] args){
//        addStockDataForProfitCalculate("BSOFT.NS");
//        calculateAndUpdateProfitDetails("BSOFT.NS");
    }

    private static List<String[]> findFirstRowOfProfitData(String filePath) {
        return StockUtil.readFileData(filePath);
    }

    public static void addStockDataForProfitCalculate(String stockName, String prevCurr) {
        String filePath = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\profit_loss\\trade_percentage_data\\"+stockName+".csv";
        String historyDataPath = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv";
        List<String[]> rowsData = new ArrayList<>();
//        String todaysDate = StockUtil.getDateWithFormat("dd/MM/yyyy");
        List<String[]> stockData = findFirstRowOfProfitData(historyDataPath);
        Collections.reverse(stockData);
        String[] rowData = new String[6];
        if (prevCurr.equalsIgnoreCase("Yesterday")) {
            rowData[0] = StockUtil.getDateWithFormat(stockData.get(1)[0], "dd/MM/yyyy");
            rowData[1] = String.valueOf(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stockData.get(1)[4])));
        }else {
            rowData[0] = StockUtil.getDateWithFormat(stockData.get(0)[0], "dd/MM/yyyy");
            rowData[1] = String.valueOf(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stockData.get(0)[4])));
        }
        rowData[2] = null;
        rowData[3] = null;
        rowData[4] = null;
        rowData[5] = null;
        File fl  = new File(filePath);
        if (fl.exists()){
            try {
                FileReader fr = new FileReader(fl);
                CSVReader rd = new CSVReader(fr);
                rowsData = rd.readAll();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            rowsData.add("Date_Up,Up_Price,Date_Down,Down_price,Percentage,Days".split(","));
        }
        List<String[]> checkRowsData = findFirstRowOfProfitData(filePath);
        String[] crd = checkRowsData.size()==0? new String[6] : checkRowsData.get(checkRowsData.size()-1);
        if(StringUtil.isBlank(crd[1]) || !StringUtil.isBlank(crd[3]) || !StringUtils.isEmpty(crd[3])) {
            System.out.println("Storing Data to calculate profit......");
            rowsData.add(rowData);
            writeToProfitFile(filePath, rowsData);
        }
    }
    public static void calculateAndUpdateProfitDetails(String stockName) {
        try {
            String filePath = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\profit_loss\\trade_percentage_data\\"+stockName+".csv";
            String historyDataPath = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\"+stockName+".csv";
            File file = new File(filePath);
            if (file.exists()) {
//                String todaysDate = StockUtil.getDateWithFormat("dd/MM/yyyy");
                List<String[]> rowsData = findFirstRowOfProfitData(filePath);
                Collections.reverse(rowsData);
                String[] rowData = rowsData.get(0);
                if (!StringUtil.isBlank(rowData[1])){
                    List<String[]> stockData = findFirstRowOfProfitData(historyDataPath);
                    Collections.reverse(stockData);
                    long dateDiff = TimeUnit.MILLISECONDS.toDays(new Date().getTime() - new SimpleDateFormat("dd/MM/yyyy").parse(rowData[0]).getTime()) % 365;
                    double profit = Double.parseDouble(stockData.get(0)[4]) - Double.parseDouble(rowData[1]);
                    double movePercetage = StockUtil.convertDoubleToTwoPrecision((profit / Double.parseDouble(rowData[1])) * 100);
                    rowData[2] = StockUtil.getDateWithFormat(stockData.get(0)[0], "dd/MM/yyyy");
//                    rowData[2] = todaysDate;
                    rowData[3] = String.valueOf(StockUtil.convertDoubleToTwoPrecision(Double.parseDouble(stockData.get(0)[4])));
                    rowData[4] = String.valueOf(movePercetage);
                    rowData[5] = String.valueOf(dateDiff);
                    Collections.reverse(rowsData);
                    System.out.println("Updating Data to calculate profit......");
                    writeToProfitFile(filePath, rowsData);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void writeToProfitFile(String filePath, List<String[]> data) {
        try {
            File file = new File(filePath);
            if (!file.exists()){
                file.createNewFile();
            }
            FileWriter outputfile = new FileWriter(file, false);
            CSVWriter writer = new CSVWriter(outputfile);
            for (String[] dt : data){
                writer.writeNext(dt);
            }
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
