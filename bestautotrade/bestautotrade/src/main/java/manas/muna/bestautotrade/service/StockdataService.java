package manas.muna.bestautotrade.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import manas.muna.bestautotrade.model.StockDetailsTable;
import manas.muna.bestautotrade.model.Stockdata;
import manas.muna.bestautotrade.model.StockdataPrimaryKey;
import manas.muna.bestautotrade.repository.StockdataCassandraRepository;
import manas.muna.bestautotrade.repository.StockdataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service("stockdataService")
public class StockdataService {

    @Autowired
    StockdataRepository stockdataRepository;

    @Autowired
    StockdataCassandraRepository cassandraRepository;

    JsonMapper mapper = new JsonMapper();

    public List<Stockdata> getAllData() {
        return stockdataRepository.findAll();
    }
    public List<Stockdata> getStockdataByStockName(String stockName) {
        return stockdataRepository.findByStockName(stockName);
    }
    public List<Stockdata> getStockdataByStockNameCass(String stockName) {
        return cassandraRepository.findByStockName(stockName);
    }
    public List<Stockdata> getStockdataByStockCandleType(String candleType) {
        return stockdataRepository.findByCandleType(candleType);
    }

    public List<Stockdata> getStockdataByDate(String date) {
        return stockdataRepository.findByDate(date);
    }

    public List<Stockdata> getStockDataBYDatesAndSatus(String[] dates, String status){

//        Stockdata g =  cassandraRepository.findByDateAndStatus(dates,status);
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

    public String saveStockDataCassandra(Stockdata stockdata) {
        cassandraRepository.save(convertStockDataToStockDetailsTable(stockdata));
        return stockdata.getStockdataPrimaryKey().getStockName() + " added successfully";
    }

    private StockDetailsTable convertStockDataToStockDetailsTable(Stockdata stockdata) {
        String jsonData = "";
        try {
            jsonData = mapper.writeValueAsString(stockdata);
        }catch (Exception e){
            System.out.println("Some error during convert to json");
        }
        return new StockDetailsTable(stockdata.getStockdataPrimaryKey().getStockName(),
                stockdata.getStockdataPrimaryKey().getDate(),stockdata.getStockdataPrimaryKey().getCandleType(),
                true, jsonData,"WAIT",null);
    }

    public String updateRecord(String stockName, String date, String candleType, String status, String statusUpdateDate, String stockData) {
//        stockdataRepository.updateStatus(stockName, date, candleType, status, statusUpdateDate);
        try{
            cassandraRepository.save(StockDetailsTable.builder().stockName(stockName).stockFindDate(date)
                    .candleOccur(candleType).status(status).statusUpdateDate(statusUpdateDate).stockDetailsData(stockData).build());
        }catch (Exception e){
            e.printStackTrace();
        }
        return stockName +" status got updated";
    }

    public void deleteByPrimaryKey(StockdataPrimaryKey primaryKey) {
        stockdataRepository.deleteById(primaryKey);
        try{
            cassandraRepository.deleteById(primaryKey.getStockName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
