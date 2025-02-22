package manas.muna.trade.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDetailsTable {

    String stockName;
    String stockFindDate;
    String candleOccur;
    Boolean stockNeedToCheck;
    String stockDetailsData;
    String status;
    String statusUpdateDate;
    String stockDirection;
}
