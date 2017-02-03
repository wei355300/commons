package com.github.sunnysuperman.commons.utils;

import java.util.UUID;

public class UUIDUtil {

	public static String genUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}

	public static String genShortUUID() {
		return new ObjectId().toHexString();
	}

}
