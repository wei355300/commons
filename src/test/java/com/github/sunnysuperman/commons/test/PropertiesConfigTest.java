package com.github.sunnysuperman.commons.test;

import java.util.Properties;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.config.PropertiesConfig;

public class PropertiesConfigTest extends TestCase {

	public final void testParse() throws Exception {
		PropertiesConfig config = new PropertiesConfig(
				PropertiesConfigTest.class.getResourceAsStream("conf/proptest.ini"), null);
		System.out.println(config.getString("doctor.worktime"));
		System.out.println(config.getString("test.0"));
	}

	public final void testParse2() throws Exception {
		PropertiesConfig config = new PropertiesConfig(
				PropertiesConfigTest.class.getResourceAsStream("conf/proptest.properties"));
		System.out.println(config.getString("test.0"));
		System.out.println(config.getString("test.1"));
		System.out.println(config.getString("test.6"));
	}

	public final void testParse3() throws Exception {
		Properties props = new Properties();
		props.load(PropertiesConfigTest.class.getResourceAsStream("conf/proptest.properties"));
		System.out.println(props.get("test.0"));
		System.out.println(props.get("test.1"));
		System.out.println(props.get("test.6"));
	}

	public final void test_all() throws Exception {
		PropertiesConfig config = new PropertiesConfig(
				PropertiesConfigTest.class.getResourceAsStream("conf/proptest3.ini"));
		System.out.println(config.getString("a"));
		System.out.println(config.getInt("b"));
		System.out.println(config.getBoolean("c"));
		System.out.println(config.getLong("d"));
		System.out.println(config.getDouble("e"));
		System.out.println(config.getDate("f"));
		System.out.println(config.getJSONObject("h"));
	}

}
