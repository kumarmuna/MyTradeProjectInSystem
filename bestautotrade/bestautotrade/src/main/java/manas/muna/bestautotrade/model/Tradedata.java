package manas.muna.bestautotrade.model;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Tradedata {
    double buyabove;
    double selllower;
    double buyproftargt;
    double sellproftargt;
}
