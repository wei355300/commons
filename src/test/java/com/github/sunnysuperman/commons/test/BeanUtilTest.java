package com.github.sunnysuperman.commons.test;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.sunnysuperman.commons.utils.BeanUtil;
import com.github.sunnysuperman.commons.utils.CollectionUtil;
import com.github.sunnysuperman.commons.utils.FileUtil;
import com.github.sunnysuperman.commons.utils.FormatUtil;
import com.github.sunnysuperman.commons.utils.JSONUtil;
import com.github.sunnysuperman.commons.utils.StringUtil;
import com.github.sunnysuperman.commons.utils.BeanUtil.ParseBeanInterceptor;
import com.github.sunnysuperman.commons.utils.BeanUtil.ParseBeanOptions;
import com.github.sunnysuperman.commons.utils.BeanUtil.ParseBeanResult;

public class BeanUtilTest extends BaseTest {

	public static class OSSFile {
		private String url;
		private int size;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}
	}

	public static class Firmware1 implements Serializable {
		private Map<String, Integer> extra;

		public Map<String, Integer> getExtra() {
			return extra;
		}

		public void setExtra(Map<String, Integer> extra) {
			this.extra = extra;
		}

	}

	public static class Firmware2 extends Firmware1 {
		private String id;
		private short version;
		private OSSFile file;
		private String note;
		private byte deviceFilter;
		private Integer[] arr;
		private List<?> children;
		private List<OSSFile> files;

		public Integer[] getArr() {
			return arr;
		}

		public void setArr(Integer[] arr) {
			this.arr = arr;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public short getVersion() {
			return version;
		}

		public void setVersion(short version) {
			this.version = version;
		}

		public OSSFile getFile() {
			return file;
		}

		public void setFile(OSSFile file) {
			this.file = file;
		}

		public String getNote() {
			return note;
		}

		public void setNote(String note) {
			this.note = note;
		}

		public byte getDeviceFilter() {
			return deviceFilter;
		}

		public void setDeviceFilter(byte deviceFilter) {
			this.deviceFilter = deviceFilter;
		}

		public List<?> getChildren() {
			return children;
		}

		public void setChildren(List<?> children) {
			this.children = children;
		}

		public List<OSSFile> getFiles() {
			return files;
		}

		public void setFiles(List<OSSFile> files) {
			this.files = files;
		}

	}

	public static class DateAware {
		private Date f1;
		private int f2;

		public Date getF1() {
			return f1;
		}

		public void setF1(Date f1) {
			this.f1 = f1;
		}

		public int getF2() {
			return f2;
		}

		public void setF2(int f2) {
			this.f2 = f2;
		}

	}

	@Test
	public void testParseValue() throws Exception {
		String s = FileUtil.read(getResourceAsStream("bean.json"));
		Firmware2 firmware = BeanUtil.jsonString2bean(s, new Firmware2());
		System.out.println(firmware.getChildren());
		System.out.println(JSONUtil.toJSONString(firmware.getChildren()));
		System.out.println(StringUtil.join(firmware.getArr(), ","));
		System.out.println(firmware.getExtra());
	}

	@Test
	public void testIsJSONString() throws Exception {
		assertTrue(JSONUtil.isJSONString("{}"));
		assertTrue(JSONUtil.isJSONString("[]"));
		assertTrue(JSONUtil.isJSONString("[]	"));
		assertTrue(JSONUtil.isJSONString("[]\n"));
		assertTrue(JSONUtil.isJSONString("  []\n"));

		assertFalse(JSONUtil.isJSONString(""));
		assertFalse(JSONUtil.isJSONString("{]"));
		assertFalse(JSONUtil.isJSONString("[}"));
		assertFalse(JSONUtil.isJSONString("s[]"));
		assertFalse(JSONUtil.isJSONString("[]="));
	}

	@Test
	public void test_map2bean() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 1);
		map.put("file", CollectionUtil.arrayAsMap("url", "http://xx", "size", 50));
		map.put("note", "note haha");
		map.put("children", Arrays.asList("a", "b", "c"));

		Map<String, Object> childFile1 = CollectionUtil.arrayAsMap("url", "http://file1", "size", 60);
		Map<String, Object> childFile2 = CollectionUtil.arrayAsMap("url", "http://file2", "size", 70);
		map.put("files", Arrays.asList(childFile1, childFile2));
		ParseBeanInterceptor interceptor = new ParseBeanInterceptor() {

			@Override
			public ParseBeanResult parse(Object value, Class<?> destClass, ParameterizedType pType,
					LinkedList<String> keys) {
				System.out.println(value.toString() + " in " + StringUtil.join(keys, ","));
				return null;
			}

		};
		BeanUtil.map2bean(map, new Firmware2(), new ParseBeanOptions().setInterceptor(interceptor));
	}

	@Test
	public void testParseDate() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// map.put("f1", String.valueOf("0" + System.currentTimeMillis()));
		map.put("f1", FormatUtil.formatISO8601Date(new Date()));
		DateAware bean = BeanUtil.map2bean(map, new DateAware());
		System.out.println(bean.getF1());
	}
}
