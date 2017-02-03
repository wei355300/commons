package com.github.sunnysuperman.commons.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import com.github.sunnysuperman.commons.utils.DateUtil;
import com.github.sunnysuperman.commons.utils.FormatUtil;

public class FormatUtilTest extends TestCase {

	@Test
	public final void testParseISO8601Date() {
		try {
			{
				Date d1 = FormatUtil.parseISO8601Date("2014-07-08T12:00:45.235Z");
				Calendar c1 = Calendar.getInstance();
				c1.setTime(d1);
				System.out.println(d1 + " mills:" + c1.get(Calendar.MILLISECOND));
			}

			{
				Date d1 = FormatUtil.parseISO8601Date("2014-07-08T12:00:45Z");
				Calendar c1 = Calendar.getInstance();
				c1.setTime(d1);
				System.out.println(d1 + " mills:" + c1.get(Calendar.MILLISECOND));
			}

			{
				Date d1 = FormatUtil.parseISO8601Date("2014-07-08T12:00:45+09:00");
				Calendar c1 = Calendar.getInstance();
				c1.setTime(d1);
				System.out.println(d1 + " mills:" + c1.get(Calendar.MILLISECOND));
			}

			{
				Date d1 = FormatUtil.parseISO8601Date("2014-07-08T12:00:45.789+09:00");
				Calendar c1 = Calendar.getInstance();
				c1.setTime(d1);
				System.out.println(d1 + " mills:" + c1.get(Calendar.MILLISECOND));
			}

			{
				Date d1 = FormatUtil.parseISO8601Date("2014-07-08T12:00:45.789+08");
				Calendar c1 = Calendar.getInstance();
				c1.setTime(d1);
				System.out.println(d1 + " mills:" + c1.get(Calendar.MILLISECOND));
			}

			{
				System.out.println(FormatUtil.formatISO8601Date(new Date()));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public final void testformatISO8601Date() {
		Date date = new Date();
		System.out.println(FormatUtil.formatISO8601Date(date, DateUtil.getTimezone(0)));
		System.out.println(FormatUtil.formatISO8601Date(date, DateUtil.getTimezone(7.5f)));
		System.out.println(FormatUtil.formatISO8601Date(date, DateUtil.getTimezone(-8f)));
		System.out.println(FormatUtil.formatISO8601Date(date, DateUtil.getTimezone(8f)));
	}
}
