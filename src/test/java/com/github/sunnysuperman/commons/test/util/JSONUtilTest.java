package com.github.sunnysuperman.commons.test.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.sunnysuperman.commons.utils.JSONUtil;

public class JSONUtilTest {

	@Test
	public final void testBadString() {
		String s = "Афганская Борзая ha 我 草ྉ原ྉ上ྉ的ྉ三ྉ只ྉ帅ྉ锅ྉ！！";
		Map<String, Object> payload = new HashMap<String, Object>();
		payload.put("content", s);
		String jsonString = JSONUtil.toJSONString(payload);
		System.out.println(jsonString);
	}
}
