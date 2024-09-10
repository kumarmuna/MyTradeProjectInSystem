package manas.muna.trade.api.repository;

import manas.muna.trade.api.model.Stockdata;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "stock-service", url="http://localhost:8080", path = "/stockdata")
public interface StockClient {

    @PostMapping("/addstockdata")
    public String addStockData(@RequestBody Stockdata stockdata);
}
