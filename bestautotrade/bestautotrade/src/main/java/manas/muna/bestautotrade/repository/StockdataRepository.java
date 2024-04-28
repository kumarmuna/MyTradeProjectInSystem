package manas.muna.bestautotrade.repository;

import manas.muna.bestautotrade.model.Stockdata;
import manas.muna.bestautotrade.model.StockdataPrimaryKey;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface StockdataRepository extends MongoRepository<Stockdata, StockdataPrimaryKey> {

    @Query("{ '_id.stockName' : ?0 }")
    List<Stockdata> findByStockName(String stockName);

    @Query("{ '_id.candleType' : ?0 }")
    List<Stockdata> findByCandleType(String candleType);

    @Query("{ '_id.date' : ?0 }")
    List<Stockdata> findByDate(String date);

    @Query("{ '_id.stockName' : ?0, '_id.date': ?1, '_id.candleType': ?2}")
    @Update("{'$set': {'status': ?3}}")
    int updateStatus(String stockName, String date, String candleType, String status);
}
