package com.github.sunnysuperman.commons.locale;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sunnysuperman.commons.Resources;
import com.github.sunnysuperman.commons.utils.FileUtil;
import com.github.sunnysuperman.commons.utils.FileUtil.ReadLineHandler;

public class Locales {
	private static final Logger LOGGER = LoggerFactory.getLogger(Locales.class);
	private static List<LocaleName> LIST = null;
	private static Map<String, LocaleName> MAP = null;
	static {
		LIST = new LinkedList<LocaleName>();
		try {
			FileUtil.read(Resources.getResourceAsStream("locales.txt"), null, new ReadLineHandler() {

				@Override
				public boolean handle(String s, int line) throws Exception {
					s = s.trim();
					if (s.isEmpty() || s.startsWith("#")) {
						return true;
					}
					int offset = s.indexOf('=');
					String key = s.substring(0, offset).trim();
					String value = s.substring(offset + 1).trim();
					LIST.add(new LocaleName(key, value));
					return true;
				}
			});
		} catch (Exception e) {
			LOGGER.error("Failed to load locales", e);
			System.exit(-1);
		}
		MAP = new HashMap<String, LocaleName>(LIST.size());
		for (LocaleName locale : LIST) {
			if (MAP.containsKey(locale.getKey())) {
				LOGGER.error("Duplicate locale: " + locale.getKey());
				System.exit(-1);
			}
			MAP.put(locale.getKey(), locale);
		}
	}

	public static List<LocaleName> getAll() {
		return LIST;
	}

	public static LocaleName find(String key) {
		return MAP.get(key);
	}
}
