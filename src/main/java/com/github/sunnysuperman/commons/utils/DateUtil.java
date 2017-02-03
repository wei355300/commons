package com.github.sunnysuperman.commons.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 日期工具类
 * 
 * 
 *
 */
public class DateUtil {

	/**
	 * 一分钟（用毫秒表示）
	 */
	public static final long MILLS_AMINUTE = 60 * 1000;

	/**
	 * 一小时（用毫秒表示）
	 */
	public static final long MILLS_ANHOUR = 3600 * 1000;

	/**
	 * 一天（用毫秒表示）
	 */
	public static final long MILLS_ADAY = MILLS_ANHOUR * 24L;

	/**
	 * 将一个整数日期转换为Calendar对象
	 * 
	 * @param day
	 *            日期，20160111，8位数
	 * @param cal
	 *            日期
	 * @return day对应的日期
	 */
	public static Calendar day2date(int day, Calendar cal) {
		cal.clear();
		cal.set(Calendar.YEAR, day / 10000);
		cal.set(Calendar.MONTH, (day % 10000) / 100 - 1);
		cal.set(Calendar.DAY_OF_MONTH, day % 100);
		return cal;
	}

	/**
	 * 将当前日期转换为int
	 * 
	 * @param cal
	 *            日期
	 * @return int日期
	 */
	public static int date2day(Calendar cal) {
		return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取一定偏移量的时区
	 * 
	 * @param offset
	 *            偏移量，小时数
	 * @return 偏移的时区
	 */
	public static TimeZone getTimezone(float offset) {
		TimeZone timezone = TimeZone.getDefault();
		timezone.setRawOffset((int) (offset * MILLS_ANHOUR));
		return timezone;
	}

	public static boolean isValidTimezone(float timezone) {
		return timezone >= -14 && timezone <= 14;
	}

}
