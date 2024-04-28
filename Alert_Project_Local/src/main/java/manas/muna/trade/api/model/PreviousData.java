package manas.muna.trade.api.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PreviousData {
    Date date;
    double highPoint;
    double openCloseHigh;
    double lowPoint;
    double openCloseLow;
    String upLowIndicator;
}
