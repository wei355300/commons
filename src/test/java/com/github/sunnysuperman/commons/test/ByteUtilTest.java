package com.github.sunnysuperman.commons.test;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.utils.ByteUtil;
import com.github.sunnysuperman.commons.utils.StringUtil;

public class ByteUtilTest extends TestCase {

	public void test_double2bytes() {
		double d = 1234567.089;
		byte[] bytes = ByteUtil.double2bytes(d);
		System.out.println(StringUtil.join(bytes, ","));
		double d2 = ByteUtil.bytes2double(bytes);
		assertTrue(d == d2);
	}

	public void test_short2bytes() {
		short d = Short.MAX_VALUE;
		byte[] bytes = ByteUtil.short2bytes(d);
		System.out.println(StringUtil.join(bytes, ","));
		short d2 = ByteUtil.bytes2short(bytes);
		assertTrue(d == d2);
	}
}
