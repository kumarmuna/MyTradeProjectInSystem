package manas.muna.bestautotrade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class SupportAndResistance {
    double high;
    double low;
    double open;
    double close;
    String date;
    long volume;
}
