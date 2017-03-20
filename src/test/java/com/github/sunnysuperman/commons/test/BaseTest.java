package com.github.sunnysuperman.commons.test;

import java.io.InputStream;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sunnysuperman.commons.config.PropertiesConfig;
import com.github.sunnysuperman.commons.repository.db.JdbcTemplate;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class BaseTest extends TestCase {
	private static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

	public static class TestDB {
		private static final byte[] LOCK = new byte[0];
		private static ComboPooledDataSource DS = null;
		private static JdbcTemplate jdbcTemplate;

		static {
			try {
				PropertiesConfig config = new PropertiesConfig(
						BaseTest.class.getResourceAsStream("resources/db.properties"));
				ComboPooledDataSource ds = new com.mchange.v2.c3p0.ComboPooledDataSource();
				ds.setDriverClass(config.getString("db.driver", "com.mysql.jdbc.Driver"));
				ds.setJdbcUrl(config.getString("db.url"));
				ds.setUser(config.getString("db.username"));
				ds.setPassword(config.getString("db.password"));
				ds.setMinPoolSize(0);
				ds.setMaxPoolSize(1);
				DS = ds;
				jdbcTemplate = new JdbcTemplate(DS, config.getString("db.type", "mysql"));
			} catch (Exception ex) {
				LOG.error("Failed to start ConfigDB", ex);
			}
		}

		// public static DataSource get() {
		// return DS;
		// }

		public static JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public static void close() {
			synchronized (LOCK) {
				if (DS != null) {
					DS.close();
					DS = null;
				}
			}
		}

	}

	protected InputStream getResourceAsStream(String fileName) {
		return BaseTest.class.getResourceAsStream("resources/" + fileName);
	}
}
