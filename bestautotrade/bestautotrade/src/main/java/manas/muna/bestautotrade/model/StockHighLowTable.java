package manas.muna.bestautotrade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("stock_high_low")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHighLowTable {
    @PrimaryKeyColumn(name = "name", type = PrimaryKeyType.PARTITIONED)
    String stockName;
    @PrimaryKeyColumn(name = "year_number", type = PrimaryKeyType.CLUSTERED)
    int yearNumber;
    @Column("year_data")
    String yearData;
    @Column("jan_mon")
    String jan_mon;
    @Column("feb_mon")
    String feb_mon;
    @Column("mar_mon")
    String mar_mon;
    @Column("apr_mon")
    String apr_mon;
    @Column("may_mon")
    String may_mon;
    @Column("jun_mon")
    String jun_mon;
    @Column("jul_mon")
    String jul_mon;
    @Column("aug_mon")
    String aug_mon;
    @Column("sep_mon")
    String sep_mon;
    @Column("oct_mon")
    String oct_mon;
    @Column("nov_mon")
    String nov_mon;
    @Column("dec_mon")
    String dec_mon;
}
