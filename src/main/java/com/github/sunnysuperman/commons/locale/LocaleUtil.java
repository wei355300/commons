package com.github.sunnysuperman.commons.locale;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LocaleUtil {
	private static final char LOCALE_SEPERATOR_CHAR = '_';
	private static final char APPLE_LOCALE_SEPERATOR_CHAR = '-';
	private static final Map<String, String> APPLE_LOCALE_MAPPING = new HashMap<String, String>();
	static {
		APPLE_LOCALE_MAPPING.put("zh-Hans", "zh_CN");
		APPLE_LOCALE_MAPPING.put("zh-Hant", "zh_TW");
		APPLE_LOCALE_MAPPING.put("zh-Hans-CN", "zh_CN");
		APPLE_LOCALE_MAPPING.put("zh-Hant-CN", "zh_TW");
	}

	public static String formatLocale(String locale) {
		if (locale == null || locale.length() == 0) {
			return null;
		}
		if (locale.indexOf(APPLE_LOCALE_SEPERATOR_CHAR) > 0) {
			String parsedLocale = APPLE_LOCALE_MAPPING.get(locale);
			if (parsedLocale != null) {
				return parsedLocale;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < locale.length(); i++) {
				char c = locale.charAt(i);
				if (c == APPLE_LOCALE_SEPERATOR_CHAR) {
					sb.append(LOCALE_SEPERATOR_CHAR);
				} else {
					sb.append(c);
				}
			}
			locale = sb.toString();
		}
		int langOffset = locale.lastIndexOf(LOCALE_SEPERATOR_CHAR);
		if (langOffset <= 0) {
			return locale;
		}
		if (langOffset == locale.length() - 1) {
			return locale.substring(0, langOffset);
		}
		return locale.substring(0, langOffset) + locale.substring(langOffset).toUpperCase();
	}

	public static String getParentLocale(String locale) {
		int index = locale.indexOf(LOCALE_SEPERATOR_CHAR);
		if (index > 0 && index < locale.length() - 1) {
			return locale.substring(0, index);
		}
		return null;
	}

	public static String findSupportLocale(String locale, Collection<String> supportedLocales) {
		if (locale == null) {
			return null;
		}
		if (supportedLocales.contains(locale)) {
			return locale;
		}
		String parent = getParentLocale(locale);
		if (parent == null) {
			parent = locale;
		}
		for (String other : supportedLocales) {
			String otherParent = getParentLocale(other);
			if (otherParent == null) {
				otherParent = other;
			}
			if (otherParent.equals(parent)) {
				return other;
			}
		}
		return null;
	}

}
