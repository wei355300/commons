package com.github.sunnysuperman.commons.repository.db;

import javax.sql.DataSource;

import com.github.sunnysuperman.commons.repository.RepositoryException;

public class ReadOnlyJdbcTemplate extends JdbcTemplate {

	public ReadOnlyJdbcTemplate() {
		super();
	}

	public ReadOnlyJdbcTemplate(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public int update(String sql, Object[] params) throws RepositoryException {
		throw new RepositoryException("Readonly database");
	}

}
