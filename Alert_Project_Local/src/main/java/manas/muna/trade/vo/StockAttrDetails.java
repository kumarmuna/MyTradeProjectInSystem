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
    String name;
    Date date;
    double price;
    double gain;
    double lose;
    String prevDaycandleType;
    @Override
    public String toString(){
        return "Name="+name+", prevDaycandleType="+prevDaycandleType+", date="+date+", price="+price+", gain="+gain+", lose="+lose;
    }
}
