package com.huburt.app.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by hubert on 2018/5/24.
 */
public class TimeZoneUtils {

    private static final String TZ_ID_BJ = "Asia/Shanghai";

    /**
     * 根据旧时区的date返回一个新时区的date
     *
     * @param date
     * @param oldZone
     * @param newZone
     * @return
     */
    public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
        return new Date(date.getTime() - timeOffset);
    }

    /**
     * 将北京时间转换成本地时间
     *
     * @param bjTime 北京时间
     * @param format 输入输出格式
     * @return 本地时间
     */
    public static String BJTimeToLocal(String bjTime, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = dateFormat.parse(bjTime);
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将本地Date格式化成北京时间
     *
     * @param date   本地Date
     * @param format 格式
     * @return 北京时间
     */
    public static String formatDateToBJString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(TZ_ID_BJ));
        return dateFormat.format(date);
    }
}
