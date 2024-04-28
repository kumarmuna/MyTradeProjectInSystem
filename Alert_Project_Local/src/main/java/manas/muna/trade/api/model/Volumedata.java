package manas.muna.trade.api.model;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volumedata {
    int firstDayVol;
    int secondDayVol;
    int thirdDayVol;
    int fourthDayVol;
    int fifthDayVol;
}
