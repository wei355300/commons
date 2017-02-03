package com.github.sunnysuperman.commons.repository.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PaginationFragment<ItemIDType, ItemType> {
	protected Map<ItemIDType, ItemType> id_value_map;

	// public static final PaginationFragment EMPTY=new PaginationFragment(1);
	public PaginationFragment(int initialCapacity) {
		id_value_map = new HashMap<ItemIDType, ItemType>(initialCapacity);
	}

	public PaginationFragment() {
		this(10);
	}

	public PaginationFragment(Map<ItemIDType, ItemType> id_value_map) {
		this.id_value_map = id_value_map;
	}

	public void add(ItemIDType id, ItemType value) {
		id_value_map.put(id, value);
	}

	public ItemType get(ItemIDType id) {
		return id_value_map.get(id);
	}

	public Iterator<ItemType> iterator() {
		return id_value_map.values().iterator();
	}

	public int size() {
		return id_value_map.size();
	}

	public List<ItemType> toList() {
		List<ItemType> items = new ArrayList<ItemType>(id_value_map.size());
		for (Iterator<ItemType> it = id_value_map.values().iterator(); it.hasNext();) {
			items.add(it.next());
		}
		return items;
	}

	/*
	 * public PaginationSupport merge(PaginationFragment<ItemIDType,ItemType>
	 * anotherFragment,PaginationSolver<ItemIDType> solver){ List<ItemType>
	 * items=new ArrayList<ItemType>(); ItemIDType itemID=null; for(int
	 * i=0;i<solver.getShowNum();i++){ itemID=offset_id_map.get(index+i);
	 * if(itemID==null)break;
	 * items.add(i,(get(itemID)==null)?anotherFragment.get(itemID):get(itemID));
	 * } return new PaginationSupport(items,totalNum,index,showNum); }
	 */
}
