package manas.muna.trade.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class StockVolumeAttribute {
    String stockName;
    int lowVol;
    int highVol;
    int avgVol;
    int highVolPos;
    int lowVolPos;
    boolean isNearByHighVol;
    boolean isNearByLowVol;

    @Override
    public String toString(){
        return "stockName="+stockName+", lowVol="+lowVol+", highVol="+highVol+", avgVol="+avgVol
                +", highVolPos="+highVolPos+", lowVolPos="+lowVolPos;
    }
}
