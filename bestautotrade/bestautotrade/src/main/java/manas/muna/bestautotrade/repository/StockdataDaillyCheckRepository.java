package manas.muna.bestautotrade.repository;

import manas.muna.bestautotrade.model.StockDailyCheckTable;
import manas.muna.bestautotrade.model.StockDetailsTable;
import manas.muna.bestautotrade.model.Stockdata;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockdataDaillyCheckRepository extends CassandraRepository<StockDailyCheckTable, String> {

    List<StockDailyCheckTable> findByStockName(String stockName);

    @Query("SELECT * from trade.trade_stock_to_check_daily WHERE name = :stockName AND ini_date = :date;")
    StockDailyCheckTable findByNameAndDate(String stockName, String date);

    @Query("SELECT * FROM trade.trade_stock_to_check_daily WHERE ini_date in :iniDates and check = :check ALLOW FILTERING ;")
    List<StockDailyCheckTable> findByDatesAndCheck(List<String> iniDates, Boolean check);

    @Query("SELECT * FROM trade.trade_stock_to_check_daily WHERE ini_date in :iniDates ALLOW FILTERING ;")
    List<StockDailyCheckTable> findByDates(List<String> iniDates);
}
