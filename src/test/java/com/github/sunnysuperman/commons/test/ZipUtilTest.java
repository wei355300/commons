package com.github.sunnysuperman.commons.test;

import java.io.File;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.utils.FileUtil;
import com.github.sunnysuperman.commons.utils.ZipUtil;

public class ZipUtilTest extends TestCase {

	private final String HOMEPATH = "/home/jesse/Desktop/";

	public void testUnZipGBK() throws Exception {
		ZipUtil.unzip(new File(HOMEPATH + "dog_food_brand.zip"),
				FileUtil.getFile(new String[] { HOMEPATH, "dog_food_brand_gbk" }), "GBK");
	}

	public void testUnZipUTF8() throws Exception {
		ZipUtil.unzip(new File(HOMEPATH + "dog_food_brand.zip"),
				FileUtil.getFile(new String[] { HOMEPATH, "dog_food_brand_utf8" }), null);
	}

	public void testZipGBK() throws Exception {
		ZipUtil.zip(new File(HOMEPATH + "dog_food_brand_gbk.zip"), new File(HOMEPATH + "dog_food_brand"), "GBK");
	}

	public void testZipUTF8() throws Exception {
		ZipUtil.zip(new File(HOMEPATH + "dog_food_brand_utf8.zip"), new File(HOMEPATH + "dog_food_brand"), null);
		ZipUtil.zip(new File(HOMEPATH + "dog_food_brand_utf82.zip"), new File(HOMEPATH + "dog_food_brand").listFiles(),
				null);
	}
}
