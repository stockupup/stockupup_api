package com.xxxz.stockupup.v1.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @autor jiangll
 * @date 2021/9/8
 * 日期处理工具
 */
@Slf4j
public final class DateUtil implements Serializable {
    private static final long serialVersionUID = 2514628309823061613L;

    /**
     * 获取今日零点时间戳
     */
    public static long getTodayTs() {
        String s = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        long ds = 0;
        try {
            ds = DateUtils.parseDate(s + " 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
        } catch (ParseException e) {
            log.error("-- 获取时间戳失败 --\n");
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * 获取昨日零点时间戳
     */
    public static long getYesterdayTs() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String s = DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd");
        long ds = 0;
        try {
            ds = DateUtils.parseDate(s + " 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
        } catch (ParseException e) {
            log.error("-- 获取时间戳失败 --\n");
            e.printStackTrace();
        }
        return ds;
    }
}
