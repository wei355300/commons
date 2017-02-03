package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class SetHandler<T> implements Handler<Set<T>> {
	protected RowHandler<T> rowHandler;
	protected int fetchSize = 200;

	public SetHandler(RowHandler<T> rowHandler) {
		this.rowHandler = rowHandler;
	}

	public SetHandler(RowHandler<T> rowHandler, int fetchSize) {
		this.rowHandler = rowHandler;
		if (fetchSize > 0) {
			this.fetchSize = fetchSize;
		}
	}

	public RowHandler<T> getRowHandler() {
		return rowHandler;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	@Override
	public Set<T> handle(ResultSet rs) throws SQLException {
		rs.setFetchSize(fetchSize);
		Set<T> set = new HashSet<T>();
		while (rs.next()) {
			set.add(rowHandler.handleRow(rs));
		}
		return set;
	}

}
