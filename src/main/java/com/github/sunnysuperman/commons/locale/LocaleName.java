package com.github.sunnysuperman.commons.locale;

public class LocaleName {
	private String key;
	private String name;

	public LocaleName() {
		super();
	}

	public LocaleName(String key, String name) {
		super();
		this.key = key;
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
