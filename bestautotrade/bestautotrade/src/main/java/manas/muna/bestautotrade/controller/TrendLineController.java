package manas.muna.bestautotrade.controller;

import manas.muna.bestautotrade.model.TrendLineRequest;
import manas.muna.bestautotrade.model.TrendLineTable;
import manas.muna.bestautotrade.repository.TrendLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trendline")
public class TrendLineController {

    @Autowired
    TrendLineRepository repository;

    @GetMapping("/getTrendData")
    public TrendLineTable getTrendData(@RequestParam(name = "stockName") String stockName) {
        return repository.findByStockName(stockName);
    }

    @PostMapping("/addTrendData")
    public void addTrendData(@RequestBody TrendLineRequest request) {
        TrendLineTable trendLineTable = TrendLineTable.builder()
                .stockName(request.getStockName())
                .stockTrendCheckPos(request.getCheckPos())
                .data(Arrays.stream(request.getData()).collect(Collectors.joining(","))).build();
        repository.save(trendLineTable);
    }
}
