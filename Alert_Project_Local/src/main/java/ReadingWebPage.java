import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

public class ReadingWebPage {
    public static void main(String args[]) throws IOException {
        String page = "https://ca.finance.yahoo.com/quote/ITC.NS/history?p=ITC.NS&.tsrc=fin-srch";
        //Connecting to the web page
        Connection conn = Jsoup.connect(page);
        //executing the get request
        Document doc = conn.get();
        //Retrieving the contents (body) of the web page
        String result = doc.body().text();
        String tableData = result.substring(result.indexOf("Date Open"), result.lastIndexOf("*Close price"));
        System.out.println(tableData);
        writeToCSV(tableData);
    }

    private static void writeToCSV(String tableData) {
        try (CSVWriter writer = new CSVWriter(new FileWriter("D:\\share-market\\history_data\\test.csv"))) {
            writer.writeAll(Collections.singleton(tableData.split(" ")));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}