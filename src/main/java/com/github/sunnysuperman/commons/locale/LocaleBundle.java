package com.github.sunnysuperman.commons.locale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.sunnysuperman.commons.utils.FormatUtil;
import com.github.sunnysuperman.commons.utils.RegexUtil;
import com.github.sunnysuperman.commons.utils.StringUtil;
import com.github.sunnysuperman.commons.utils.RegexUtil.CompileHandler;

public abstract class LocaleBundle {

	public static class LocaleBundleOptions {
		private boolean strictMode;
		private String defaultLocale;
		private String[] prefLocales;
		private String logKey;
		private boolean escapeSpecialChars = true;

		public boolean isStrictMode() {
			return strictMode;
		}

		public void setStrictMode(boolean strictMode) {
			this.strictMode = strictMode;
		}

		public String getDefaultLocale() {
			return defaultLocale;
		}

		public void setDefaultLocale(String defaultLocale) {
			this.defaultLocale = defaultLocale;
		}

		public String[] getPrefLocales() {
			return prefLocales;
		}

		public void setPrefLocales(String[] prefLocales) {
			this.prefLocales = prefLocales;
		}

		public String getLogKey() {
			return logKey;
		}

		public void setLogKey(String logKey) {
			this.logKey = logKey;
		}

		public boolean isEscapeSpecialChars() {
			return escapeSpecialChars;
		}

		public void setEscapeSpecialChars(boolean escapeSpecialChars) {
			this.escapeSpecialChars = escapeSpecialChars;
		}

	}

	private static final Pattern P0 = Pattern.compile("\\$\\{([^\\}]+)\\}");
	private static final Pattern P1 = Pattern.compile("\\$\\{([a-zA-Z0-9_(),\\.\\[\\]]+)\\}");
	private static final Pattern P2 = Pattern.compile("\\{([a-zA-Z0-9_(),\\.\\[\\]]+)\\}");
	private final byte[] writeLock = new byte[0];
	private volatile boolean initialized = false;
	private Map<String, Map<String, String>> bundlesMap = new ConcurrentHashMap<String, Map<String, String>>(0);
	protected final LocaleBundleOptions options;

	public LocaleBundle(LocaleBundleOptions options) {
		this.options = options;
		if (options.getDefaultLocale() == null) {
			throw new RuntimeException(wrapLogMessage("No default locale set", options));
		}
	}

	protected String wrapLogMessage(String msg, LocaleBundleOptions options) {
		if (options.getLogKey() != null) {
			return options.getLogKey() + ": " + msg;
		}
		return msg;
	}

	protected void put(String key, String locale, String value) throws Exception {
		if (StringUtil.isEmpty(key)) {
			throw new IllegalArgumentException("Bad key");
		}
		if (StringUtil.isEmpty(locale)) {
			throw new IllegalArgumentException("Bad locale");
		}
		value = StringUtil.trimToNull(value);
		if (value == null) {
			throw new IllegalArgumentException("Bad value");
		}
		if (options.escapeSpecialChars) {
			value = StringUtil.escapeSpecialChars(value);
		}
		if (!isValidLocalizedValue(value)) {
			throw new RuntimeException(wrapLogMessage("Bad locale key " + key + " for locale: " + locale, options));
		}
		synchronized (writeLock) {
			Map<String, String> table = bundlesMap.get(key);
			if (table == null) {
				table = new ConcurrentHashMap<String, String>();
				bundlesMap.put(key, table);
			}
			table.put(locale, value);
		}
	}

	protected void finishPut() {
		Set<String> locales = null;
		for (Entry<String, Map<String, String>> bundleEntry : bundlesMap.entrySet()) {
			String key = bundleEntry.getKey();
			Map<String, String> table = bundleEntry.getValue();
			if (table.get(options.getDefaultLocale()) == null) {
				throw new RuntimeException(wrapLogMessage("No default value set for key: " + key, options));
			}
			if (options.strictMode) {
				if (locales == null) {
					locales = table.keySet();
				} else {
					Set<String> theLocales = table.keySet();
					if (theLocales.size() != locales.size() || !theLocales.containsAll(locales)) {
						throw new RuntimeException(wrapLogMessage("Missing some locales for key: " + key, options));
					}
				}
			}
		}
		initialized = true;
	}

