package com.github.sunnysuperman.commons.test.repository;

import java.util.Date;

import org.junit.Test;

import com.github.sunnysuperman.commons.repository.db.DBUtil;
import com.github.sunnysuperman.commons.test.BaseTest;
import com.github.sunnysuperman.commons.utils.FormatUtil;

public class DBUtilTest extends BaseTest {

	@Test
	public void test_fixChars2date() throws Exception {
		System.out.println(FormatUtil.formatISO8601Date(DBUtil.fixChars2date("001423152300828")).equals(
				"2015-02-05T08:05:00.828Z"));
	}

	@Test
	public void test_date2fixChars() throws Exception {
		Date date = FormatUtil.parseISO8601Date("1969-12-31T23:00:00.000Z");
		System.out.println(date.getTime());
	}

	@Test
	public void test_number() throws Exception {
		String s = "-0110";
		System.out.println(Long.parseLong(s));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test_date2fixChars2() throws Exception {

		String[] dates = new String[] { "0-01-01T00:00:00.000Z", "1600-01-01T00:00:00.000Z",
				"1900-01-01T00:00:00.000Z", "2000-01-01T00:00:00.000Z", "2200-01-01T00:00:00.000Z",
				"5000-01-01T00:00:00.000Z", "9999-01-01T00:00:00.000Z" };

		for (String dateAsStr : dates) {
			Date date = FormatUtil.parseISO8601Date(dateAsStr);
			System.out.println(date);
			String s = DBUtil.date2fixChars(date);
			long t = date.getTime();
			t += (-60 * 1000 * date.getTimezoneOffset());
			System.out.println(t);
			System.out.println(s);
			System.out.println("================");
			assertTrue(s.length() == 15);
		}

	}
}
