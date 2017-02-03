package com.github.sunnysuperman.commons.test.repository;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.repository.db.CustomFunction;
import com.github.sunnysuperman.commons.repository.db.JdbcTemplate;
import com.github.sunnysuperman.commons.repository.db.LongPropertyHandler;
import com.github.sunnysuperman.commons.test.BaseTest.TestDB;
import com.github.sunnysuperman.commons.utils.CollectionUtil;

public class JdbcTemplateTest extends TestCase {

	public void test1() throws Exception {
		JdbcTemplate template = TestDB.getJdbcTemplate();

		// template.insert("testupdate2", CollectionUtil.arrayAsMap("id1", "a",
		// "id2", "b"));

		// Map<String, Object> doc = CollectionUtil.arrayAsMap("$inc",
		// CollectionUtil.arrayAsMap("n1", 0, "n2", 3));
		Map<String, Object> doc = CollectionUtil.arrayAsMap("s1", "haha2");
		template.update("testupdate2", doc, "id1, id2", new Object[] { "a", "b" });
	}

	public void test2() throws Exception {
		JdbcTemplate template = TestDB.getJdbcTemplate();
		Long deviceIdAsNumber = template.insert("device1_id", CollectionUtil.arrayAsMap("id", null),
				LongPropertyHandler.getInstance());
		assertTrue(deviceIdAsNumber != null);
		System.out.println(deviceIdAsNumber);
	}

	public void test_insert() throws Exception {
		JdbcTemplate template = TestDB.getJdbcTemplate();
		Map<String, Object> doc = new HashMap<String, Object>();
		doc.put("id", 2);
		doc.put("ip", 0);
		// doc.put("loc", new CustomFunction("ST_GeometryFromText(?,4326)", new
		// Object[]{"Point(116.318319 39.99878)"}));
		doc.put("loc", null);
		template.insert("loc_test", doc);
	}

	public void test_update() throws Exception {
		JdbcTemplate template = TestDB.getJdbcTemplate();
		Map<String, Object> doc = new HashMap<String, Object>();
		doc.put("ip", 1);
		doc.put("loc", new CustomFunction("ST_GeometryFromText(?,4326)", new Object[] { "Point(117.318319 39.99878)" }));
		template.update("loc_test", doc, "id", 1);
	}

}
