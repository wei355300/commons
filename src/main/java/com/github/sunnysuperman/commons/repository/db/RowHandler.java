package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 行映射器，抽象类，每次处理结果集的一行数据
 * 
 * 
 *
 * @param <T>
 */
public abstract class RowHandler<T> implements Handler<T> {

	@Override
	public T handle(ResultSet rs) throws SQLException {
		rs.setFetchSize(1);
		if (rs.next()) {
			return handleRow(rs);
		}
		return null;
	}

	/**
	 * 处理一行结果集
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public abstract T handleRow(ResultSet rs) throws SQLException;
}
