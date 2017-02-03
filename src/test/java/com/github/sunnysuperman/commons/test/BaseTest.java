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
				PropertiesConfig config = new PropertiesConfig(BaseTest.class.getResourceAsStream("conf/db.properties"));
				ComboPooledDataSource ds = new com.mchange.v2.c3p0.ComboPooledDataSource();
				ds.setDriverClass(config.getString("db.driver"));
				ds.setJdbcUrl(config.getString("db.url"));
				ds.setUser(config.getString("db.username"));
				ds.setPassword(config.getString("db.password"));
				ds.setMinPoolSize(0);
				ds.setMaxPoolSize(1);
				DS = ds;
				jdbcTemplate = new JdbcTemplate(DS, config.getString("db.type"));
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
		return BaseTest.class.getResourceAsStream("conf/" + fileName);
	}
}
