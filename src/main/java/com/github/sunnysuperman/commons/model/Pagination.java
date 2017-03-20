package com.github.sunnysuperman.commons.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pagination<T> {
	public static final int DEFAULT_PAGESIZE = 10;

	public static <T> Pagination<T> getEmptyInstance() {
		return new Pagination<T>();
	}

	public static <T> Pagination<T> wrapSingle(T t, int limit) {
		List<T> list = new ArrayList<T>(1);
		list.add(t);
		return new Pagination<T>(list, 1, 0, limit);
	}

	public boolean empty() {
		return items == null || items.isEmpty();
	}

	private Collection<T> items;
	private int total;
	private int offset;
	private int limit = DEFAULT_PAGESIZE;

	public Pagination() {
		this(new ArrayList<T>(0), 0, 0, DEFAULT_PAGESIZE);
	}

	public Pagination(Collection<T> items, int total, int offset, int limit) {
		this.items = items;
		this.total = total;
		this.offset = offset;
		this.limit = limit <= 0 ? DEFAULT_PAGESIZE : limit;
	}

	public Collection<T> getItems() {
		return items;
	}

	public int getTotal() {
		return total;
	}

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

}
