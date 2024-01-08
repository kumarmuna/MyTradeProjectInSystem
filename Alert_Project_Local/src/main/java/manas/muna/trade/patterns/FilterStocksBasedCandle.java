package manas.muna.trade.patterns;

import manas.muna.trade.constants.CandleTypes;
import manas.muna.trade.util.CandleUtil;

import java.util.List;

public class FilterStocksBasedCandle {

    public static void filterStocks(){
        List<String> candleTypes = CandleTypes.getAllConstantNames();
        for (String candle: candleTypes){
            switch (candle){
//                case CandleTypes.DARKCLOUDCOVER:
//                    CandleUtil.vali
            }
        }
    }
    public static void main(String[] args){

    }
}
