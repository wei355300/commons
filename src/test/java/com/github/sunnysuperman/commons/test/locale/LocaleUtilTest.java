package com.github.sunnysuperman.commons.test.locale;

import junit.framework.TestCase;

import org.junit.Test;

import com.github.sunnysuperman.commons.locale.LocaleUtil;

public class LocaleUtilTest extends TestCase {

	@Test
	public final void test() {
		System.out.println(LocaleUtil.formatLocale("zh-Hans"));
		System.out.println(LocaleUtil.formatLocale("zh-Hant"));
		System.out.println(LocaleUtil.formatLocale("zh-Hans-NZ"));
		System.out.println(LocaleUtil.formatLocale("en"));
		System.out.println(LocaleUtil.formatLocale("en-AU"));
		System.out.println(LocaleUtil.formatLocale("sr-sp"));
		System.out.println(LocaleUtil.formatLocale("sr_"));
	}

	@Test
	public final void test2() {
		System.out.println(LocaleUtil.formatLocale("zh-Hans-CN").equals("zh_CN"));
		System.out.println(LocaleUtil.formatLocale("zh-Hant-CN").equals("zh_TW"));
	}
}
