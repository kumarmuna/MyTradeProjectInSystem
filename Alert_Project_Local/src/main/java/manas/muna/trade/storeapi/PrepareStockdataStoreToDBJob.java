package manas.muna.trade.storeapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import manas.muna.trade.api.model.*;
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
            List<String[]> marketRunDate = getStockHistory();
            String lastMarktRunDate = marketRunDate.get(0)[0];
//            Map<String, FutureStock> stockMap = readTopHighLowStocks();
//            String lastMarktRunDate = "2024-05-02";
            Map<String, FutureStock> stockMap = finalizeStocks(lastMarktRunDate);
            boolean dataStore = false;
            for (String stockName : stockMap.keySet()) {
                if (!stockName.equals("ZODIACLOTH.NS"))
                    continue;
                dataStore = false;
                FutureStock futureStock = stockMap.get(stockName);
                List<Integer> vols = getVolumeHistory(stockName,5);
                Map<String, Boolean> validateEma = validateEma(stockName);
                String reportDataLoc = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024\\"+stockName;
                List<String[]> reportData = StockUtil.readFileData(reportDataLoc);
                String[] todayReport = reportData.get(reportData.size()-1);
//                String[] todayReport = reportData.get(reportData.size()-3);
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
        Tradedata tradedata = Tradedata.builder().buyabove(calculateBuy(futureStock)).selllower(calculateSell(futureStock)).build();
        return Stockdata.builder()
                .stockdataPrimaryKey(key)
                .open(futureStock.getOpen()).close(futureStock.getClose()).expectedMove(null).tradePosition(null)
                .high(futureStock.getHigh()).low(futureStock.getLow())
                .tradedata(tradedata)
                .volumedata(volumedata)
                .statusUpdateDate(null)
                .build();
    }

    private static double calculateBuy(FutureStock futureStock) {
        if (futureStock.getExctMrktDirection().equalsIgnoreCase("Not Down")
                || futureStock.getExctMrktDirection().equalsIgnoreCase("UP"))
            return futureStock.getHigh() + 1;
        return 0;
    }

    private static double calculateSell(FutureStock futureStock) {
        if (futureStock.getExctMrktDirection().equalsIgnoreCase("Not UP")
                || futureStock.getExctMrktDirection().equalsIgnoreCase("DOWN"))
            return futureStock.getLow() - 1;
        return 0;
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
        List<String[]> runDatas = getStockHistory();
        for (int i=0; i<=10; i++){
            String runDate = runDatas.get(i)[0];
            System.out.println("Starting for----"+runDate);
//            if (!runDate.equals("2024-05-17"))
//                continue;
//        for (int i=0; i<1; i++){
//            String runDate = "2024-04-25";
            Map<String, FutureStock> stockDBData = readStockDataFromDB(runDate);
            if (stockDBData.isEmpty())
                System.out.println("Data not found with this date="+runDate);
            for (String key : stockDBData.keySet()) {
//                if (!key.equals("DEEPENR.NS"))
//                    continue;
                List<String[]> historyData = StockUtil.loadStockData(key);
                FutureStock futureStockDBData = stockDBData.get(key);
                if (historyData.get(0)[0].equals(futureStockDBData.getDate())){
                    System.out.println("Next Data not available for this date="+futureStockDBData.getDate());
                    break;
                }else if("SELL".equals(futureStockDBData.getStatus()) || ("BUY").equals(futureStockDBData.getStatus())){
                    System.out.println(key+" stock already in final status="+futureStockDBData.getStatus()+" so skipping");
                    continue;
                }
                List<String[]> emaData = StockUtil.loadEmaData(key);
                String[] nextDayEma = emaData.get(0);
                String[] sameDayEma = emaData.get(1);
                String[] nextDayData = historyData.get(0);
                double low = futureStockDBData.getClose() < futureStockDBData.getOpen() ? futureStockDBData.getClose() : futureStockDBData.getOpen();
                double high = futureStockDBData.getClose() < futureStockDBData.getOpen() ? futureStockDBData.getOpen() : futureStockDBData.getClose();
                if (futureStockDBData.getSelectType().equalsIgnoreCase("UP")
                        || futureStockDBData.getSelectType().equalsIgnoreCase("HIGH")) {
                    if (Double.parseDouble(nextDayData[4]) <= low
                            && (Double.parseDouble(nextDayEma[1]) <= Double.parseDouble(nextDayEma[0]))) {
                        System.out.println(key + "---moving DOWN, its confirmation");
                        if (!"SELL".equals(futureStockDBData.getStatus()))
                            validateRecordAndUpdateStatus(sameDayEma, nextDayEma, futureStockDBData, "SELL");
                    } else if (Double.parseDouble(nextDayData[4]) <= low) { //ema not cross
                        System.out.println(key + "---moving DOWN, you can trade or wait for confirmation");
                        if (!"WAIT".equals(futureStockDBData.getStatus()))
                            validateRecordAndUpdateStatus(sameDayEma, nextDayEma, futureStockDBData, "WAIT");
                    } else if (Double.parseDouble(nextDayData[4]) > high) {
                        //delete from table
                        deleteStockRecord(futureStockDBData);
                    }
                } else if (futureStockDBData.getSelectType().equalsIgnoreCase("DOWN")
                        || futureStockDBData.getSelectType().equalsIgnoreCase("LOW")) {
                    if (Double.parseDouble(nextDayData[4]) > high
                            && (Double.parseDouble(nextDayEma[0]) <= Double.parseDouble(nextDayEma[1]))) {
                        System.out.println(key + "---moving UP, you can trade or wait for confirmation");
                        if (!"BUY".equals(futureStockDBData.getStatus()))
                            validateRecordAndUpdateStatus(sameDayEma, nextDayEma, futureStockDBData, "BUY");
                    } else if (Double.parseDouble(nextDayData[4]) >= high) { //ema not cross
                        System.out.println(key + "---moving UP, you can trade or wait for confirmation");
                        if (!"WAIT".equals(futureStockDBData.getStatus()))
                            validateRecordAndUpdateStatus(sameDayEma, nextDayEma, futureStockDBData, "WAIT");
                    } else if (Double.parseDouble(nextDayData[4]) < low) {
                        //delete from table
                        deleteStockRecord(futureStockDBData);
                    }
                }
            }
            System.out.println("End for----"+runDate);
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
            HttpResponse<String> response = apiCAll(objectMapper.writeValueAsString(primaryKey), "/deleteStockData", "POST");
            System.out.println(response.body());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void validateRecordAndUpdateStatus(String[] sameDayEma, String[] nextDayEma, FutureStock futureStock, String status) {
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
        sb.append(status);
        sb.append("&");
        sb.append("statusUpdateDate=");
        sb.append(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"));
        HttpResponse<String> response = apiCAll("{}", sb.toString(),"POST");
        System.out.println(response.body()+" "+status);
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

    public static Map<String, FutureStock> finalizeStocks(String runDate) {
        String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\high_low_stocks";
//        String fileLocation1 = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\stocks_to_trade\\filter_based_candle\\expectedMove";
        Map<String, FutureStock> finalizeStocks = new HashMap<>();
        Map<String, String[]> stockList = new HashMap<>();
        try {
            List<String> files = Files.list(Paths.get(fileLocation))
                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            files.sort(Comparator.reverseOrder());
            int i=0,j = 0;
//            int i=2, j=15;
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
                FutureStock fs = FutureStock.prepareFutureStockData(stockList.get(key), runDate);
                if (!fs.getExctMrktDirection().equalsIgnoreCase("Not SURE"))
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

    private static List<String[]> getStockHistory() {
        List<String[]> data = null;
        try {
//            String fileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_data";
//            List<String> files = Files.list(Paths.get(fileLocation))
//                    .map(fpath -> fpath.getFileName().toFile().getName()).collect(Collectors.toList());
            data = StockUtil.loadStockData("^NSEI");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    private static void runWaitStockCleanUpJobEveryWeekend() throws JsonProcessingException {
        List<String[]> stockHist = getStockHistory();
        Map<String, String> dates = new HashMap<>();
        for (int i=7;i<14;i++){
            dates.put("date"+i,stockHist.get(i)[0]);
        }
        DatesAndStatusRequest req = DatesAndStatusRequest.builder().dates(dates).status("WAIT").build();
        String requestBody = objectMapper.writeValueAsString(req);
        HttpResponse<String> response = apiCAll(requestBody,"/deleteStocksByDatesAndStatus","POST");
    }

    private static void findStockTotrade() {
        String format = "yyyy-MM-dd";
        String dt = DateUtil.getPreviousWeekDate(format);
        for (int i=0;i<7;i++){
            dt = DateUtil.getNextDayDate(dt, format);
            System.out.println(dt);
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
//        runWaitStockCleanUpJobEveryWeekend();
//        findStockTotrade();
//        getVolumeHistory("3IINFOLTD.NS", 5);
//                validateDBData(); //this will validate yesterday data, change date manually
        prepareStockDataAndStoreToDB(); // this will store today data

    }

}
