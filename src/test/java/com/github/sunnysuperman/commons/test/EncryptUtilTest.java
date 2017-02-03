package com.github.sunnysuperman.commons.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.github.sunnysuperman.commons.utils.EncryptUtil;

public class EncryptUtilTest extends TestCase {

	@Test
	public final void test() {
		try {
			System.out.println(EncryptUtil.md5("1234567"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

}
