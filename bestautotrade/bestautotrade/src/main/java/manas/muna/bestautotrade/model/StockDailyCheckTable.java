package manas.muna.bestautotrade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("trade_stock_to_check_daily")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDailyCheckTable {

    @PrimaryKeyColumn(name = "name", type = PrimaryKeyType.PARTITIONED)
    String stockName;
    @Column("ini_date")
    String stockFindDate;
    @Column("candle")
    String candleOccur;
    @Column("prev_candle")
    String prevCandleOccur;
    @Column("stock_direction")
    String stockDirection;
    @Column("ema_direction")
    String emaDirection;
    @Column("status_match_date")
    String statusMatchDate;
    @Column("check")
    Boolean stockNeedToCheck;
    @Column("stock_type")
    String stockType;
    @Column("days_high_low")
    String daysHighLow;
}
