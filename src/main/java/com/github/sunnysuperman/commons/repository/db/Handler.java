package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库查询，映射转换器接口
 * 
 * 
 *
 * @param <T>
 */
public interface Handler<T> {
	T handle(ResultSet rs) throws SQLException;
}
