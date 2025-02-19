package manas.muna.trade.util;

import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
    public static String dd_MM_yyyy = "dd_MM_yyyy";
    public static String yyyy_MM_dd = "yyyy_MM_dd";
    public static String DD_MO_YYYY = "DD-MMM-YYYY";

    static Calendar cal = null;

    public static String getTodayDate() {
        return getTodayDate(yyyy_MM_dd);
    }

    public static String getTodayDate(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
//        return "2024_04_26";
    }

    public static String getYesterdayDate() {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();
        return dateFormat.format(date);
//        return "2024_02_19";
    }

    public static String getPrevDate(String date, String format) {
        String prevDate = "";
        try {
            cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat(format);
            cal.setTime(dateFormat.parse(date));
            cal.add(Calendar.DATE, -1);
            Date dt = cal.getTime();
            prevDate = dateFormat.format(dt);
        }catch (Exception e) {
            prevDate = "";
        }
        return prevDate;
    }

    public static String getPreviousMonthDate() {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        cal.add(Calendar.DATE, -31);
        Date date = cal.getTime();
        return dateFormat.format(date);
    }

    public static String getPreviousWeekDate() {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        cal.add(Calendar.DATE, -7);
        Date date = cal.getTime();
        return dateFormat.format(date);
    }

    public static String getPreviousWeekDate(String format) {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(format);
        cal.add(Calendar.DATE, -7);
        Date date = cal.getTime();
        return dateFormat.format(date);
    }

    public static String getPreviousWeekDate(int checkDay) {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        cal.add(Calendar.DATE, -(7+checkDay));
        Date date = cal.getTime();
        return dateFormat.format(date);
    }

    public static String getNextDayDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dt = null;
        try {
            cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            cal.add(Calendar.DATE, +1);
            dt = cal.getTime();
        }catch (Exception e){
            System.out.println("Error during format");
            return "";
        }
        return sdf.format(dt);
    }

