package manas.muna.bestautotrade.controller;

import manas.muna.bestautotrade.model.DatesAndStatusRequest;
import manas.muna.bestautotrade.model.DatesRequest;
import manas.muna.bestautotrade.model.Stockdata;
import manas.muna.bestautotrade.model.StockdataPrimaryKey;
import manas.muna.bestautotrade.service.StockdataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stockdata")
public class StockdataController {

    @Autowired
    StockdataService service;

    @GetMapping("/name/{name}")
    public List<Stockdata> getStockdataByName(@PathVariable String name) {
        List<Stockdata> data = service.getStockdataByStockName(name);
        return data;
    }

    @GetMapping("/namesByDate/{date}")
    public List<String> getStockNamesByDate(@PathVariable String date) {
        List<Stockdata> stockdata = service.getStockdataByDate(date);
        return stockdata.stream().map(a-> a.getStockdataPrimaryKey().getStockName()+":"+a.getStatus()+":"+a.getStockdataPrimaryKey().getCandleType()).collect(Collectors.toList());
    }

    @GetMapping("/candletype/{candletype}")
    public List<Stockdata> getStockdataByCandleType(@PathVariable String candletype) {
        List<Stockdata> data = service.getStockdataByStockCandleType(candletype);
        return data;
    }

    @GetMapping("/bydate/{date}")
    public List<Stockdata> getStockdataByDate(@PathVariable String date) {
        List<Stockdata> data = service.getStockdataByDate(date);
        return data;
    }

    @PostMapping("/allstockdata")
    public Optional<Stockdata> getStockdataById(@RequestBody StockdataPrimaryKey key){
        Optional<Stockdata> data = service.getStockdataById(key);
        return data;
    }

    @GetMapping("/getallstockdata")
    public List<Stockdata> getAllStockdata(){
        List<Stockdata> data = service.getAllData();
        return data;
    }

    @PostMapping("/addstockdata")
    public String addStockData(@RequestBody Stockdata stockdata) {
        String res = "";
        try {
            res = service.saveStockData(stockdata);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam String stockName, @RequestParam String date, @RequestParam String candleType, @RequestParam String status,
                               @RequestParam String statusUpdateDate) {
        String res = "";
        try{
            res = service.updateRecord(stockName, date, candleType, status, statusUpdateDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    @PostMapping("/deleteStockData")
    public String deleteStockDataByKey(@RequestBody StockdataPrimaryKey primaryKey) {
        try{
            service.deleteByPrimaryKey(primaryKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "deleted stock="+primaryKey.getStockName()+" candle="+primaryKey.getCandleType();
    }

    @PostMapping("/getStocksByDatesAndStatus")
    public List<Stockdata> getStockByDatesAndStatus(@RequestBody DatesAndStatusRequest datesAndStatusRequest) {
        List<Stockdata> resp = null;
        try {
            resp = service.getStockDataBYDatesAndSatus(datesAndStatusRequest.getDates().values().toArray(new String[0]),
                    datesAndStatusRequest.getStatus());
        }catch (Exception e){
            e.printStackTrace();
        }
        return resp;
    }

    @PostMapping("/getStocksByDates")
    public List<String> getStockByDates(@RequestBody DatesRequest datesRequest) {
        try {
            List<Stockdata> stockdata = service.getStockDataBYDates(datesRequest.getDates().values().toArray(new String[0]));
            return stockdata.stream().map(a-> a.getStockdataPrimaryKey().getStockName()).collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/deleteStocksByDatesAndStatus")
    public String deleteStockByDatesAndStatus(@RequestBody DatesAndStatusRequest datesAndStatusRequest) {
        String resp = "deleted.....";
        try {
            service.deleteStockDataBYDatesAndSatus(datesAndStatusRequest.getDates().values().toArray(new String[0]),
                    datesAndStatusRequest.getStatus());
        }catch (Exception e){
            e.printStackTrace();
        }
        return resp;
    }
}
