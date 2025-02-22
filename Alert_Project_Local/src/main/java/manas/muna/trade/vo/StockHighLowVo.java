package manas.muna.trade.vo;

import lombok.*;

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
