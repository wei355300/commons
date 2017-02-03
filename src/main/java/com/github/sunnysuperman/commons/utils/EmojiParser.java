package com.github.sunnysuperman.commons.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.github.sunnysuperman.commons.Resources;
import com.github.sunnysuperman.commons.utils.FileUtil.ReadLineHandler;

public abstract class EmojiParser {
	private static Map<String, String> TABLE = new HashMap<String, String>();
	static {
		try {
			FileUtil.read(Resources.getResourceAsStream("emoji.txt"), null, new ReadLineHandler() {

				public boolean handle(String s, int arg1) throws Exception {
					s = s.trim();
					if (s.isEmpty()) {
						return true;
					}
					int offset = s.indexOf(',');
					String key = s.substring(0, offset);
					String name = s.substring(offset + 1);
					TABLE.put(key, name);
					return true;
				}

			});
		} catch (Exception ex) {
			LoggerFactory.getLogger(EmojiParser.class).error("Failed to init EmojiParser", ex);
		}
	}

	private static int[] toCodePointArray(String str) {
		char[] ach = str.toCharArray();
		int len = ach.length;
		int[] acp = new int[Character.codePointCount(ach, 0, len)];
		int j = 0;

		for (int i = 0, cp; i < len; i += Character.charCount(cp)) {
			cp = Character.codePointAt(ach, i);
			acp[j++] = cp;
		}
		return acp;
	}

	public String parse(String input) {
		StringBuilder result = new StringBuilder();
		int[] codePoints = toCodePointArray(input);
		for (int i = 0; i < codePoints.length; i++) {
			// List<Integer> key2 = null;
			String key1 = Integer.toHexString(codePoints[i]);
			if (i + 1 < codePoints.length) {
				// key2 = new ArrayList<Integer>();
				// key2.add(codePoints[i]);
				// key2.add(codePoints[i + 1]);
				String key = key1 + "-" + Integer.toHexString(codePoints[i + 1]);
				if (TABLE.containsKey(key)) {
					result.append(doParse(key, TABLE.get(key)));
					i++;
					continue;
				}
			}

			// List<Integer> key1 = new ArrayList<Integer>();
			// key1.add(codePoints[i]);
			if (TABLE.containsKey(key1)) {
				result.append(doParse(key1, TABLE.get(key1)));
				continue;
			}

			result.append(Character.toChars(codePoints[i]));

		}
		return result.toString();
	}

	protected abstract String doParse(String unicode, String title);

}
