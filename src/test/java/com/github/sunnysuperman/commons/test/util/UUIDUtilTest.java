package com.github.sunnysuperman.commons.test.util;

import java.util.Date;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.utils.FormatUtil;
import com.github.sunnysuperman.commons.utils.UUIDUtil;

public class UUIDUtilTest extends TestCase {

	public void test_genShortUUID() {
		System.out.println(UUIDUtil.genShortUUID());
		System.out.println(UUIDUtil.genShortUUID());
		System.out.println(UUIDUtil.genShortUUID());
	}

	public void test_genShortUUID_max() {
		Date date = new Date(1000L * Integer.MAX_VALUE);
		System.out.println(FormatUtil.formatISO8601Date(date));
	}
}
