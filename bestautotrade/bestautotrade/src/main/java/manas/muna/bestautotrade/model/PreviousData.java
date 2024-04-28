package manas.muna.bestautotrade.model;

import lombok.*;

import java.util.Date;

@Data
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
