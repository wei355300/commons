package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListHandler<T> implements Handler<List<T>> {
	protected RowHandler<T> rowHandler;
	protected int fetchSize = 200;

	public ListHandler(RowHandler<T> rowHandler) {
		this.rowHandler = rowHandler;
	}

	public ListHandler(RowHandler<T> rowHandler, int fetchSize) {
		this.rowHandler = rowHandler;
		if (fetchSize > 0) {
			this.fetchSize = fetchSize;
		}
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public RowHandler<T> getRowHandler() {
		return rowHandler;
	}

	@Override
	public List<T> handle(ResultSet rs) throws SQLException {
		rs.setFetchSize(fetchSize);
		List<T> list = new ArrayList<T>();
		while (rs.next()) {
			list.add(rowHandler.handleRow(rs));
		}
		return list;
	}

	// TODO XXX
	public static <T> ListHandler<T> newInstance(RowHandler<T> t) {
		return new ListHandler<T>(t);
	}

}
