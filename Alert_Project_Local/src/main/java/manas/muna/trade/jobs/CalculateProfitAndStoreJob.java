package manas.muna.trade.jobs;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import manas.muna.trade.util.StockUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CalculateProfitAndStoreJob {
    public static void main(String[] args){
        calculate("37.10");
    }

    private static List<String[]> findFirstRowOfProfitData(String filePath) {
        return StockUtil.readFileData(filePath);
    }

    //
    private static void calculate(String todaysClosePrice) {
        try {
            String stockName = "3IINFOLTD.NS";
            String filePath = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\profit_loss\\trade_percentage_data\\"+stockName+".csv";
            String headerData = "Date_Up,Up_Price,Date_Down,Down_price,Percentage,Days";
            String todaysDate = StockUtil.getDateWithFormat("dd/MM/yyyy");
            List<String[]> rowsData = findFirstRowOfProfitData(filePath);
            String[] rowData = rowsData.get(1);
            long dateDiff = TimeUnit.MILLISECONDS.toDays(new Date().getTime() - new SimpleDateFormat("dd/MM/yyyy").parse(rowData[0]).getTime()) % 365;
            double profit = Double.parseDouble(todaysClosePrice) - Double.parseDouble(rowData[1]);
            double movePercetage = StockUtil.convertDoubleToTwoPrecision((profit/Double.parseDouble(rowData[1]))*100);
            rowData[2] = todaysDate;
            rowData[3] = todaysClosePrice;
            rowData[4] = String.valueOf(movePercetage);
            rowData[5] = String.valueOf(dateDiff);
            rowsData.add(1,rowData);
            writeToProfitFile(filePath, rowsData);
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
