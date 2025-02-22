package manas.muna.bestautotrade.repository;

import manas.muna.bestautotrade.model.TrendLineTable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrendLineRepository extends CassandraRepository<TrendLineTable, String> {

    TrendLineTable findByStockName(String stockName);

    @Query("SELECT * from trade.trend_line_data WHERE name = :stockName AND checkpos = :stockTrendCheckPos ALLOW FILTERING ;")
    TrendLineTable findByNameAndCheckpos(String stockName, String stockTrendCheckPos);


}
