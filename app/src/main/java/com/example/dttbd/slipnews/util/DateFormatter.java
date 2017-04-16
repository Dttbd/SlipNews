package com.example.dttbd.slipnews.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dttbd
 * 方便将日期转换为String类型
 */

public class DateFormatter {

    public String ZhihuDailyDateFormat(int year, int month, int day) {

        String date = String.valueOf(new StringBuilder()
                .append(year)
                .append((month + 1) < 10 ? "0" + (month + 1) : (month + 1))
                .append((day < 9) ? "0" + (day+1) : (day+1)));

        return date;
    }

}