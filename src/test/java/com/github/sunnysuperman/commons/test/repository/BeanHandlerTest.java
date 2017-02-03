package com.github.sunnysuperman.commons.test.repository;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.repository.db.BeanHandler;

public class BeanHandlerTest extends TestCase {

	public static class Model1 {

	}

	public void test1() {
		BeanHandler<Model1> handler = new BeanHandler<Model1>() {
		};
		assertTrue(handler != null);
	}
}
