package com.github.sunnysuperman.commons.repository.db;


public class CustomFunction {
	private String function;
	private Object[] params;

	public CustomFunction(String function, Object[] params) {
		super();
		this.function = function;
		this.params = params;
	}

	public String getFunction() {
		return function;
	}

	public Object[] getParams() {
		return params;
	}

}
