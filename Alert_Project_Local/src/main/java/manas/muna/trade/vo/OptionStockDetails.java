package manas.muna.trade.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OptionStockDetails {
    String stockName;
    int compareDays;
    int volume;
    String isCEPE;
    String isGreenRed;

    @Override
    public String toString() {
        return "StockName=  "+stockName+",  Volume= "+volume+",  CompareDays= "+compareDays+",   CE/PE= "+isCEPE+", IsGreenRed= "+isGreenRed;
    }
}
