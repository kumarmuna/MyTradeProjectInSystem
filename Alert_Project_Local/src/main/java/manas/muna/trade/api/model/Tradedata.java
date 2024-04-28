package manas.muna.trade.api.model;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tradedata {
    double buyabove;
    double selllower;
    double buyproftargt;
    double sellproftargt;
}
