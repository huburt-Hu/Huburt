package com.huburt.app.test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by hubert on 2018/5/24.
 */
public class TimeZoneTest {

    public static void main(String[] args) {
        String[] availableIDs = TimeZone.getAvailableIDs();
        System.out.println(Arrays.toString(availableIDs));

        //模拟本地环境
        TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Honolulu"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
//            Date date = dateFormat.parse(time);
//            System.out.println(changeTimeZone(date, TimeZone.getTimeZone("Asia/Shanghai"), TimeZone.getDefault()));

            Calendar instance = Calendar.getInstance();
            Date now = instance.getTime();
            System.out.println("original:" + dateFormat.format(now));
            String bjTime = TimeZoneUtils.formatDateToBJString(now, "yyyy-MM-dd HH:mm:ss");
            System.out.println("bj zone:" + bjTime);

            System.out.println(TimeZoneUtils.BJTimeToLocal(bjTime, "yyyy-MM-dd HH:mm:ss"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}