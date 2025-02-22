package manas.muna.bestautotrade.repository;

import manas.muna.bestautotrade.model.StockDetailsTable;
import manas.muna.bestautotrade.model.StockHighLowTable;
import manas.muna.bestautotrade.model.Stockdata;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockdataHighLowCassandraRepository extends CassandraRepository<StockHighLowTable, String> {
    @Query("SELECT * from trade.stock_high_low WHERE name = :stockName AND year_number = :year;")
    List<StockHighLowTable> findByNameAndYear(String stockName, int year);

    @Query("delete from trade.stock_high_low WHERE name =?0 AND year_number =?1;")
    void deleteByKey(String name, int year);
}
