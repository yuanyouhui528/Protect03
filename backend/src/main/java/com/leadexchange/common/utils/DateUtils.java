package com.leadexchange.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 日期时间工具类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    // 常用日期时间格式
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String MM_DD = "MM-dd";
    public static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    // 常用格式化器
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    public static final DateTimeFormatter DATETIME_MS_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_SS);
    public static final DateTimeFormatter COMPACT_DATE_FORMATTER = DateTimeFormatter.ofPattern(YYYYMMDD);
    public static final DateTimeFormatter COMPACT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);

    // 默认时区
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    public static final ZoneId BEIJING_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 获取当前时间
     * 
     * @return LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     * 
     * @return LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间戳(毫秒)
     * 
     * @return 时间戳
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳(秒)
     * 
     * @return 时间戳
     */
    public static long currentTimeSeconds() {
        return Instant.now().getEpochSecond();
    }

    // =============================格式化============================

    /**
     * 格式化日期时间
     * 
     * @param dateTime 日期时间
     * @param pattern 格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        try {
            if (dateTime == null) {
                return null;
            }
            return dateTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            logger.error("日期时间格式化失败: dateTime={}, pattern={}", dateTime, pattern, e);
            return null;
        }
    }

    /**
     * 格式化日期
     * 
     * @param date 日期
     * @param pattern 格式
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, String pattern) {
        try {
            if (date == null) {
                return null;
            }
            return date.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            logger.error("日期格式化失败: date={}, pattern={}", date, pattern, e);
            return null;
        }
    }

    /**
     * 格式化时间
     * 
     * @param time 时间
     * @param pattern 格式
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time, String pattern) {
        try {
            if (time == null) {
                return null;
            }
            return time.format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            logger.error("时间格式化失败: time={}, pattern={}", time, pattern, e);
            return null;
        }
    }

    /**
     * 使用默认格式格式化日期时间 (yyyy-MM-dd HH:mm:ss)
     * 
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return format(dateTime, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 使用默认格式格式化日期 (yyyy-MM-dd)
     * 
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatDate(LocalDate date) {
        return format(date, YYYY_MM_DD);
    }

    /**
     * 使用默认格式格式化时间 (HH:mm:ss)
     * 
     * @param time 时间
     * @return 格式化后的字符串
     */
    public static String formatTime(LocalTime time) {
        return format(time, HH_MM_SS);
    }

    // =============================解析============================

    /**
     * 解析日期时间字符串
     * 
     * @param dateTimeStr 日期时间字符串
     * @param pattern 格式
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        try {
            if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            logger.error("日期时间解析失败: dateTimeStr={}, pattern={}", dateTimeStr, pattern, e);
            return null;
        }
    }

    /**
     * 解析日期字符串
     * 
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            logger.error("日期解析失败: dateStr={}, pattern={}", dateStr, pattern, e);
            return null;
        }
    }

    /**
     * 解析时间字符串
     * 
     * @param timeStr 时间字符串
     * @param pattern 格式
     * @return LocalTime
     */
    public static LocalTime parseTime(String timeStr, String pattern) {
        try {
            if (timeStr == null || timeStr.trim().isEmpty()) {
                return null;
            }
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            logger.error("时间解析失败: timeStr={}, pattern={}", timeStr, pattern, e);
            return null;
        }
    }

    /**
     * 使用默认格式解析日期时间 (yyyy-MM-dd HH:mm:ss)
     * 
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 使用默认格式解析日期 (yyyy-MM-dd)
     * 
     * @param dateStr 日期字符串
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, YYYY_MM_DD);
    }

    /**
     * 使用默认格式解析时间 (HH:mm:ss)
     * 
     * @param timeStr 时间字符串
     * @return LocalTime
     */
    public static LocalTime parseTime(String timeStr) {
        return parseTime(timeStr, HH_MM_SS);
    }

    // =============================转换============================

    /**
     * Date转LocalDateTime
     * 
     * @param date Date对象
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * LocalDateTime转Date
     * 
     * @param dateTime LocalDateTime对象
     * @return Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return Date.from(dateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * LocalDate转Date
     * 
     * @param date LocalDate对象
     * @return Date
     */
    public static Date toDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 时间戳转LocalDateTime
     * 
     * @param timestamp 时间戳(毫秒)
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE_ID);
    }

    /**
     * LocalDateTime转时间戳
     * 
     * @param dateTime LocalDateTime对象
     * @return 时间戳(毫秒)
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * LocalDate转时间戳(当天开始时间)
     * 
     * @param date LocalDate对象
     * @return 时间戳(毫秒)
     */
    public static long toTimestamp(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return date.atStartOfDay(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    // =============================计算============================

    /**
     * 计算两个日期之间的天数差
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 计算两个时间之间的小时差
     * 
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 小时差
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }

    /**
     * 计算两个时间之间的分钟差
     * 
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 分钟差
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }

    /**
     * 计算两个时间之间的秒数差
     * 
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 秒数差
     */
    public static long secondsBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.SECONDS.between(startDateTime, endDateTime);
    }

    // =============================日期操作============================

    /**
     * 增加天数
     * 
     * @param date 日期
     * @param days 天数
     * @return 新日期
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }

    /**
     * 减少天数
     * 
     * @param date 日期
     * @param days 天数
     * @return 新日期
     */
    public static LocalDate minusDays(LocalDate date, long days) {
        if (date == null) {
            return null;
        }
        return date.minusDays(days);
    }

    /**
     * 增加小时
     * 
     * @param dateTime 日期时间
     * @param hours 小时数
     * @return 新日期时间
     */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }

    /**
     * 减少小时
     * 
     * @param dateTime 日期时间
     * @param hours 小时数
     * @return 新日期时间
     */
    public static LocalDateTime minusHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.minusHours(hours);
    }

    /**
     * 增加分钟
     * 
     * @param dateTime 日期时间
     * @param minutes 分钟数
     * @return 新日期时间
     */
    public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusMinutes(minutes);
    }

    /**
     * 减少分钟
     * 
     * @param dateTime 日期时间
     * @param minutes 分钟数
     * @return 新日期时间
     */
    public static LocalDateTime minusMinutes(LocalDateTime dateTime, long minutes) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.minusMinutes(minutes);
    }

    // =============================特殊日期============================

    /**
     * 获取月初日期
     * 
     * @param date 日期
     * @return 月初日期
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取月末日期
     * 
     * @param date 日期
     * @return 月末日期
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取年初日期
     * 
     * @param date 日期
     * @return 年初日期
     */
    public static LocalDate getFirstDayOfYear(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取年末日期
     * 
     * @param date 日期
     * @return 年末日期
     */
    public static LocalDate getLastDayOfYear(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 获取周一日期
     * 
     * @param date 日期
     * @return 周一日期
     */
    public static LocalDate getFirstDayOfWeek(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 获取周日日期
     * 
     * @param date 日期
     * @return 周日日期
     */
    public static LocalDate getLastDayOfWeek(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    // =============================判断============================

    /**
     * 判断是否为闰年
     * 
     * @param year 年份
     * @return 是否为闰年
     */
    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    /**
     * 判断日期是否为今天
     * 
     * @param date 日期
     * @return 是否为今天
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now());
    }

    /**
     * 判断日期是否为昨天
     * 
     * @param date 日期
     * @return 是否为昨天
     */
    public static boolean isYesterday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now().minusDays(1));
    }

    /**
     * 判断日期是否为明天
     * 
     * @param date 日期
     * @return 是否为明天
     */
    public static boolean isTomorrow(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now().plusDays(1));
    }

    /**
     * 判断日期是否为周末
     * 
     * @param date 日期
     * @return 是否为周末
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) {
            return false;
        }
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 判断时间是否在指定范围内
     * 
     * @param dateTime 待判断时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否在范围内
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime startTime, LocalDateTime endTime) {
        if (dateTime == null || startTime == null || endTime == null) {
            return false;
        }
        return !dateTime.isBefore(startTime) && !dateTime.isAfter(endTime);
    }

    /**
     * 判断日期是否在指定范围内
     * 
     * @param date 待判断日期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 是否在范围内
     */
    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // =============================时区转换============================

    /**
     * 转换时区
     * 
     * @param dateTime 日期时间
     * @param fromZone 源时区
     * @param toZone 目标时区
     * @return 转换后的日期时间
     */
    public static LocalDateTime convertTimeZone(LocalDateTime dateTime, ZoneId fromZone, ZoneId toZone) {
        if (dateTime == null || fromZone == null || toZone == null) {
            return dateTime;
        }
        return dateTime.atZone(fromZone).withZoneSameInstant(toZone).toLocalDateTime();
    }

    /**
     * 转换为UTC时间
     * 
     * @param dateTime 本地时间
     * @return UTC时间
     */
    public static LocalDateTime toUtc(LocalDateTime dateTime) {
        return convertTimeZone(dateTime, DEFAULT_ZONE_ID, UTC_ZONE_ID);
    }

    /**
     * 从UTC时间转换为本地时间
     * 
     * @param utcDateTime UTC时间
     * @return 本地时间
     */
    public static LocalDateTime fromUtc(LocalDateTime utcDateTime) {
        return convertTimeZone(utcDateTime, UTC_ZONE_ID, DEFAULT_ZONE_ID);
    }

    /**
     * 转换为北京时间
     * 
     * @param dateTime 日期时间
     * @return 北京时间
     */
    public static LocalDateTime toBeijingTime(LocalDateTime dateTime) {
        return convertTimeZone(dateTime, DEFAULT_ZONE_ID, BEIJING_ZONE_ID);
    }
}