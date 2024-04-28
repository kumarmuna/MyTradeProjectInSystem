package manas.muna.trade.api;

import manas.muna.trade.api.model.Stockdata;
import manas.muna.trade.api.service.StockdataStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodaysStockDataController {

    @Autowired
    StockdataStoreService service;

    @PostMapping("/storeStockData")
    public String storeStockData(@RequestBody Stockdata stockdata) {
        return service.storeStockData(stockdata);
    }
}
