package com.github.sunnysuperman.commons.test.util;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.utils.ObjectId;

public class ObjectIdTest extends TestCase {

	public void test() throws Exception {
		AtomicInteger counter = new AtomicInteger(Integer.MAX_VALUE);
		while (true) {
			ObjectId id = new ObjectId(counter);
			System.out.println(id);
			Thread.sleep(1000);
		}
	}

}
