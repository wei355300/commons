package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MultiPropertyHandler extends RowHandler<Object[]> {
	@Override
	public Object[] handleRow(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		Object[] properties = new Object[columnCount];
		for (int i = 0; i < columnCount; i++) {
			properties[i] = rs.getObject(i + 1);
		}
		return properties;
	}

	private MultiPropertyHandler() {
	}

	private static final MultiPropertyHandler INSTANCE = new MultiPropertyHandler();

	public static MultiPropertyHandler getInstance() {
		return INSTANCE;
	}

}
