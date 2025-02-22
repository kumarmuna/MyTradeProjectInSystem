package manas.muna.bestautotrade.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manas.muna.bestautotrade.model.StockDailyCheckTable;
import manas.muna.bestautotrade.model.StockPayload;
import manas.muna.bestautotrade.repository.StockdataDaillyCheckRepository;
import manas.muna.bestautotrade.service.StockdataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dailyCheck")
public class StockDailyCheckController {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    StockdataDaillyCheckRepository repository;
    @Autowired
    StockdataService service;

    @GetMapping("/getStocksByDates")
    public List<StockDailyCheckTable> getAllDataByDate(@RequestParam(name = "dates", required = false) List<String> dates) {
        List<StockDailyCheckTable> result = service.getDailyCheckDataByDates(dates);
        return result;
    }

    @GetMapping("/getStockNamesByDates")
    public List<String> getAllStockNamesByDate(@RequestParam(name = "dates", required = false) List<String> dates) {
        List<StockDailyCheckTable> result = service.getDailyCheckDataByDates(dates);
        return result.stream().map(a->a.getStockName()).collect(Collectors.toList());
    }

    @GetMapping("/getStocksByDatesAndCheck")
    public List<StockDailyCheckTable> getAllDataByDateAndCheck(@RequestParam(name = "dates") List<String> dates) {
        List<StockDailyCheckTable> result = service.getDailyCheckDataByDatesAndCheck(dates, true);
        return result;
    }

    @PostMapping("/addStock")
    public String addDailyStock(@RequestBody StockPayload payload) {
        Map<String, String> requestDetailsMap = new HashMap<>();
        requestDetailsMap.put("name", payload.getName());
        requestDetailsMap.putAll(mapper.convertValue(payload.getRequestDetails(), new TypeReference<Map<String, String>>() {}));
        try {
            StockDailyCheckTable writable = StockDailyCheckTable.builder()
                    .stockName(requestDetailsMap.get("name"))
                    .stockDirection(requestDetailsMap.get("stock_direction"))
                    .stockNeedToCheck(true)
                    .emaDirection(requestDetailsMap.get("ema_direction"))
                    .stockFindDate(requestDetailsMap.get("ini_date"))
                    .candleOccur(requestDetailsMap.get("candle"))
                    .statusMatchDate(requestDetailsMap.get("status_match_date"))
                    .stockType(requestDetailsMap.get("stock_type"))
                    .prevCandleOccur(requestDetailsMap.get("prev_candle"))
                    .daysHighLow(requestDetailsMap.get("days_high_low"))
                    .build();
            repository.save(writable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "stored..."+ payload.getName();
    }


}
