package manas.muna.trade.stocksRule;

import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.CandleStick;
import manas.muna.trade.vo.StockDetails;
import manas.muna.trade.vo.StockRules;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StocksRuleCreateUpdateJob {

    public static void readConfirmStocksAndPrepareRule() {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day2\\stock_details_"+ DateUtil.getTodayDate()+".csv";
        List<String[]> stockData = StockUtil.readFileData(path);
        for (String[] str: stockData){
            StockDetails stockDetails = StockUtil.prepareCandleData(str);
            if (stockDetails.getStockName().equals("AGI.NS")) {
                createUpdateRule(stockDetails);
            }
//            createUpdateRule(stockDetails);
        }
    }

    private static void createUpdateRule(StockDetails stockDetails) {
        String locationOfFile = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_rule_for_indivitual";
        String filePath = "";
        String fileName = stockDetails.getStockName()+".csv";
        try {
            List<String> files = Files.list(Paths.get(locationOfFile))
                    .map(path -> path.getFileName().toFile().getName()).collect(Collectors.toList());
            if (!files.contains(fileName)){
                filePath = locationOfFile+"\\"+fileName;
                File file = new File(filePath);
                file.createNewFile();
            }
            List<String[]> stockData = StockUtil.readFileData(locationOfFile+"\\"+fileName);
            List<String[]> modifiedRule = modifyRule(stockData, stockDetails);
            CandleUtil.storeDataToCSVFileWithArrayData(locationOfFile+"\\"+fileName,modifiedRule);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static List<String[]> modifyRule(List<String[]> stockData,StockDetails stockDetails) {
        List<String[]> modifyRule = new ArrayList<>();
        StringBuilder redToGreen = new StringBuilder();
        StringBuilder greenToRed = new StringBuilder();
        redToGreen.append("RedToGreen");
        redToGreen.append("=");
        greenToRed.append("GreenToRed");
        greenToRed.append("=");
        if (stockData!=null && stockData.size()!=0){
            for (String[] rule: stockData){
                String[] existingRule = rule[0].split("=");
                if (existingRule[0].equals("RedToGreen") && stockDetails.getIsGreenRed().equals("RED")){
                    redToGreen.append(existingRule[1]+":"+CandleUtil.removeLastCharFromString(stockDetails.getCandleTypesOccur()));
                    redToGreen.append("-");
                    redToGreen.append("UP");
                }else if(existingRule[0].equals("GreenToRed") && stockDetails.getIsGreenRed().equals("GREEN")){
                    greenToRed.append(existingRule[1]+":"+CandleUtil.removeLastCharFromString(stockDetails.getCandleTypesOccur()));
                    greenToRed.append("-");
                    greenToRed.append("DOWN");
                }else if(existingRule[0].equals("GreenToRed") && stockDetails.getIsGreenRed().equals("RED")){
                    greenToRed.append(existingRule[1]);
                }else if(existingRule[0].equals("RedToGreen") && stockDetails.getIsGreenRed().equals("GREEN")){
                    redToGreen.append(existingRule[1]);
                }
            }
        }else {
            if (stockDetails.getIsGreenRed().equals("RED")){
                redToGreen.append(CandleUtil.removeLastCharFromString(stockDetails.getCandleTypesOccur()));
                redToGreen.append("-");
                redToGreen.append("UP");
            }else if (stockDetails.getIsGreenRed().equals("GREEN")){
                greenToRed.append(CandleUtil.removeLastCharFromString(stockDetails.getCandleTypesOccur()));
                greenToRed.append("-");
                greenToRed.append("DOWN");
            }
        }
        modifyRule.add(new String[]{redToGreen.toString()});
        modifyRule.add(new String[]{greenToRed.toString()});
        return modifyRule;
    }

    public static void main(String[] args) {
        readConfirmStocksAndPrepareRule();
    }
}
