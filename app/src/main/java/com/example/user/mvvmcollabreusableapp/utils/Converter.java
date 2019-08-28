package com.example.user.mvvmcollabreusableapp.utils;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Converter {

    public static String convertTime(String createdAt) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        long timeVal = 0;
        try {
            timeVal = sdf.parse(createdAt).getTime();
        } catch (ParseException e) {
            Log.e("Exception:", e.getMessage());
        }
        long now = System.currentTimeMillis();

        CharSequence ago =
                DateUtils.getRelativeTimeSpanString(timeVal, now, DateUtils.MINUTE_IN_MILLIS);
        return String.valueOf(ago);
    }
}
