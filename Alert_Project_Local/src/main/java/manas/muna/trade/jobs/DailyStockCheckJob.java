package manas.muna.trade.jobs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import manas.muna.trade.storeapi.PrepareStockdataStoreToDBJob;
import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.DailyCheckResponse;
import manas.muna.trade.vo.StockDailyCheck;
import manas.muna.trade.vo.StockHighLowVo;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DailyStockCheckJob {

    public static void getLastOneWkStocks() {
        StringBuilder dates = new StringBuilder();
        String today = DateUtil.getTodayDate("yyyy-MM-dd");
        for (int i=0;i<7;i++){
            dates.append(today);
            today = DateUtil.getPrevDate(today, "yyyy-MM-dd");
            dates.append(",");
        }
        try {
            HttpResponse<String> response = PrepareStockdataStoreToDBJob.dailyStockApiCAll("", "/dailyCheck/getStocksByDates?dates=" + dates + "", "GET");
            if (response != null && response.statusCode() == 200) {
                DailyCheckResponse[] result = new ObjectMapper().readValue(response.body(), DailyCheckResponse[].class);

                for (DailyCheckResponse res : result){
                    String emaMovement = StockUtil.emaMovement(res.getStockName());
                    if (emaMovement!=null && emaMovement.equalsIgnoreCase(res.getStockDirection())){
                        PrepareStockdataStoreToDBJob.storeDailyCheckStocks(res.getStockName(),res.getCandleOccur(),res.getPrevCandleOccur(), res.getStockFindDate(), res.getStockDirection()
                                ,emaMovement, res.getStockType(),DateUtil.getTodayDate("yyyy-MM-dd"), "false", Integer.parseInt(res.getDaysHighLow()==null?"0":res.getDaysHighLow()));
                    }
                }

                System.out.println("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Map<String, Object> getYearData(String name, int year) {
        Map<String, Object> result = new HashMap();
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            HttpResponse<String> response = PrepareStockdataStoreToDBJob.yearDataApiCAll("", "/stockhighlow/getData/"+name+"/"+year, "GET");
            if (response != null && response.statusCode() == 200) {
//                String re = new ObjectMapper().writeValueAsString(response.body());
                StockHighLowVo[] res = new ObjectMapper().readValue(response.body(), StockHighLowVo[].class);
//                result.put("Jan", new ObjectMapper().readValue(res[0].getJan_mon(), Map.class));
                result = objectMapper.convertValue(res[0], new TypeReference<Map<String, Object>>() {});
//                Map dd = map.entrySet().stream().filter(e->!e.getKey().equals("yearData")).filter(e->e.getValue()!=null).filter(e->!e.getKey().equals("yearNumber")).filter(e->!e.getKey().equals("stockName")).collect(Collectors.toMap(e->e.getKey(),e-> {
//                    try {
//                        return objectMapper.readValue(e.getValue(), Map.class);
//                    } catch (Exception ex) {
//                        throw new RuntimeException(ex);
//                    }
//                }));
                System.out.println("");
            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return result;
    }
    public static void main(String[] args) {
//        getLastOneWkStocks();
//        getYearData("DHAMPURSUG.NS", 2024);
        System.out.println("hi");
    }
}
