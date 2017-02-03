package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.github.sunnysuperman.commons.utils.StringUtil;

public class MapHandler extends RowHandler<Map<String, Object>> {

	private static interface MapKeyHandler {
		String makeKey(String key);
	}

	private static final MapKeyHandler KEY_HANDLER_CASESENSITIVE = new MapKeyHandler() {

		@Override
		public String makeKey(String key) {
			return key;
		}

	};

	private static final MapKeyHandler KEY_HANDLER_LOWERCASE = new MapKeyHandler() {

		@Override
		public String makeKey(String key) {
			return key.toLowerCase();
		}

	};

	private static final MapKeyHandler KEY_HANDLER_UPPERCASE = new MapKeyHandler() {

		@Override
		public String makeKey(String key) {
			return key.toUpperCase();
		}

	};

	private static final MapKeyHandler KEY_HANDLER_CAMELCASE = new MapKeyHandler() {

		@Override
		public String makeKey(String key) {
			return StringUtil.underline2camel(key);
		}

	};

	private MapKeyHandler keyHandler;

	private MapHandler(MapKeyHandler keyHandler) {
		super();
		this.keyHandler = keyHandler;
	}

	@Override
	public Map<String, Object> handleRow(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		Map<String, Object> dataMap = new HashMap<String, Object>(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			String key = meta.getColumnLabel(i);
			key = keyHandler.makeKey(key);
			Object value = rs.getObject(i);
			dataMap.put(key, value);
		}
		return dataMap;
	}

	public static final MapHandler CASESENSITIVE = new MapHandler(KEY_HANDLER_CASESENSITIVE);
	public static final MapHandler LOWERCASE = new MapHandler(KEY_HANDLER_LOWERCASE);
	public static final MapHandler UPPERCASE = new MapHandler(KEY_HANDLER_UPPERCASE);
	public static final MapHandler CAMELCASE = new MapHandler(KEY_HANDLER_CAMELCASE);
}