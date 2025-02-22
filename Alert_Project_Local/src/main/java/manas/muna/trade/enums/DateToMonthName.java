package manas.muna.trade.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DateToMonthName {

    JAN("01"),
    FEB("02"),
    MAR("03"),
    APR("04"),
    MAY("05"),
    JUN("06"),
    JUL("07"),
    AUG("08"),
    SEP("09"),
    OCT("10"),
    NOV("11"),
    DEC("12")
    ;

    String monthName;
    static Map<String, DateToMonthName> monthNameMap = new HashMap<>();
    DateToMonthName(String mon){
        monthName = mon;
    }

    public String getName(){
        return monthName;
    }

    static {
        monthNameMap = Arrays.stream(DateToMonthName.values())
                .collect(Collectors.toMap(DateToMonthName::getName, Function.identity()));
    }

    public static DateToMonthName getDateToMonthName(String key) {
        return monthNameMap.get(key);
    }
}
