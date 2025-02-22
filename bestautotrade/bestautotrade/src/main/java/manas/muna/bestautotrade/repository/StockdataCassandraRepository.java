package manas.muna.bestautotrade.repository;

import manas.muna.bestautotrade.model.StockDetailsTable;
import manas.muna.bestautotrade.model.Stockdata;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockdataCassandraRepository extends CassandraRepository<StockDetailsTable, String> {

    List<Stockdata> findByStockName(String stockName);

    @Query("SELECT * from trade.trade_stock_details WHERE name=:name ALLOW FILTERING ;")
    List<StockDetailsTable> findByName(@Param("name") String name);

    @Query("SELECT * from trade.trade_stock_details WHERE name ='?0' AND ini_date ='?1';")
    Stockdata findByNameAndDate(String stockName, String date);

    @Query("SELECT * FROM trade.trade_stock_details WHERE ini_date in :dates and status =?1 ALLOW FILTERING ;")
    Stockdata findByDateAndStatus(@Param("dates") List<String> dates, String status);

    @Query("SELECT * FROM trade.trade_stock_details WHERE ini_date=:iniDate ALLOW FILTERING ;")
    List<StockDetailsTable> findByDate(String iniDate);

    @Query("SELECT * FROM trade.trade_stock_details WHERE ini_date IN :initDate ALLOW FILTERING ;")
    List<StockDetailsTable> findByDates(@Param("initDate") List<String> iniDate);
}
