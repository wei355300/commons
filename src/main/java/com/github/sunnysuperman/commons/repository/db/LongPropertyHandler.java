package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongPropertyHandler extends RowHandler<Long> {
	@Override
	public Long handleRow(ResultSet rs) throws SQLException {
		return rs.getLong(1);
	}

	private LongPropertyHandler() {
	}

	private static final LongPropertyHandler INSTANCE = new LongPropertyHandler();

	public static LongPropertyHandler getInstance() {
		return INSTANCE;
	}
}
