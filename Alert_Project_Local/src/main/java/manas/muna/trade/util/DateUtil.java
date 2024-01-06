package manas.muna.trade.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static String dd_MM_yyyy = "dd_MM_yyyy";
    public static String yyyy_MM_dd = "yyyy_MM_dd";
    static Calendar cal = null;

    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        Date date = new Date();
        return dateFormat.format(date);
//        return "26_12_2023";
    }

    public static String getYesterdayDate() {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();
        return dateFormat.format(date);
//        return "23_12_2023";
    }

    public static String getTomorrowDate() {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        cal.add(Calendar.DATE, +1);
        Date date = cal.getTime();
        return dateFormat.format(date);
    }
}
