package com.github.sunnysuperman.commons.repository.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.sunnysuperman.commons.utils.Pagination;

public class PaginationSolver<ItemIDType, ItemType> {
	private int index = 0;
	private int showNum = 0;
	private int totalNum = 0;
	private Map<Integer, ItemIDType> offset_id_map;

	// public static final PaginationSolver<?,?> EMPTY=new
	// PaginationSolver<?,?>();

	// private PaginationSolver(){
	// offset_id_map=new HashMap<Integer,ItemIDType>(1);
	// }
	public PaginationSolver(List<ItemIDType> idList) {
		this(0, 10, idList.size(), idList);
	}

	public PaginationSolver(int index, int showNum, int totalNum, List<ItemIDType> idList) {
		this.index = index;
		this.showNum = showNum;
		this.totalNum = totalNum;
		offset_id_map = new HashMap<Integer, ItemIDType>(showNum);
		int actualNum = idList.size();
		for (int i = 0; i < actualNum; i++) {
			offset_id_map.put(index + i, idList.get(i));
		}
	}

	public Iterator<ItemIDType> idIterator() {
		return offset_id_map.values().iterator();
	}

	public int size() {
		return offset_id_map.values().size();
	}

	public Pagination<ItemType> merge(PaginationFragment<ItemIDType, ItemType> one,
			PaginationFragment<ItemIDType, ItemType> another) {
		List<ItemType> items = new ArrayList<ItemType>();
		ItemIDType itemID = null;
		ItemType item = null;
		for (int i = 0; i < showNum; i++) {
			itemID = offset_id_map.get(index + i);
			if (itemID == null)
				break;
			item = one.get(itemID);
			if (item == null)
				item = another.get(itemID);
			items.add(i, item);
		}
		return new Pagination<ItemType>(items, totalNum, index, showNum);
	}

	public boolean isEmpty() {
		return totalNum == 0 || offset_id_map.size() == 0;
	}

}
