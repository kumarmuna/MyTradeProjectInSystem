package manas.muna.bestautotrade.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.netty.util.internal.StringUtil;
import manas.muna.bestautotrade.model.*;
import manas.muna.bestautotrade.repository.StockdataCassandraRepository;
import manas.muna.bestautotrade.repository.StockdataDaillyCheckRepository;
import manas.muna.bestautotrade.repository.StockdataHighLowCassandraRepository;
import manas.muna.bestautotrade.repository.StockdataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("stockdataService")
public class StockdataService {

    @Autowired
    StockdataRepository stockdataRepository;

    @Autowired
    StockdataCassandraRepository cassandraRepository;

    @Autowired
    StockdataHighLowCassandraRepository highLowCassandraRepository;

    @Autowired
    StockdataDaillyCheckRepository stockdataDaillyCheckRepository;

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

    public List<StockDetailsTable> getStockdataByNameCass(String stockName) {
        return cassandraRepository.findByName(stockName);
    }

    public List<Stockdata> getStockdataByStockCandleType(String candleType) {
        return stockdataRepository.findByCandleType(candleType);
    }

    public List<Stockdata> getStockdataByDate(String date) {
        List<StockDetailsTable> dt = cassandraRepository.findByDate(date);
        return convertStockData(dt);
    }

    private List<Stockdata> convertStockData(List<StockDetailsTable> dt) {
        List<Stockdata> result = new ArrayList<>();
        for (StockDetailsTable sdt : dt) {
            try {
                    if (sdt.getStatus().equalsIgnoreCase("WAIT") && StringUtil.isNullOrEmpty(sdt.getStatusUpdateDate())
                                && StringUtil.isNullOrEmpty(sdt.getStockDetailsData())) {
                        Stockdata sData = new JsonMapper().readValue(sdt.getStockDetailsData(), Stockdata.class);
                        result.add(sData);
                    }
                }catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<Stockdata> getStockDataBYDatesAndSatus(String[] dates, String status){

//        Stockdata g =  cassandraRepository.findByDateAndStatus(dates,status);
        return stockdataRepository.findByDatesAndStatus(dates, status);
    }

    public List<StockDailyCheckTable> getDailyCheckDataByDatesAndCheck(List<String> dates, Boolean check){
        return stockdataDaillyCheckRepository.findByDatesAndCheck(dates, check);
    }

    public List<Stockdata> getStockDataByDates(String[] dates){
        List<String> l = new ArrayList<>();l.add("2024-12-05");l.add("2024-12-21");
        List<StockDetailsTable> data = cassandraRepository.findByDates(l);
        return convertStockDetailsTableToStockdata(data);
//        return stockdataRepository.findByDates(dates);
    }

    private List<Stockdata> convertStockDetailsTableToStockdata(List<StockDetailsTable> data) {
        List<Stockdata> stockdataList = new ArrayList<>();
        for (StockDetailsTable sd : data) {
            //TODO
        }
        return null;
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
                true, jsonData,"WAIT",null,stockdata.getStockDirection());
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

    //this is to delete from HighLowData Table
    public void deleteByKey(String name, int year) {
        highLowCassandraRepository.deleteByKey(name, year);
    }

    public void storeHighLowData(StockHighLowVo stockHighLowVo) {
        try {
            highLowCassandraRepository.save(convertToHighLowTable(stockHighLowVo));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private StockHighLowTable convertToHighLowTable(StockHighLowVo stockHighLowVo) {
        return StockHighLowTable.builder()
                .stockName(stockHighLowVo.getStockName())
                .yearNumber(stockHighLowVo.getYearNumber())
                .jan_mon(stockHighLowVo.getJan_mon())
                .feb_mon(stockHighLowVo.getFeb_mon())
                .jul_mon(stockHighLowVo.getJul_mon())
                .jun_mon(stockHighLowVo.getJun_mon())
                .mar_mon(stockHighLowVo.getMar_mon())
                .may_mon(stockHighLowVo.getMay_mon())
                .aug_mon(stockHighLowVo.getAug_mon())
                .apr_mon(stockHighLowVo.getApr_mon())
                .dec_mon(stockHighLowVo.getDec_mon())
                .oct_mon(stockHighLowVo.getOct_mon())
                .sep_mon(stockHighLowVo.getSep_mon())
                .yearData(stockHighLowVo.getYearData())
                .nov_mon(stockHighLowVo.getNov_mon())
                .build();
    }

    public List<StockHighLowVo> getHighLowStock(String name, int year) {
        List<StockHighLowTable> list = highLowCassandraRepository.findByNameAndYear(name,year);
        List<StockHighLowVo> result = convertDBDataToVo(list);
        return result;
    }

    private List<StockHighLowVo> convertDBDataToVo(List<StockHighLowTable> list) {
        List<StockHighLowVo> result = new ArrayList<>();
        for (StockHighLowTable data : list){
            result.add(StockHighLowVo.builder()
                    .stockName(data.getStockName())
                    .yearNumber(data.getYearNumber())
                    .jan_mon(data.getJan_mon())
                    .feb_mon(data.getFeb_mon())
                    .mar_mon(data.getMar_mon())
                    .apr_mon(data.getApr_mon())
                    .may_mon(data.getMay_mon())
                    .jun_mon(data.getJun_mon())
                    .jul_mon(data.getJul_mon())
                    .aug_mon(data.getAug_mon())
                    .sep_mon(data.getSep_mon())
                    .oct_mon(data.getOct_mon())
                    .nov_mon(data.getNov_mon())
                    .dec_mon(data.getDec_mon())
                    .yearData(data.getYearData())
                    .build());
        }
        return result;
    }

    public List<StockHighLowVo> getAllStockHighLowData() {
        List<StockHighLowTable> list = highLowCassandraRepository.findAll();
        return convertDBDataToVo(list);
    }

    public List<StockDailyCheckTable> getDailyCheckDataByDates(List<String> dates){
        return stockdataDaillyCheckRepository.findByDates(dates);
    }
}
