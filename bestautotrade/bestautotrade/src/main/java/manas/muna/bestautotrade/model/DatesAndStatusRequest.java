package manas.muna.bestautotrade.model;

import lombok.*;

import java.util.Map;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DatesAndStatusRequest {
    Map<String, String> dates;
    String status;
}
