package manas.muna.trade.vo;

import lombok.*;

@Builder
@Getter
//@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandleStick {
    double open;
    double close;
    double high;
    double low;
    String candleType; //SolidGreen, HallowGreen, SolidRed & HallowRed
    String dateOfCandle;
    /*
    Green Candle - if close > prior close
        Green Hallow - if close > open
        Green Fill/Solid - if close < open
    Red Candle - if close < prior close
        Red Hallow - if close > open
        Red Fill/Solid - if close < open */
}
