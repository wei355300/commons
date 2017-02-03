package com.github.sunnysuperman.commons.utils;

import java.util.List;

public class IpUtil {
	private static final int TOKEN_SIZE = 4;

	public static int ip2int(String addr) {
		List<String> tokens = StringUtil.split(addr, ".");
		if (tokens == null || tokens.size() != TOKEN_SIZE) {
			return 0;
		}
		byte[] bytes = new byte[TOKEN_SIZE];
		for (int i = 0; i < TOKEN_SIZE; i++) {
			bytes[i] = (byte) (Integer.parseInt(tokens.get(i)) & 0xFF);
		}
		return ByteUtil.bytes2int(bytes);
	}

	public static String int2ip(int intValue) {
		byte[] bytes = ByteUtil.int2bytes(intValue);
		StringBuilder buf = new StringBuilder(15);
		for (int i = 0; i < TOKEN_SIZE; i++) {
			if (i > 0) {
				buf.append('.');
			}
			buf.append(bytes[i] & 0xFF);
		}
		return buf.toString();
	}
}
