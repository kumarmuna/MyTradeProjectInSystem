package manas.muna.bestautotrade.model;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StockHighLowVo {
    String stockName;
    int yearNumber;
    String yearData;
    String jan_mon;
    String feb_mon;
    String mar_mon;
    String apr_mon;
    String may_mon;
    String jun_mon;
    String jul_mon;
    String aug_mon;
    String sep_mon;
    String oct_mon;
    String nov_mon;
    String dec_mon;
}
