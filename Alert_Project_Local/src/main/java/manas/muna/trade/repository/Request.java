package manas.muna.trade.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonIgnore(IN)
public class Request {
    Map<String, String> dates;
}
