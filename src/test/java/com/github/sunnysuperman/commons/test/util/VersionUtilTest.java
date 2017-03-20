package com.github.sunnysuperman.commons.test.util;

import junit.framework.TestCase;

import org.junit.Test;

import com.github.sunnysuperman.commons.utils.VersionUtil;

public class VersionUtilTest extends TestCase {

	@Test
	public final void test_compare() {

		assertTrue(VersionUtil.compare("2.3", "2.3") == 0);
		assertTrue(VersionUtil.compare("2.3.0", "2.3") == 0);
		assertTrue(VersionUtil.compare("2.3", "2.3.0") == 0);
		assertTrue(VersionUtil.compare("2.3", "2.3.0.0") == 0);
		assertTrue(VersionUtil.compare("2.3.0.0", "2.3") == 0);

		assertTrue(VersionUtil.compare("2.3.1", "2.3") > 0);
		assertTrue(VersionUtil.compare("2.3.1", "2.3.0") > 0);
		assertTrue(VersionUtil.compare("2.4", "2.3.5") > 0);
		assertTrue(VersionUtil.compare("2.3.0.5", "2.3") > 0);

		assertTrue(VersionUtil.compare("2.3.1", "2.4") < 0);
		assertTrue(VersionUtil.compare("2.3", "2.3.1") < 0);
		assertTrue(VersionUtil.compare("2.3.0", "2.3.1") < 0);
		assertTrue(VersionUtil.compare("2.3.0", "2.4") < 0);
		assertTrue(VersionUtil.compare("2.3", "2.3.0.5") < 0);
	}

	@Test
	public final void test_isGreaterThan() {

		assertTrue(VersionUtil.isGreaterThan("2.3", "2.2"));
		assertTrue(VersionUtil.isGreaterThan("2.1", "2"));
		assertTrue(VersionUtil.isGreaterThan("2.10", "2.2"));
		assertTrue(VersionUtil.isGreaterThan("v2.3", "V2.2.6"));
		assertTrue(VersionUtil.isGreaterThan("2.3.1", "2.3"));
		assertTrue(VersionUtil.isGreaterThan("2.3.10", "2.3.9"));

		assertTrue(!VersionUtil.isGreaterThan("2.3", "2.3.1"));
		assertTrue(!VersionUtil.isGreaterThan("2.3", "2.3"));
		assertTrue(!VersionUtil.isGreaterThan("2.3.9", "2.5"));
	}

	@Test
	public final void test_isGreaterThanOrEqual() {
		assertTrue(VersionUtil.isGreaterThanOrEqual("2.3", "2.2"));
		assertTrue(VersionUtil.isGreaterThanOrEqual("2.3", "2.3"));
		assertTrue(VersionUtil.isGreaterThanOrEqual("2.3", "2.3.0"));
		assertTrue(!VersionUtil.isGreaterThanOrEqual("2.3", "2.4"));
	}

	@Test
	public final void test_isLessThan() {
		assertTrue(VersionUtil.isLessThan("2.2", "2.3"));
		assertTrue(!VersionUtil.isLessThan("2.2", "2.2"));
		assertTrue(!VersionUtil.isLessThan("2.2", "2.1"));
	}

	@Test
	public final void test_isLessThanOrEqual() {
		assertTrue(VersionUtil.isLessThanOrEqual("2.2", "2.3"));
		assertTrue(VersionUtil.isLessThanOrEqual("2.2", "2.2"));
		assertTrue(VersionUtil.isLessThanOrEqual("2.2", "2.2.0"));
		assertTrue(!VersionUtil.isLessThanOrEqual("2.2", "2.1"));
	}

	@Test
	public final void testIsValidVersion() {

		assertTrue(VersionUtil.isValidVersion("2"));
		assertTrue(VersionUtil.isValidVersion("2.1"));
		assertTrue(VersionUtil.isValidVersion("2.1.5"));

		assertTrue(!VersionUtil.isValidVersion(null));
		assertTrue(!VersionUtil.isValidVersion(""));
		assertTrue(!VersionUtil.isValidVersion("x"));
		assertTrue(!VersionUtil.isValidVersion("2.x"));
		assertTrue(!VersionUtil.isValidVersion("2.1.x"));
	}

}
