package manas.muna.trade.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ExpectedCandle {
    String stockName;
    double highDiff;
    double lowDiff;
    String candleType;
    String stockMovement;
    String isGreenRed;
    int priority;

    @Override
    public String toString(){
        return "StockName="+stockName+", HighDiff="+highDiff+", LowDiff="+lowDiff+", candleType="+candleType+", " +
                "movement="+stockMovement+", isRedGreen="+isGreenRed+", priority="+priority;
    }
}
