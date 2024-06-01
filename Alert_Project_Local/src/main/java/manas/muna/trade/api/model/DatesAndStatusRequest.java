package manas.muna.trade.api.model;

import lombok.*;

import java.util.Map;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatesAndStatusRequest {
    Map<String, String> dates;
    String status;
}
