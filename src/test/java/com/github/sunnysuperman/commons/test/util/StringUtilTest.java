package com.github.sunnysuperman.commons.test.util;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.utils.StringUtil;
import com.github.sunnysuperman.commons.utils.UUIDUtil;

public class StringUtilTest extends TestCase {

	private static final String ALPHA_NUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE_ALPHA_NUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz";

	public final void testRandom() {
		System.out.println(StringUtil.randomString(ALPHA_NUMERIC, 10));
	}

	public final void testIsTargetString() {
		assertTrue(StringUtil.isTargetString(ALPHA_NUMERIC, "aE"));
		assertTrue(StringUtil.isTargetString(LOWERCASE_ALPHA_NUMERIC, "ae9"));
		assertTrue(!StringUtil.isTargetString(ALPHA_NUMERIC, "a_b"));
		assertTrue(!StringUtil.isTargetString(LOWERCASE_ALPHA_NUMERIC, "aE"));
	}

	public final void test_isNumeric() {
		assertTrue(StringUtil.isNumeric('0'));
		assertTrue(StringUtil.isNumeric('1'));
		assertTrue(StringUtil.isNumeric('2'));
		assertTrue(StringUtil.isNumeric('3'));
		assertTrue(StringUtil.isNumeric('4'));
		assertTrue(StringUtil.isNumeric('5'));
		assertTrue(StringUtil.isNumeric('6'));
		assertTrue(StringUtil.isNumeric('7'));
		assertTrue(StringUtil.isNumeric('8'));
		assertTrue(StringUtil.isNumeric('9'));
		assertTrue(StringUtil.isNumeric("10"));
		assertTrue(StringUtil.isNumeric("123"));

		assertFalse(StringUtil.isNumeric('a'));
		assertFalse(StringUtil.isNumeric('f'));
		assertFalse(StringUtil.isNumeric('z'));
		assertFalse(StringUtil.isNumeric("1a"));
	}

	public final void testUUID() {
		System.out.println(UUIDUtil.genUUID());
	}

	public final void testReplaceAll() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < LOWERCASE_ALPHA_NUMERIC.length(); i++) {
			builder.append(LOWERCASE_ALPHA_NUMERIC.charAt(i));
			if (i != LOWERCASE_ALPHA_NUMERIC.length() - 1) {
				builder.append("-");
			}
		}
		String dest = StringUtil.replaceAll(builder.toString(), "-", "+");
		System.out.println(dest);
	}

	public final void test_camel2underline() {
		assertTrue(StringUtil.camel2underline("appId").equals("app_id"));
		assertTrue(StringUtil.camel2underline("appU").equals("app_u"));
		assertTrue(StringUtil.camel2underline("appIosId").equals("app_ios_id"));
		assertTrue(StringUtil.camel2underline("app").equals("app"));
		assertTrue(StringUtil.camel2underline("App").equals("_app"));
	}

	public final void test_underline2camel() {
		assertTrue(StringUtil.underline2camel("app_id").equals("appId"));
		assertTrue(StringUtil.underline2camel("app_u").equals("appU"));
		assertTrue(StringUtil.underline2camel("app_ios_id").equals("appIosId"));
		assertTrue(StringUtil.underline2camel("app_").equals("app"));
		assertTrue(StringUtil.underline2camel("_app").equals("App"));
	}

	public void testSplit() {
		System.out.println(StringUtil.split("/a", "/").size());
		System.out.println(StringUtil.split("/a/b", "/").size());
	}
}
