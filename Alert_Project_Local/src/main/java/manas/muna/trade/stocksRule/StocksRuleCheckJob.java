package manas.muna.trade.stocksRule;

import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.StockRules;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StocksRuleCheckJob {

    public static Map<String, StockRules> stockRulesData = new HashMap<>();
    static {
        String readFileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_rule_for_indivitual";
        try{
            List<String> files = Files.list(Paths.get(readFileLocation))
                    .map(path -> path.getFileName().toFile().getName()).collect(Collectors.toList());
            List<String> testFiles = List.of(new String[]{"AGI.NS.csv"});
            for (String file : testFiles) {
//            for (String file : files) {
                List<String[]> stockData = StockUtil.readFileData(readFileLocation+"\\"+file);
                if (stockData !=null && stockData.size()>0) {
                    StockRules stockRules = CandleUtil.prepareStockRulesData(file, stockData);
                    stockRulesData.put(file, stockRules);
                    System.out.println("");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

    }
}
