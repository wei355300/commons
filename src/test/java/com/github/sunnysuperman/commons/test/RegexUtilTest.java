package com.github.sunnysuperman.commons.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.github.sunnysuperman.commons.utils.CollectionUtil;
import com.github.sunnysuperman.commons.utils.RegexUtil;

public class RegexUtilTest extends TestCase {

	// @Test
	// public final void testPerformance() {
	// String s = "${aa}bbbb ddddddddd eeee ${xx} ddd";
	// int counter = 10000;
	// Map<String, Object> context = CollectionUtil.arrayAsMap("xx", "yy");
	//
	// {
	// long t1 = System.currentTimeMillis();
	// for (int i = 0; i < 1000; i++) {
	// RegexUtil.compileOld(s, context, false);
	// }
	// long t2 = System.currentTimeMillis();
	// System.out.println(t2 - t1);
	// }
	//
	// {
	// long t1 = System.currentTimeMillis();
	// for (int i = 0; i < counter; i++) {
	// RegexUtil.compile(s, context, false);
	// }
	// long t2 = System.currentTimeMillis();
	// System.out.println(t2 - t1);
	// }
	//
	// {
	// long t1 = System.currentTimeMillis();
	// for (int i = 0; i < counter; i++) {
	// RegexUtil.compileOld(s, context, false);
	// }
	// long t2 = System.currentTimeMillis();
	// System.out.println(t2 - t1);
	// }
	// }

	@Test
	public final void test() {
		String[] tests = new String[] { "${xx}", "$a", "a$", "a$b", "${a}b", "b${a}$}$" };
		for (String test : tests) {
			try {
				System.out.print(RegexUtil.compile(test, CollectionUtil.arrayAsMap("xx", "yy"), false));
				// System.out.print(",");
				// System.out.print(RegexUtil.compile(test,
				// CollectionUtil.arrayAsMap("xx", "yy"), true));
				System.out.println();
			} catch (Exception ex) {
				ex.printStackTrace();
				assertTrue(false);
			}
		}

	}
}
