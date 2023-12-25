package manas.muna.trade.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import manas.muna.trade.util.StockUtil;

@Builder
@Getter
@Setter
@ToString
public class EmaChangeDetails {
    String stockName;
    double ema3Change;
    double ema8Change;
    double ema8Avg;
    double ema3Avg;
    double emaMovingAvg;
    boolean eligible;

    @Override
    public String toString() {
        return "Name: "+stockName+", ema3:"+ ema3Change
                +" ema3Avg:"+ema3Avg
                +", ema8:"+ema8Change
                +", ema8Avg:"+ema8Avg
                +", emaAvg:"+emaMovingAvg
                +System.lineSeparator();
    }
}
