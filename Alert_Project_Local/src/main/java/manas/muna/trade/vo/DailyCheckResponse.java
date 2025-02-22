package manas.muna.trade.vo;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyCheckResponse {
    String stockName;
    String stockFindDate;
    String candleOccur;
    String prevCandleOccur;
    String stockDirection;
    String emaDirection;
    String statusMatchDate;
    Boolean stockNeedToCheck;
    String stockType;
    String daysHighLow;
}
