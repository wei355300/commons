package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class PaginationHandler<T> implements Handler<List<?>> {
	protected int index;
	protected int count;
	protected RowHandler<T> rowHandler;

	// constructors
	public PaginationHandler() {
	}

	public PaginationHandler(int index, int count, RowHandler<T> rowHandler) {
		this.index = index;
		this.count = count;
		this.rowHandler = rowHandler;
	}

	// getters/setters
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public RowHandler<T> getRowHandler() {
		return rowHandler;
	}

	public void setRowHandler(RowHandler<T> rowHandler) {
		this.rowHandler = rowHandler;
	}

	@Override
	public List<?> handle(ResultSet rs) throws SQLException {
		rs.setFetchSize(count);
		List<T> list = new ArrayList<T>();
		while (rs.next()) {
			list.add(rowHandler.handleRow(rs));
		}
		return list;
	}

	public abstract String getDialect(String sql);

}
