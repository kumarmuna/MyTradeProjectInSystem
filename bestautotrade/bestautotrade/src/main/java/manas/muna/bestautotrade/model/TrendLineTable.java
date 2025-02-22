package manas.muna.bestautotrade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("trend_line_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendLineTable {

    @PrimaryKeyColumn(name = "name", type = PrimaryKeyType.PARTITIONED)
    String stockName;
    @Column("checkpos")
    String stockTrendCheckPos;
    @Column("data")
    String data;
}
