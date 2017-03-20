package com.github.sunnysuperman.commons.test.util;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.utils.IpUtil;

public class IpUtilTest extends TestCase {

	private void testIp(String ip) {
		int i = IpUtil.ip2int(ip);
		System.out.println(i);
		String ip2 = IpUtil.int2ip(i);
		assertTrue(ip2.equals(ip));
	}

	public void test() {
		testIp("0.0.0.0");
		testIp("8.8.8.8");
		testIp("127.255.255.255");
		testIp("128.0.0.0");
		testIp("128.168.1.1");
		testIp("192.168.1.1");
		testIp("255.255.255.255");
	}

}
