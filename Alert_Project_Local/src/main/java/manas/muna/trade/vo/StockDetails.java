package manas.muna.trade.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
public class StockDetails {
    String stockName;
    String isGreenRed;
    int volume;
    int highVolumeCompareDays;
    double percentageMoved;

    @Override
    public String toString() {
        return "StockName= "+stockName+", Volume= "+volume+", GR= "+isGreenRed+", days= "+highVolumeCompareDays+" " +
                ", Prcnt_Moved= "+percentageMoved;
    }
}
