package manas.muna.bestautotrade.model;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Volumedata {
    int firstDayVol;
    int secondDayVol;
    int thirdDayVol;
    int fourthDayVol;
    int fifthDayVol;
}
