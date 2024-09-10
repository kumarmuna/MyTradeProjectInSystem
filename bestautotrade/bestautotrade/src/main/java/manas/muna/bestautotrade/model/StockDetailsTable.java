package manas.muna.bestautotrade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("trade_stock_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDetailsTable {

    @PrimaryKeyColumn(name = "name", type = PrimaryKeyType.PARTITIONED)
    String stockName;
    @PrimaryKeyColumn(name = "ini_date", type = PrimaryKeyType.CLUSTERED)
    String stockFindDate;
    @Column("candle")
    String candleOccur;
    @Column("check")
    Boolean stockNeedToCheck;
    @Column("details")
    String stockDetailsData;
    @Column("status")
    String status;
    @Column("sts_updt_date")
    String statusUpdateDate;

}
