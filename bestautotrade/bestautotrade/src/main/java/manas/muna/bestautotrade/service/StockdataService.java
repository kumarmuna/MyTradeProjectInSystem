package manas.muna.bestautotrade.service;

import manas.muna.bestautotrade.model.Stockdata;
import manas.muna.bestautotrade.model.StockdataPrimaryKey;
import manas.muna.bestautotrade.repository.StockdataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("stockdataService")
public class StockdataService {

    @Autowired
    StockdataRepository stockdataRepository;

    public List<Stockdata> getAllData() {
        return stockdataRepository.findAll();
    }
    public List<Stockdata> getStockdataByStockName(String stockName) {
        return stockdataRepository.findByStockName(stockName);
    }
    public List<Stockdata> getStockdataByStockCandleType(String candleType) {
        return stockdataRepository.findByCandleType(candleType);
    }

    public List<Stockdata> getStockdataByDate(String date) {
        return stockdataRepository.findByDate(date);
    }

    public List<Stockdata> getStockDataBYDatesAndSatus(String[] dates, String status){
        return stockdataRepository.findByDatesAndStatus(dates, status);
    }

    public List<Stockdata> getStockDataBYDates(String[] dates){
        return stockdataRepository.findByDates(dates);
    }

    public void deleteStockDataBYDatesAndSatus(String[] dates, String status){
        stockdataRepository.deleteByDatesAndStatus(dates, status);
    }

    public Optional<Stockdata> getStockdataById(StockdataPrimaryKey stockdataPrimaryKey) {
        return stockdataRepository.findById(stockdataPrimaryKey);
    }

    public String saveStockData(Stockdata stockdata) {
        stockdataRepository.save(stockdata);
        return stockdata.getStockdataPrimaryKey().getStockName() + " added successfully";
    }

    public String updateRecord(String stockName, String date, String candleType, String status, String statusUpdateDate) {
        stockdataRepository.updateStatus(stockName, date, candleType, status, statusUpdateDate);
        return stockName +" status got updated";
    }

    public void deleteByPrimaryKey(StockdataPrimaryKey primaryKey) {
        stockdataRepository.deleteById(primaryKey);
    }
}
