package com.github.sunnysuperman.commons.test.task;

import junit.framework.TestCase;

import org.junit.Test;

import com.github.sunnysuperman.commons.task.CompleteAwareTaskEngine;
import com.github.sunnysuperman.commons.task.TaskEngine;

public class TaskEngineTest extends TestCase {

	private final byte[] LOCK = new byte[0];

	private static class MyTask implements Runnable {

		private String key;

		public MyTask(String key) {
			super();
			this.key = key;
		}

		@Override
		public void run() {
			System.out.println("Running: " + key);
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Complete: " + key);
		}

	}

	@Test
	public final void test_complete_aware() {
		CompleteAwareTaskEngine engine = new CompleteAwareTaskEngine("default", 4);
		engine.setLogEnabled(true);
		for (int i = 1; i <= 10; i++) {
			engine.addTask(new MyTask(String.valueOf(i)));
			engine.waitUntilTaskNumLessThan(100);
		}
		engine.waitUntilDoneAndExit();
	}

	@Test
	public final void test_normal() {
		TaskEngine engine = new TaskEngine("default", 4);
		engine.setLogEnabled(true);
		for (int i = 1; i <= 10; i++) {
			engine.addTask(new MyTask(String.valueOf(i)));
		}
		synchronized (LOCK) {
			try {
				LOCK.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
