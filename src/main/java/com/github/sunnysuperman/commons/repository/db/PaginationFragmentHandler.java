package com.github.sunnysuperman.commons.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class PaginationFragmentHandler<ItemIDType, ItemType> implements
		Handler<PaginationFragment<ItemIDType, ItemType>> {

	@Override
	public PaginationFragment<ItemIDType, ItemType> handle(ResultSet rs) throws SQLException {
		return getFragment(rs);
	}

	protected abstract PaginationFragment<ItemIDType, ItemType> getFragment(ResultSet rs) throws SQLException;

}
