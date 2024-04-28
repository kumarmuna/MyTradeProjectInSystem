package manas.muna.trade.storeapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import manas.muna.trade.api.model.Stockdata;
import manas.muna.trade.api.model.StockdataPrimaryKey;
import manas.muna.trade.api.model.Tradedata;
import manas.muna.trade.api.model.Volumedata;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.FutureStock;
import manas.muna.trade.vo.StockDetails;

import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PrepareStockdataStoreToDBJob {
    static ObjectMapper objectMapper = new ObjectMapper();
    public static void prepareStockDataAndStoreToDB() {
        try {
//            Map<String, FutureStock> stockMap = readTopHighLowStocks();
            Map<String, FutureStock> stockMap = finalizeStocks();
            boolean dataStore = false;
            for (String stockName : stockMap.keySet()) {
                dataStore = false;
                FutureStock futureStock = stockMap.get(stockName);
                List<Integer> vols = getVolumeHistory(stockName,5);
                Map<String, Boolean> validateEma = validateEma(stockName);
                String reportDataLoc = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024\\"+stockName;
                List<String[]> reportData = StockUtil.readFileData(reportDataLoc);
                String[] todayReport = reportData.get(reportData.size()-1);
                calculateExpectedMoveData();
                prepareTradeData();
//                System.out.println(stockName+"-rsi-"+todayReport[9]+""+futureStock.getExctMrktDirection());
                if (futureStock.getExctMrktDirection().equalsIgnoreCase("UP") ||
                        futureStock.getExctMrktDirection().equalsIgnoreCase("Not DOWN")){
                    if (Double.parseDouble(todayReport[9]) < 40){
                        System.out.println(stockName+"---"+futureStock.getExctMrktDirection());
                        dataStore = true;
                    }
                }else if(futureStock.getExctMrktDirection().equalsIgnoreCase("DOWN") ||
                        futureStock.getExctMrktDirection().equalsIgnoreCase("Not UP")) {
                    if (Double.parseDouble(todayReport[9]) > 55){
                        System.out.println(stockName+"---"+futureStock.getExctMrktDirection());
                        dataStore = true;
                    }
                }
                if(dataStore) {
                    Stockdata stockdata = buildRequestJson(stockMap.get(stockName), vols);
                    String requestBody = objectMapper.writeValueAsString(stockdata);
                    HttpResponse<String> response = apiCAll(requestBody, "/addstockdata", "POST");;

                    System.out.println(response.statusCode());
                    System.out.println(response.body());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Map<String, Boolean> validateEma(String stockName) {
        Map<String, Boolean> map = new HashMap<>();
        String emaLoc = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data\\"+stockName+".csv";
        List<String[]> emaHistoryData = StockUtil.readFileData(emaLoc);
        //0 - ema8, 1 - ema3
        for (int i=0;i<3;i++){

        }
        return map;
    }

    private static Stockdata buildRequestJson(FutureStock futureStock, List<Integer> vols) {
        StockdataPrimaryKey key = StockdataPrimaryKey.builder()
                .stockName(futureStock.getStockName())
                .candleType(futureStock.getCandleOccur())
                .highIndicatorPos(findPositionOfIndicator(futureStock.getExctMrktDirection()))
                .date(futureStock.getDate())
                .build();
        Volumedata volumedata = Volumedata.builder()
                .firstDayVol(vols.get(0)).secondDayVol(vols.get(1)).thirdDayVol(vols.get(2)).fourthDayVol(vols.get(3)).fifthDayVol(vols.get(4))
                .build();
        Tradedata tradedata = Tradedata.builder().build();
        return Stockdata.builder()
                .stockdataPrimaryKey(key)
                .open(futureStock.getOpen()).close(futureStock.getClose()).expectedMove(null).tradePosition(null)
                .tradedata(tradedata)
                .volumedata(volumedata)
                .build();
    }

    private static String findPositionOfIndicator(String exctMrktDirection) {
        String pos = exctMrktDirection.split("expctMrkDirction:")[0].trim();
        if (pos.equalsIgnoreCase("Not DOWN"))
            return "LOW";
        else if (pos.equalsIgnoreCase("Not UP"))
            return "UP";
        return "";
    }

    private static void prepareTradeData() {}
    private static void calculateExpectedMoveData() {}
    private static List<Integer> getVolumeHistory(String stockName, int days) {
        List<Integer> vols = new ArrayList<>();
        Path path = Paths.get("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data\\" + stockName + ".csv");
        try {
            FileReader filereader = new FileReader(path.toString());
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();
            Collections.reverse(allData);
            for (int i=0; i<days; i++){
                vols.add(Integer.valueOf(allData.get(i)[6]));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        System.out.println("Volumes "+vols);
        return vols;
    }

    public static void validateDBData() {
//        Map<String, FutureStock> sData = readStockDataFromDB(DateUtil.getYesterdayDate());
        Map<String, FutureStock> sData = readStockDataFromDB("2024-04-25");
        for (String key: sData.keySet()){
            FutureStock futureStock = sData.get(key);
            List<String[]> historyData = StockUtil.loadStockData(key);
            List<String[]> emaData = StockUtil.loadEmaData(key);
            String[] nextDayEma = emaData.get(0);
            String[] sameDayEma = emaData.get(1);
            String[] nextDayData = historyData.get(0);
            double low = futureStock.getClose() < futureStock.getOpen()? futureStock.getClose() : futureStock.getOpen();
            double high = futureStock.getClose() < futureStock.getOpen()? futureStock.getOpen() : futureStock.getClose();
            if (futureStock.getSelectType().equalsIgnoreCase("UP")){
                if (Double.parseDouble(nextDayData[4]) < low
                    && (Double.parseDouble(nextDayEma[0]) < Double.parseDouble(sameDayEma[0])
                        || Double.parseDouble(nextDayData[1])<Double.parseDouble(nextDayEma[1]))){
                        System.out.println(key+"---moving DOWN, you can trade or wait for confirmation");
                        validateRecordAndUpdateDB(sameDayEma, nextDayEma, futureStock);
                }else if (Double.parseDouble(nextDayData[4]) > futureStock.getHigh() && Double.parseDouble(nextDayData[4]) > Double.parseDouble(nextDayData[1])){
                    //delete from table
                    deleteStockRecord(futureStock);
                }
            }else if (futureStock.getSelectType().equalsIgnoreCase("DOWN")){
                if (Double.parseDouble(nextDayData[4]) > high
                        && (Double.parseDouble(nextDayEma[0]) > Double.parseDouble(sameDayEma[0])
                        || Double.parseDouble(nextDayData[1])>Double.parseDouble(nextDayEma[1]))){
                    System.out.println(key+"---moving UP, you can trade or wait for confirmation");
                    validateRecordAndUpdateDB(sameDayEma, nextDayEma, futureStock);
                } else if (Double.parseDouble(nextDayData[4]) < futureStock.getLow() && Double.parseDouble(nextDayData[4])<Double.parseDouble(nextDayData[1])) {
                    //delete from table
                    deleteStockRecord(futureStock);
                }
            }
        }
    }

    private static void deleteStockRecord(FutureStock futureStock) {
        try {
            StockdataPrimaryKey primaryKey = StockdataPrimaryKey.builder()
                    .stockName(futureStock.getStockName())
                    .date(futureStock.getDate())
                    .highIndicatorPos(futureStock.getSelectType())
                    .candleType(futureStock.getCandleOccur())
                    .build();
            apiCAll(objectMapper.writeValueAsString(primaryKey), "/deleteStockData", "POST");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void validateRecordAndUpdateDB(String[] sameDayEma, String[] nextDayEma, FutureStock futureStock) {
        StringBuilder sb = new StringBuilder("/updateStatus?");
        sb.append("stockName=");
        sb.append(futureStock.getStockName());
        sb.append("&");
        sb.append("date=");
        sb.append(futureStock.getDate());
        sb.append("&");
        sb.append("candleType=");
        sb.append(futureStock.getCandleOccur());
        sb.append("&");
        sb.append("status=");
        if (Double.parseDouble(nextDayEma[0]) >= Double.parseDouble(nextDayEma[1])){
            //update DB data status to completed
            if (futureStock.getSelectType().equalsIgnoreCase("UP"))
                sb.append("SELL");
            else if (futureStock.getSelectType().equalsIgnoreCase("DOWN"))
                sb.append("BUY");
        }else if(Double.parseDouble(nextDayEma[0]) < Double.parseDouble(nextDayEma[1])){
            //update DB data status to hold
            sb.append("HOLD");
        }
        apiCAll("{}", sb.toString(),"POST");
    }

    private static Map<String, FutureStock> readStockDataFromDB(String date) {
        HttpResponse<String> response = null;
        Map<String, FutureStock> futureStock = new HashMap<>();
        try{
            String path = "/bydate/"+date;
            response = apiCAll(null, path, "GET");
            if (response != null && response.statusCode()==200){
                Stockdata[] stockdata = objectMapper.readValue(response.body(), Stockdata[].class);
                for (int i=0; i<stockdata.length;i++){
                    Stockdata sData = stockdata[i];
                    futureStock.put(sData.getStockdataPrimaryKey().getStockName(), FutureStock.prepareFutureFromStockData(sData));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return futureStock;
    }

    public static Map<String, FutureStock> finalizeStocks() {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\high_low_stocks";
//        String fileLocation1 = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\expectedMove";
        Map<String, FutureStock> finalizeStocks = new HashMap<>();
        Map<String, String[]> stockList = new HashMap<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
//            int i=0,j = 0;
            int i=0, j=15;
            fileLocation = fileLocation + "\\" + files.get(i);
            System.out.println("Reading frm : " + fileLocation);
            fileterDuplicateStock(stockList, StockUtil.readFileData(fileLocation));
//            files = Files.list(Paths.get(fileLocation1))
//                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
//            files.sort(Comparator.reverseOrder());
//            fileLocation1 = fileLocation1 + "\\" + files.get(j);
//            System.out.println("Reading frm : " + fileLocation1);
//            fileterDuplicateStock(stockList, StockUtil.readFileData(fileLocation1));
            for (String key : stockList.keySet()){
                String[] stock = stockList.get(key);
                FutureStock fs = FutureStock.prepareFutureStockData(stockList.get(key));
                finalizeStocks.put(key, fs);
//                System.out.println("dgdn");
            }
        }catch (Exception e){
            System.out.println("StocksPatternToConfirmTrade extract file failed....");
            System.exit(0);
        }
        return finalizeStocks;
    }

    private static void fileterDuplicateStock(Map<String, String[]> stockList, List<String[]> data) {
        if (!data.isEmpty()){
            for (String[] dt : data){
                String name = null;
                if (dt[0].contains("StockName=")){
                    name = dt[0].split("StockName=")[1].trim();
                }else if(dt[0].contains("Name:")) {
                    name = dt[0].split("Name:")[1].trim();
                }
                if (!stockList.containsKey(name)){
                    stockList.put(name, dt);
                }
            }
        }
    }

    public static HttpResponse<String> apiCAll(String requestBody, String path, String methodType) {
        HttpResponse<String> response = null;
        HttpRequest request = null;
        try {
            if (methodType.equals("POST")) {
                request = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .uri(URI.create("http://localhost:8080/stockdata" + path))
                        .header("Content-Type", "application/json")
                        .build();
            }else if (methodType.equals("GET")){
                request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/stockdata" + path))
                        .header("Content-Type", "application/json")
                        .build();
            }
            response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
    public static void main(String[] args) {
//        getVolumeHistory("3IINFOLTD.NS", 5);
//        prepareStockDataAndStoreToDB();
        validateDBData();
    }

}
