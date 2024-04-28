package manas.muna.bestautotrade.controller;

import manas.muna.bestautotrade.model.Stockdata;
import manas.muna.bestautotrade.model.StockdataPrimaryKey;
import manas.muna.bestautotrade.service.StockdataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

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
    public String updateStatus(@RequestParam String stockName, @RequestParam String date, @RequestParam String candleType, @RequestParam String status) {
        String res = "";
        try{
            res = service.updateRecord(stockName, date, candleType, status);
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
}
