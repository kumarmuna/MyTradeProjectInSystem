package manas.muna.trade.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
public class StockDetails {
    String stockName;
    String isGreenRed;
    int volume;
    int highVolumeCompareDays;
    double percentageMoved;
    String target;
    String tradeCondition;
    String ema100_5_cross;
    String macd;
    String isResultDateNear = "No";
    String candleTypesOccur;
    String entryExit;

    @Override
    public String toString() {
//        return "StockName= "+stockName+", Vol= "+volume+", GR= "+isGreenRed+", days= "+highVolumeCompareDays+" " +
//                ", resultDateNear= "+isResultDateNear+
//                ", Prcnt_Moved= "+percentageMoved+", target="+target+", Ema100_5="+ema100_5_cross+", tradeCondition="+tradeCondition;
        return "StockName= "+stockName+", Vol= "+volume+", GR= "+isGreenRed+
                ", candleTypesOccur= "+candleTypesOccur+", entryExit= "+entryExit;
    }
}
