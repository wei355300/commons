package com.github.sunnysuperman.commons.repository.db;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.github.sunnysuperman.commons.utils.BeanUtil;
import com.github.sunnysuperman.commons.utils.BeanUtil.ParseBeanOptions;

public abstract class BeanHandler<T> extends RowHandler<T> {

	private Class<T> clazz;
	private ParseBeanOptions options;

	@SuppressWarnings("unchecked")
	public BeanHandler(ParseBeanOptions options) {
		clazz = (Class<T>) ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0];
		this.options = options;
	}

	public BeanHandler() {
		this(null);
	}

	@Override
	public final T handleRow(ResultSet rs) throws SQLException {
		Map<String, Object> map = MapHandler.CAMELCASE.handleRow(rs);
		try {
			T bean = clazz.newInstance();
			beforeParseBean(bean, map);
			BeanUtil.map2bean(map, bean, options);
			afterParseBean(bean, map);
			return bean;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	protected void beforeParseBean(T bean, Map<String, Object> map) {

	}

	protected void afterParseBean(T bean, Map<String, Object> map) {

	}

}
