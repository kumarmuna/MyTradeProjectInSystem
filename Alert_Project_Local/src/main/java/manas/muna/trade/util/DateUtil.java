package manas.muna.trade.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static String dd_MM_yyyy = "dd_MM_yyyy";
    static Calendar cal = null;

    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat(dd_MM_yyyy);
        Date date = new Date();
        return dateFormat.format(date);
//        return "26_12_2023";
    }

    public static String getYesterdayDate() {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(dd_MM_yyyy);
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();
//        return dateFormat.format(date);
        return "23_12_2023";
    }

    public static String getTomorrowDate() {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(dd_MM_yyyy);
        cal.add(Calendar.DATE, +1);
        Date date = cal.getTime();
        return dateFormat.format(date);
    }
}
