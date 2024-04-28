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
    String mrkDirection;
    int volumePos;
    String selectedCategory;
    double expctHigh;
    double expctLow;
    double highMovePerDay;
    double lowMovePerDay;

    @Override
    public String toString(){
        return "StockName="+stockName+", HighDiff="+highDiff+", LowDiff="+lowDiff+", candleType="+candleType+", " +
                "mrkDirection="+mrkDirection+", isRedGreen="+isGreenRed+", slctCategry="+selectedCategory+", volumePos="+volumePos
                +", expctHigh="+expctHigh+", expctLow="+expctLow+", highMovePerDay="+highMovePerDay+", lowMovePerDay="+lowMovePerDay;
    }
}
