package manas.muna.trade.stocksRule;

import manas.muna.trade.util.CandleUtil;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.StockDetails;
import manas.muna.trade.vo.StockRules;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class StocksRuleCheckJob {

    public static Map<String, StockRules> stockRulesData = new HashMap<>();
    static {
        String readFileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_rule_for_indivitual";
        try{
            List<String> files = Files.list(Paths.get(readFileLocation))
                    .map(path -> path.getFileName().toFile().getName()).collect(Collectors.toList());
            List<String> testFiles = List.of(new String[]{"AGI.NS.csv"});
//            for (String file : testFiles) {
            for (String file : files) {
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

    public static String stockRuleChecker(String stockName, String candleKey, String marketMovement){
        String marketMove = "";
        if (!stockName.contains(".csv"))
            stockName = stockName+".csv";
        if(stockRulesData.containsKey(stockName)){
            StockRules stockRules = stockRulesData.get(stockName);
            Map<String, Map<String,String>> rules = stockRules.getRules();
            Map<String,String> marketBasedRule = rules.get(marketMovement);
            if (marketBasedRule.containsKey(candleKey)){
                marketMove = marketBasedRule.get(candleKey);
            }
            System.out.println(marketMove);
        }
        return marketMove;
    }

    public static void getValidWithRule() {
        String readFileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\day4";
        List<String[]> filterStocks = new ArrayList<>();
        try {
            List<String> files = Files.list(Paths.get(readFileLocation))
                    .map(path -> path.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            readFileLocation = readFileLocation+"\\"+files.get(0);
            List<String[]> stockData = StockUtil.readFileData(readFileLocation);
            for (String[] str: stockData){
                StockDetails stockDetails = StockUtil.prepareCandleData(str);
                String movement = "";
                if (stockDetails.getIsGreenRed().equals("GREEN")) {
                    movement = "GreenToRed";
                    if(stockRuleChecker(stockDetails.getStockName(), stockDetails.getCandleTypesOccur(), movement).equals("DOWN"))
                        filterStocks.add(str);
                }else if (stockDetails.getIsGreenRed().equals("RED")) {
                    movement = "RedToGreen";
                    if(stockRuleChecker(stockDetails.getStockName(), stockDetails.getCandleTypesOccur(), movement).equals("UP"))
                        filterStocks.add(str);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(filterStocks);
    }

    public static void main(String[] args) {
//        stockRuleChecker("AFFLE.NS", "Dojis", "GreenToRed");
        getValidWithRule();
    }
}
