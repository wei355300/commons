package com.github.sunnysuperman.commons.bean;

import java.util.LinkedList;

/**
 * 解析bean的配置项
 *
 */
public class ParseBeanOptions {
	private ParseBeanInterceptor interceptor;
	protected LinkedList<String> contextKeys;

	public ParseBeanInterceptor getInterceptor() {
		return interceptor;
	}

	public ParseBeanOptions setInterceptor(ParseBeanInterceptor interceptor) {
		this.interceptor = interceptor;
		contextKeys = new LinkedList<String>();
		return this;
	}
}
