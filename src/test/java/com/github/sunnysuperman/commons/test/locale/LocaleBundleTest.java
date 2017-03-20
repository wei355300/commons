package com.github.sunnysuperman.commons.test.locale;

import java.io.File;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.locale.ClassPathPropertiesLocaleBundle;
import com.github.sunnysuperman.commons.locale.ClassPathPropertiesLocaleBundle.ClassPathPropertiesLocaleBundleOptions;
import com.github.sunnysuperman.commons.locale.DBLocaleBundle;
import com.github.sunnysuperman.commons.locale.DBLocaleBundle.DBLocaleBundleOptions;
import com.github.sunnysuperman.commons.locale.ExcelLocaleBundle;
import com.github.sunnysuperman.commons.locale.ExcelLocaleBundle.ExcelLocaleBundleOptions;
import com.github.sunnysuperman.commons.locale.FileSystemPropertiesLocaleBundle;
import com.github.sunnysuperman.commons.locale.FileSystemPropertiesLocaleBundle.FileSystemPropertiesLocaleBundleOptions;
import com.github.sunnysuperman.commons.model.TimeSerializeType;
import com.github.sunnysuperman.commons.test.BaseTest.TestDB;
import com.github.sunnysuperman.commons.utils.CollectionUtil;

public class LocaleBundleTest extends TestCase {

	public void testClassPathPropertiesLocaleBundle() throws Exception {
		ClassPathPropertiesLocaleBundleOptions options = new ClassPathPropertiesLocaleBundleOptions();
		options.setDefaultLocale("zh_CN");
		options.setPrefLocales(new String[] { "zh_CN" });
		options.setClazz(LocaleBundleTest.class);
		options.setPath("conf/locales");
		options.setStrictMode(true);
		ClassPathPropertiesLocaleBundle bundle = new ClassPathPropertiesLocaleBundle(options);

		{
			String s = bundle.getWithMapParams("en_US", "mate.default.name", CollectionUtil.arrayAsMap("count", 1));
			System.out.println(s);
			assertTrue(s.equals("My first Mate"));
		}

		{
			String s = bundle.getWithMapParams("en_US", "mate.default.name", CollectionUtil.arrayAsMap("count", 2));
			System.out.println(s);
			assertTrue(s.equals("My second Mate"));
		}

		{
			String s = bundle.getWithMapParams("en_US", "mate.default.name", CollectionUtil.arrayAsMap("count", 5));
			System.out.println(s);
			assertTrue(s.equals("My Mate (4)"));
		}

		{
			String s = bundle.getWithMapParams("zh_CN", "mate.default.name", CollectionUtil.arrayAsMap("count", 1));
			System.out.println(s);
		}

		{
			String s = bundle.getWithMapParams("zh_CN", "mate.default.name", CollectionUtil.arrayAsMap("count", 2));
			System.out.println(s);
		}

		{
			String s = bundle.getWithMapParams("zh_CN", "mate.default.name", CollectionUtil.arrayAsMap("count", 3.5));
			System.out.println(s);
		}
	}

	public void testFileSystemPropertiesLocaleBundle() throws Exception {
		FileSystemPropertiesLocaleBundleOptions options = new FileSystemPropertiesLocaleBundleOptions();
		options.setDefaultLocale("zh_CN");
		options.setDir(new File("/data/tmp/localtest"));
		FileSystemPropertiesLocaleBundle bundle = new FileSystemPropertiesLocaleBundle(options);
		String key = "age.male.3";
		printValue(bundle.getRaw("zh_CN", "age.male.2"));
		printValue(bundle.getRaw("zh_CN", key));
		printValue(bundle.getRaw("en_US", key));
		printValue(bundle.getRaw("en_CN", key));
	}

	public void testDBLocaleBundle() throws Exception {
		DBLocaleBundleOptions options = new DBLocaleBundleOptions();
		options.setDefaultLocale("zh_CN");
		options.setPrefLocales(new String[] { "zh_CN", "en_US" });
		options.setJdbcTemplate(TestDB.getJdbcTemplate());
		options.setTableName("api_localebundle");
		options.setUpdatedAtSerializeType(TimeSerializeType.Long);
		options.setReloadSeconds(6);
		DBLocaleBundle bundle = new DBLocaleBundle(options);
		String key = "age.male.3";
		while (true) {
			printValue(bundle.getRaw("zh_CN", key));
			Thread.sleep(5000);
		}
	}

	public void testExcelLocaleBundle() throws Exception {
		ExcelLocaleBundleOptions options = new ExcelLocaleBundleOptions();
		options.setDefaultLocale("zh_CN");
		options.setPrefLocales(new String[] { "zh_CN", "en_US" });
		options.setWorkbookStream(LocaleBundleTest.class.getResourceAsStream("conf/apiserver.xls"));
		ExcelLocaleBundle bundle = new ExcelLocaleBundle(options);
		String key = "age.male.3";
		printValue(bundle.getRaw("zh_CN", key));
		printValue(bundle.getRaw("en_CN", key));
	}

	private void printValue(String value) {
		System.out.println("====" + value + "====");
	}
}
