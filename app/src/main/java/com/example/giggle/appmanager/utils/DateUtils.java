package com.example.giggle.appmanager.utils;

/**
 * Created by leishifang on 2017/3/24 11:05.
 */

public class DateUtils {

    public static String convertTimeMill(Long timeMill) {
        return android.text.format.DateFormat.format("yy/MM/dd", timeMill).toString();
    }
}