//    public static String getWeekDate(String checkDay) {
//        Date dt = convertStrToDate(checkDay, "YYYY-mm-DD");
//        cal = Calendar.getInstance();
//        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
//        return dateFormat.format(date);
//    }

    public static String getTomorrowDate() {
        return getTomorrowDate(yyyy_MM_dd);
    }

    public static String getTomorrowDate(String format) {
        cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat(format);
        cal.add(Calendar.DATE, +1);
        Date date = cal.getTime();
        return dateFormat.format(date);
//        return "2024-02-22";
    }

    public static Date convertStrToDate(String date, String format){
        Date dt = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            dt = dateFormat.parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return dt;
    }

    public static int getDateDiffBetweenTwoDate(String first,String formatFirst, String last, String formatSecond) {
        Date dtFirst = null;
        Date dtSecond = null;
        int diff= 0;
        try {
            DateFormat dateFormatFirst = new SimpleDateFormat(formatFirst);
            DateFormat dateFormatSecond = new SimpleDateFormat(formatSecond);
            dtFirst = dateFormatFirst.parse(first);
            dtSecond = dateFormatFirst.parse(last);
//            Period p = Period.between(LocalDate.of(dtFirst.getYear(), dtFirst.getMonth(),dtFirst.getDate()),
//                    LocalDate.of(dtSecond.getYear(), dtSecond.getMonth(),dtSecond.getDate()));
            long diffInDays = ChronoUnit.DAYS.between(LocalDateTime.ofInstant(dtFirst.toInstant(), ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(dtSecond.toInstant(), ZoneId.systemDefault()));
            diff = (int) diffInDays;
        }catch (Exception e){
            e.printStackTrace();
        }
//        return Math.abs(diff) / (24 * 60 * 60 * 1000);
        if (diff <1)
            diff = diff *-1;
        return diff;
    }
    public static long getDateDiffFromToday(String resultDate) {
        Date dt = null;
        long diff= 0;
        try {
            DateFormat dateFormat = new SimpleDateFormat(DD_MO_YYYY);
            dt = dateFormat.parse(resultDate);
//            diff = dt.getTime() - new Date().getTime();
            Date d = new Date();
            Period p = Period.between(LocalDate.of(dt.getYear(), dt.getMonth(),dt.getDate()), LocalDate.of(d.getYear(), d.getMonth(),d.getDate()));
            diff = p.getDays();
        }catch (Exception e){
            e.printStackTrace();
        }
//        return Math.abs(diff) / (24 * 60 * 60 * 1000);
        return diff;
    }

    public static boolean isDateInCurrentMonth(String strDate) {
        LocalDate d1 = LocalDate.parse(strDate);
        LocalDate d2 = LocalDate.now().minusMonths(1);
        if (d1.isBefore(d2))
            return false;
        return true;
    }
    public static boolean isDateInCurrentWeek(String strDate) {
        LocalDate d1 = LocalDate.parse(strDate);
        LocalDate d2 = LocalDate.now().minusDays(6);
        if(d1.isBefore(d2)){
            return false;
        }
        return true;
    }

    public static boolean isDateInThisMonth(String strDate, String format, int month) {
        boolean flag = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date targetDt = sdf.parse(strDate);
            if(targetDt.getMonth() == month)
                flag = true;
        }catch (Exception e){
            System.out.println("Date-"+strDate);
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean isDateInThisWeek(String strDate, String format, int week) {
        boolean flag = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date targetDt = sdf.parse(strDate);
            GregorianCalendar calendar = DateToGregorianCal(targetDt);
            if(calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) == week)
                flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean isDateBetweenTwoDates(Date checkDate, Date begin, Date end){
        boolean flag = false;
        if (checkDate.compareTo(begin)==0 || checkDate.compareTo(end)==0 ||
                (checkDate.after(begin) && checkDate.before(end)))
            flag = true;
        return flag;
    }

    public static boolean isBothDateSame(Date date1, Date date2) {
        return date1.compareTo(date2)==0;
    }

    public static GregorianCalendar DateToGregorianCal(Date dt){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return cal;
    }

    public static LocalDate DateToLocalDate(Date dt) {
        return LocalDate.of(dt.getYear(), dt.getMonth(), dt.getDate());
    }

    public static String checkIfMondayGivePrevWeekDate(String checkDay, String format) {
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        Date dt = convertStrToDate(checkDay, format);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
            cal.add(Calendar.DATE, -7);
        }
        Date date = cal.getTime();
        return dateFormat.format(date);
    }

    public static String getThisWeekModay() {
        return LocalDate.now( ZoneId.of( "Asia/Kolkata" ) )
                        .with(TemporalAdjusters.previous( DayOfWeek.MONDAY )).format( DateTimeFormatter.ISO_LOCAL_DATE );
    }

    public static boolean checkIfTodayIsGivenDay(String checkDay, String format, int day) {
        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd);
        Date dt = convertStrToDate(checkDay, format);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        if(cal.get(Calendar.DAY_OF_WEEK) == day){
            return true;
        }
        return false;
    }

    public static String convertDateToStr(Date end, String fomat) {
        String dt = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fomat);
            dt = sdf.format(end);
        }catch (Exception e){
            e.printStackTrace();
        }
        return dt;
    }

    public static String getPreviousDateOfGivenDate(String date) {
        String prevDate = "";
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = dateFormat.parse(date);
            Date oneDayBefore = new Date(myDate.getTime() - 2);
            prevDate = dateFormat.format(oneDayBefore);
        }catch (Exception e){
            e.printStackTrace();
        }
        return prevDate;
    }

    public static void getAllSatDateOfThisMonth() {

    }

    public static Date getMonthFirstDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        return calendar.getTime();
    }

    public static Date getMonthLastDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

//    public static Date convertStrToDate(String date, String format){
//        Date dt = null;
//        try {
//            DateFormat dateFormat = new SimpleDateFormat(format);
//            dt = dateFormat.parse(date);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return dt;
//    }

}
