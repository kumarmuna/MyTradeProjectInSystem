package manas.muna.trade.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;

import java.util.Date;

//@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
public class StockAttrDetails {
    Date date;
    double price;
}
