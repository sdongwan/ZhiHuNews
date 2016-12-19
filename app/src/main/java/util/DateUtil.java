package util;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/11/25.
 */

public class DateUtil {
    //获取当前的日期
    public static String getCurrentStringDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTime(new Date(System.currentTimeMillis()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = "" + year + month + day + "";
        // String date = "" + year + "-" + month + "-" + day + "";
        return date;
    }

    //获取前n天的日期
    public static String getBeforeNStringDate(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTime(new Date(System.currentTimeMillis() - n * 24 * 3600 * 1000L));
        //calendar.setTime(new Date(System.currentTimeMillis() -n * 24 * 3600 * 1000));
        // calendar.setTimeInMillis(System.currentTimeMillis() -n * 24 * 3600 * 1000);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthStr = month + "";
        String dayStr = day + "";
        if (month < 10) {
            monthStr = "0" + monthStr;

        }
        if (day < 10) {

            dayStr = "0" + day;
        }
        // String date = "" + year + "-" + month + "-" + day + "";

        String date = "" + year + monthStr + dayStr + "";
        return date;

    }

    public static String getBeforeNFormatDate(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTime(new Date(System.currentTimeMillis() - n * 24 * 3600 * 1000L));
        //calendar.setTime(new Date(System.currentTimeMillis() -n * 24 * 3600 * 1000));
        // calendar.setTimeInMillis(System.currentTimeMillis() -n * 24 * 3600 * 1000);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;//星期几
        String dayStr = "";
        switch (dayOfWeek) {
            case 1:
                dayStr = "一";
                break;
            case 2:
                dayStr = "二";
                break;
            case 3:
                dayStr = "三";
                break;
            case 4:
                dayStr = "四";
                break;
            case 5:
                dayStr = "五";
                break;
            case 6:
                dayStr = "六";
                break;
            default:
                dayStr = "日";
                break;

        }
        String date = "" + month + "月" + day + "日" + "  星期" + dayStr;
        return date;
    }

    public static String convertTime(Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.setTime(new Date(time * 1000));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = "" + year + "-" + month + "-" + day + "";
        Log.e("time", System.currentTimeMillis() + "");
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String hourStr = hour + "";
        String minuteStr = minute + "";
        String monthStr = month + "";
        String dayStr = day + "";

        if (hour < 10) {
            hourStr = "0" + hourStr;
        }
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }
        if (month < 10) {
            monthStr = "0" + monthStr;
        }
        if (day < 10) {
            dayStr = "0" + dayStr;
        }


        return monthStr + "-" + dayStr + "  " + hourStr + ":" + minuteStr;
    }


}
