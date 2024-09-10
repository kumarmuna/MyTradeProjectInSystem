package manas.muna.trade.vo;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rules {
    String name;
    String mrkDirection;
    String tradeCondition;
    String rsiCondition;
    String checkPoint;
}
