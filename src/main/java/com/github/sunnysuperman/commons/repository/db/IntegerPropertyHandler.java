package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerPropertyHandler extends RowHandler<Integer> {
	@Override
	public Integer handleRow(ResultSet rs) throws SQLException {
		return rs.getInt(1);
	}

	private IntegerPropertyHandler() {
	}

	private static final IntegerPropertyHandler INSTANCE = new IntegerPropertyHandler();

	public static IntegerPropertyHandler getInstance() {
		return INSTANCE;
	}
}
