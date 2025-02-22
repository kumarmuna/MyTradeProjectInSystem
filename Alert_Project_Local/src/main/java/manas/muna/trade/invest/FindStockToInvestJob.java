package manas.muna.trade.invest;

import com.fasterxml.jackson.databind.ObjectMapper;
import manas.muna.trade.api.model.Stockdata;
import manas.muna.trade.api.model.StockdataPrimaryKey;
import manas.muna.trade.repository.StockDataFeigenClient;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.RsiData;

import java.net.http.HttpResponse;
import java.util.*;

public class FindStockToInvestJob {
    static ObjectMapper mapper = new ObjectMapper();

    public static Stockdata[] readDBDataByName(String name) {
        HttpResponse<String> response = StockDataFeigenClient.getStockdataByName(name);
        Stockdata[] data = null;
        try {
            data = mapper.readValue(response.body(), Stockdata[].class);
            data = Arrays.stream(data).sorted(new Comparator<Stockdata>() {
                @Override
                public int compare(Stockdata o1, Stockdata o2) {
                    return o2.getStockdataPrimaryKey().getDate().compareTo(o1.getStockdataPrimaryKey().getDate());
                }
            }).toArray(Stockdata[]::new);
        }catch (Exception e){
            System.out.println("error during read data by name"+e);
        }
        return data;
    }

    public static List<String> readDBDataByDate(String date) {
        Map<String, String> reqDaTa = Map.of("date1", date);
        List<String> response = StockDataFeigenClient.getStockNamesBydate(reqDaTa);
        return response;
    }

    public static List<String> readNameAndPositionByDate(String date) {
        List<String> response = StockDataFeigenClient.getStockNameAndPositionBydate(date);
        return response;
    }


    public static List<List<RsiData>> findPointOfRSI(String stockName, int days) {
        List<List<RsiData>> rsiDetails = new ArrayList<>();
        String reportLoc = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\report_data\\2024";
        //report data
        List<RsiData> rsiDataList = new ArrayList<>();
//        boolean willReset = false;
        List<String[]> reportData = StockUtil.readFileData(reportLoc+"\\"+stockName);
        reportData = reportData.subList(1,reportData.size());
        Collections.reverse(reportData);
        reportData = reportData.subList(days,reportData.size());
        for (String[] data : reportData){
            if (rsiDataList.size()!=0 && rsiDataList.get(rsiDataList.size()-1).getRsi() < Double.parseDouble(data[9])) {
                if (rsiDataList.size() > 3) {
                    Collections.sort(rsiDataList, Comparator.comparingDouble(RsiData::getRsi));
                    rsiDetails.add(rsiDataList);
                }
                rsiDataList = new ArrayList<>();
            }
            rsiDataList.add(RsiData.builder().name(stockName).date(DateUtil.convertStrToDate(data[1],"yyyy-MM-dd"))
                    .open(Double.parseDouble(data[2])).close(Double.parseDouble(data[3])).volume(Integer.parseInt(data[4]))
                    .profit(Double.parseDouble(data[5])).avgProfit(Double.parseDouble(data[6]))
                    .loss(Double.parseDouble(data[7])).avgLoss(Double.parseDouble(data[8])).rsi(Double.parseDouble(data[9])).build());
        }
        return rsiDetails;
    }

    public static List<String[]> applyRuleAndFindStock(String date) {
        List<String[]> res = new ArrayList<>();
//        List<String> stockNames = readNameAndPositionByDate(date);
        List<String> stockNames = List.of("DEEPAKNTR.NS:down");
        List<Map<String, Object>> check = new ArrayList<>();
        int days = 2;
        for (String stockName: stockNames) {
            String[] nameAndPos = stockName.split(":");
            String name = nameAndPos[0];
            String position = nameAndPos[1];
            List<String[]> reportData = StockUtil.loadReportData(name);
            reportData = reportData.subList(days, reportData.size()-1);
            String[] todayReport = reportData.get(0);
            List<List<RsiData>> rsiChart = findPointOfRSI(name, days);
            Map<String, Object> expctUpRsi = calculateExpctRSIMove(rsiChart, "up", todayReport[1]);
            Map<String, Object> expctDownRsi = calculateExpctRSIMove(rsiChart, "down", todayReport[1]);
//            Map<String, Object> expctRsi = calculateExpctRSIMove(rsiChart, position, "2024-06-06");//todayReport[1]);
            check.add(expctUpRsi);
            check.add(expctDownRsi);
            System.out.println(".....");
        }

        System.out.println(check);
        return res;
    }

    private static Map<String, Object> calculateExpctRSIMove(List<List<RsiData>> rsiChart, String position, String todayDate) {
        Map<String,Object> rsi = new HashMap<>();
        String stockName = "";
        List<RsiData> checkPoint = rsiChart.get(0);
        List<RsiData> firstPoint = rsiChart.get(1);
        List<RsiData> secondPoint = rsiChart.get(2);
        Date tDate = StockUtil.getDateFromString(todayDate, "yyyy-MM-dd");
        if (!checkPoint.get(0).getDate().equals(tDate) || !checkPoint.get(checkPoint.size()-1).getDate().equals(tDate)){
            firstPoint = checkPoint;
            secondPoint = firstPoint;
        }
        double fPrice = 0.0;
        double lPrice = 0.0;
        Date fDate = null;
        Date lDate = null;
        if (position.equalsIgnoreCase("low") || position.equalsIgnoreCase("down")){
            RsiData rsiData = firstPoint.get(0);
            stockName = rsiData.getName();
            lPrice = rsiData.getRsi();
            lDate = rsiData.getDate();
            rsiData = secondPoint.get(0);
            fPrice = rsiData.getRsi();
            fDate = rsiData.getDate();
        } else if (position.equalsIgnoreCase("up") || position.equalsIgnoreCase("high")) {
            RsiData rsiData = firstPoint.get(firstPoint.size()-1);
            stockName = rsiData.getName();
            lPrice = rsiData.getRsi();
            lDate = rsiData.getDate();
            rsiData = secondPoint.get(secondPoint.size()-1);
            fPrice = rsiData.getRsi();
            fDate = rsiData.getDate();
        }
        long dayDiff = StockUtil.getDaysBetweenDaysInStock(stockName, fDate, lDate);
        double movPerDay = (lPrice-fPrice)/dayDiff;
        long targtDiff = StockUtil.getDaysBetweenDaysInStock(stockName, lDate, StockUtil.getDateFromString(todayDate, "yyyy-MM-dd"));
        rsi.put("targetTodayRSI", lPrice + (movPerDay*targtDiff));
        rsi.put("targetPreviousRSI", lPrice + (movPerDay*(targtDiff-1)));
        return rsi;
    }

    public static void main(String[] args){
//        readDBDataByName("ACCELYA.NS");
//        findPointOfRSI("DIXON.NS");
        applyRuleAndFindStock("2024-06-07");


//        long dt = StockUtil.getDaysBetweenDaysInStock("DCI.NS",StockUtil.getDateFromString("2024-04-10", "yyyy-MM-dd"),
//                StockUtil.getDateFromString("2024-05-31", "yyyy-MM-dd"));
//        System.out.println(dt);
    }
}
