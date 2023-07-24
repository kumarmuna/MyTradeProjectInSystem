package manas.muna.trade.util;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StockPropertiesUtil {
    public static Map<String, Boolean> indicators = new HashMap<>();
    public static List<String> optionStockSymbol = new ArrayList<>();
    static {
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\conditional_job_run.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
            Enumeration enu = p.keys();
            while (enu.hasMoreElements()) {
                String key = enu.nextElement().toString();
                indicators.put(key, Boolean.parseBoolean(p.getProperty(key)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static Map<String, Boolean> getIndicatorProps(){
        return indicators;
    }

    public static List<String> getOptionStockSymbol() {
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\option_stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
            List<String> names = Arrays.asList(p.get("option_stock_symbol").toString().split(","));
            optionStockSymbol = names;
        }catch (Exception e){
            e.printStackTrace();
        }
        return optionStockSymbol;
    }
}
