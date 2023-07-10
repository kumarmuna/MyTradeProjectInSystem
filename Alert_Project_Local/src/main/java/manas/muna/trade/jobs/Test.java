package manas.muna.trade.jobs;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Test {
    public static void main(String[] args) {
        System.out.println("running......");
        System.out.println(loadStockNames()[0]);
    }

    public static String[] loadStockNames() {
        Properties p = new Properties();
        try {
            Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stock.properties");
            FileReader reader = new FileReader(path.toString());
            p.load(reader);
            p.getProperty("stock_list");
        }catch (Exception e){
            e.printStackTrace();
        }
        return p.getProperty("stock_list").split(",");
    }
}
