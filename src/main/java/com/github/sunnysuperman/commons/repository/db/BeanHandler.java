package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.github.sunnysuperman.commons.bean.Bean;
import com.github.sunnysuperman.commons.bean.ParseBeanOptions;

public class BeanHandler<T> extends RowHandler<T> {
	private Class<T> clazz;
	private ParseBeanOptions parseBeanOptions;

	public BeanHandler(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	public BeanHandler(Class<T> clazz, ParseBeanOptions parseBeanOptions) {
		this.clazz = clazz;
		this.parseBeanOptions = parseBeanOptions;
	}

	@Override
	public final T handleRow(ResultSet rs) throws SQLException {
		Map<String, Object> map = MapHandler.CAMELCASE.handleRow(rs);
		T bean = newInstance(map);
		beforeParseBean(bean, map);
		Bean.fromMap(map, bean, parseBeanOptions);
		afterParseBean(bean, map);
		return bean;
	}

	protected T newInstance(Map<String, Object> map) throws SQLException {
		T bean;
		try {
			bean = clazz.newInstance();
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return bean;
	}

	protected void beforeParseBean(T bean, Map<String, Object> map) {

	}

	protected void afterParseBean(T bean, Map<String, Object> map) {

	}

}
