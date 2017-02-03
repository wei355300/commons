package com.github.sunnysuperman.commons.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

public final class JSONUtil {
	private static Map<Type, ObjectSerializer> ADDITIONAL_SERIALIZERS = new HashMap<Type, ObjectSerializer>();
	private static SerializeConfig SERIALIZE_CONFIG = new SerializeConfig();
	private static final ISO8601DateWithMillsSerializer ISO8601DATEWITHMILLS_SERIALIZER = new ISO8601DateWithMillsSerializer();

	static {
		ADDITIONAL_SERIALIZERS.put(java.util.Date.class, ISO8601DATEWITHMILLS_SERIALIZER);
		ADDITIONAL_SERIALIZERS.put(java.sql.Timestamp.class, ISO8601DATEWITHMILLS_SERIALIZER);
		ADDITIONAL_SERIALIZERS.put(java.sql.Date.class, ISO8601DATEWITHMILLS_SERIALIZER);
		for (Entry<Type, ObjectSerializer> entry : ADDITIONAL_SERIALIZERS.entrySet()) {
			SERIALIZE_CONFIG.put(entry.getKey(), entry.getValue());
		}
	}

	public static Map<Type, ObjectSerializer> getDefaultSerializers() {
		return ADDITIONAL_SERIALIZERS;
	}

	public static SerializeConfig getDefaultSerializeConfig() {
		return SERIALIZE_CONFIG;
	}

	public static class ISO8601DateWithMillsSerializer implements ObjectSerializer {
		@Override
		public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType)
				throws IOException {
			if (object == null) {
				serializer.getWriter().writeNull();
			} else {
				serializer.write(FormatUtil.formatISO8601Date((Date) object));
			}
		}
	}

	public static String toJSONString(Object object) {
		return toJSONString(object, null);
	}

	public static String toJSONString(Object object, Map<Type, ObjectSerializer> serializers) {
		return toJSONString(object, serializers, SerializerFeature.DisableCircularReferenceDetect,
				SerializerFeature.BrowserCompatible);
	}

	public static String toJSONString(Object object, Map<Type, ObjectSerializer> serializers,
			SerializerFeature... features) {
		if (object == null) {
			return null;
		}
		SerializeConfig sc = null;
		if (serializers == null) {
			sc = SERIALIZE_CONFIG;
		} else {
			sc = new SerializeConfig();
			for (Entry<Type, ObjectSerializer> entry : ADDITIONAL_SERIALIZERS.entrySet()) {
				sc.put(entry.getKey(), entry.getValue());
			}
			for (Entry<Type, ObjectSerializer> entry : serializers.entrySet()) {
				sc.put(entry.getKey(), entry.getValue());
			}
		}
		return JSON.toJSONString(object, sc, features);
	}

	public static Object parse(String s) {
		if (StringUtil.isEmpty(s)) {
			return null;
		}
		return JSON.parse(s);
	}

	public static Map<String, Object> parseJSONObject(String s) {
		Object o = parse(s);
		return (JSONObject) o;
	}

	public static List<?> parseJSONArray(String s) {
		Object o = parse(s);
		return (JSONArray) o;
	}

	/**
	 * 是否为json string
	 * 
	 * @param s
	 *            string字符串
	 * @return 若是，返回true；否则，返回false
	 */
	public static boolean isJSONString(String s) {
		if (s == null) {
			return false;
		}
		char endChar = '0';
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isWhitespace(c)) {
				continue;
			}
			if (c == '{') {
				endChar = '}';
				break;
			}
			if (c == '[') {
				endChar = ']';
				break;
			}
			return false;
		}
		if (endChar == '0') {
			return false;
		}
		for (int i = s.length() - 1; i >= 0; i--) {
			char c = s.charAt(i);
			if (Character.isWhitespace(c)) {
				continue;
			}
			return c == endChar;
		}
		return false;
	}
}