	public boolean containsKey(String key) {
		return bundlesMap.containsKey(key);
	}

	public int size() {
		return bundlesMap.size();
	}

	public String getRaw(String locale, String key) {
		if (!initialized) {
			throw new RuntimeException("Does not finish init");
		}
		Map<String, String> table = bundlesMap.get(key);
		if (table == null) {
			return null;
		}
		locale = LocaleUtil.findSupportLocale(locale, table.keySet());
		if (locale != null) {
			return table.get(locale);
		}
		String[] preferencedLocales = options.getPrefLocales();
		if (preferencedLocales != null) {
			for (String prefLocale : preferencedLocales) {
				String value = table.get(prefLocale);
				if (value != null) {
					return value;
				}
			}
		}
		return table.get(options.getDefaultLocale());
	}

	public String toString() {
		return bundlesMap.toString();
	}

	private class LocaleCompileHandler implements CompileHandler {

		private String locale;

		public LocaleCompileHandler(String locale) {
			super();
			this.locale = locale;
		}

		private Number getNumber(String s, Map<String, Object> context) {
			return StringUtil.isNumeric(s.charAt(0)) ? FormatUtil.parseNumber(s) : FormatUtil.parseNumber(context
					.get(s));
		}

		@Override
		public String compile(String key, Map<String, Object> context) {
			Object value = context.get(key);
			if (value != null) {
				return value.toString();
			}
			if (key.indexOf("subtract(") == 0) {
				List<String> numbers = StringUtil.split(key.substring("subtract(".length(), key.indexOf(')')), ",");
				Number n1 = getNumber(numbers.get(0), context);
				Number n2 = getNumber(numbers.get(1), context);
				if (n1 instanceof Double || n1 instanceof Float || n2 instanceof Double || n2 instanceof Float) {
					return String.valueOf(n1.doubleValue() - n2.doubleValue());
				}
				return String.valueOf(n1.longValue() - n2.longValue());
			}
			if (key.indexOf("plus(") == 0) {
				List<String> numbers = StringUtil.split(key.substring("plus(".length(), key.indexOf(')')), ",");
				Number n1 = getNumber(numbers.get(0), context);
				Number n2 = getNumber(numbers.get(1), context);
				if (n1 instanceof Double || n1 instanceof Float || n2 instanceof Double || n2 instanceof Float) {
					return String.valueOf(n1.doubleValue() + n2.doubleValue());
				}
				return String.valueOf(n1.longValue() + n2.longValue());
			}
			int arrayIndex = key.indexOf("[");
			if (arrayIndex > 0) {
				String prefix = key.substring(0, arrayIndex);
				String pluralKey = key.substring(arrayIndex + 1, key.indexOf(']'));
				Number number = FormatUtil.parseNumber(context.get(pluralKey));
				if (number == null) {
					return null;
				}
				String d = number.toString();
				String s = getWithMapParams(locale, prefix + "[" + d + "]", context);
				if (s != null) {
					return s;
				}
				return getWithMapParams(locale, prefix + "[other]", context);
			}
			return null;
		}

	};

	public String getWithMapParams(String locale, String key, Map<String, Object> context) {
		String text = getRaw(locale, key);
		if (context == null) {
			return text;
		}
		return RegexUtil.compile(text, context, false, new LocaleCompileHandler(locale));
	}

	public String getWithArrayParams(String locale, String key, Object[] params) {
		String text = getRaw(locale, key);
		if (params == null) {
			return text;
		}
		Map<String, Object> context = new HashMap<String, Object>(params.length);
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			context.put(String.valueOf(i), param);
		}
		return RegexUtil.compile(text, context, false, new LocaleCompileHandler(locale));
	}

	public static boolean isValidLocalizedValue(String s) {
		if (s == null) {
			return false;
		}
		Pattern[] patterns = new Pattern[] { P0, P1, P2 };
		int[] counters = new int[patterns.length];
		for (int i = 0; i < patterns.length; i++) {
			int counter = 0;
			Matcher matcher = patterns[i].matcher(s);
			while (matcher.find()) {
				counter++;
			}
			counters[i] = counter;
		}
		if (counters[0] != counters[1] || counters[0] != counters[2]) {
			return false;
		}
		return true;
	}

}
