package com.github.sunnysuperman.commons.test.config;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.config.Config;
import com.github.sunnysuperman.commons.config.DBConfig;
import com.github.sunnysuperman.commons.config.DBConfig.DBConfigOptions;
import com.github.sunnysuperman.commons.model.TimeSerializeType;
import com.github.sunnysuperman.commons.test.BaseTest.TestDB;
import com.github.sunnysuperman.commons.utils.CollectionUtil;

public class DBConfigTest extends TestCase {

	private DBConfig getConfig(boolean read) {
		DBConfigOptions options = new DBConfigOptions();
		options.setJdbcTemplate(TestDB.getJdbcTemplate());
		options.setTableName("api_config");
		options.setLoadOnInit(read);
		options.setReloadable(read);
		options.setReloadSeconds(2);
		options.setUpdatedAtSerializeType(TimeSerializeType.Long);
		return new DBConfig(options);
	}

	public void test_get() throws Exception {
		DBConfig config = getConfig(true);
		System.out.println(config.getInt("config.reload.seconds"));
		System.out.println(config.getString("file.unzip.tmpdir"));
		System.out.println(config.getBoolean("requestlog.enabled"));
		System.out.println(config.getJSONObject("cache"));
	}

	public void test_get2() throws Exception {
		DBConfig config = getConfig(true);
		while (true) {
			System.out.println(config.getInt("a"));
			Thread.sleep(3000);
		}
	}

	public void test_save2() throws Exception {
		DBConfig config = getConfig(false);
		int i = 100;
		while (true) {
			config.save("a", Config.TYPE_INT, i++);
			Thread.sleep(5000);
		}
	}

	public void test_save() throws Exception {
		DBConfig config = getConfig(false);
		config.save("a", Config.TYPE_STRING, "it's string");
		config.save("b", Config.TYPE_INT, 100);
		config.save("c", Config.TYPE_BOOLEAN, true);
		config.save("d", Config.TYPE_LONG, 4567890);
		config.save("e", Config.TYPE_DOUBLE, 999.888);
		config.save("f", Config.TYPE_DATE, "1969-12-31T23:59:00.000Z");
		config.save("g", Config.TYPE_BLOB, new byte[] { 1, 2, 3, 4, 5, 6 });
		config.save("h", Config.TYPE_JSONOBJECT, CollectionUtil.arrayAsMap("p1", "1", "p2", 2));
	}

	public void test_purge() throws Exception {
		DBConfig config = getConfig(false);
		config.purge("h");
	}
}
