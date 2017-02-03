package com.github.sunnysuperman.commons.locale;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.github.sunnysuperman.commons.utils.ExcelUtil;
import com.github.sunnysuperman.commons.utils.FormatUtil;
import com.github.sunnysuperman.commons.utils.StringUtil;
import com.github.sunnysuperman.commons.utils.ExcelUtil.ExcelColumn;
import com.github.sunnysuperman.commons.utils.ExcelUtil.ExcelException;
import com.github.sunnysuperman.commons.utils.ExcelUtil.RowParser;

public class ExcelLocaleBundle extends LocaleBundle {

	public static class ExcelLocaleBundleOptions extends LocaleBundleOptions {
		private InputStream workbookStream;
		private String sheetName;
		private int sheetIndex;
		private HSSFSheet sheet;
		private String keyText;
		private boolean toLowerCaseKey;

		public HSSFSheet getSheet() {
			return sheet;
		}

		public void setSheet(HSSFSheet sheet) {
			this.sheet = sheet;
		}

		public String getKeyText() {
			return keyText;
		}

		public void setKeyText(String keyText) {
			this.keyText = keyText;
		}

		public boolean isToLowerCaseKey() {
			return toLowerCaseKey;
		}

		public void setToLowerCaseKey(boolean toLowerCaseKey) {
			this.toLowerCaseKey = toLowerCaseKey;
		}

		public InputStream getWorkbookStream() {
			return workbookStream;
		}

		public void setWorkbookStream(InputStream workbookStream) {
			this.workbookStream = workbookStream;
		}

		public String getSheetName() {
			return sheetName;
		}

		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}

		public int getSheetIndex() {
			return sheetIndex;
		}

		public void setSheetIndex(int sheetIndex) {
			this.sheetIndex = sheetIndex;
		}

	}

	public ExcelLocaleBundle(final ExcelLocaleBundleOptions options) {
		super(options);
		if (options.getSheet() == null) {
			if (options.getWorkbookStream() == null) {
				throw new RuntimeException("No sheet specified.");
			}
			HSSFSheet sheet = null;
			HSSFWorkbook book;
			try {
				book = ExcelUtil.getWorkbook(options.getWorkbookStream());
			} catch (ExcelException e) {
				throw new RuntimeException(e);
			}
			if (StringUtil.isNotEmpty(options.getSheetName())) {
				sheet = book.getSheet(options.getSheetName());
			} else {
				sheet = book.getSheetAt(options.getSheetIndex());
			}
			options.setSheet(sheet);
		}
		List<String> titles = ExcelUtil.readHeader(options.getSheet());
		String keyText = options.getKeyText();
		if (keyText == null) {
			keyText = "key";
		}
		List<ExcelColumn> columnList = new ArrayList<ExcelColumn>(titles.size());
		final Set<String> locales = new HashSet<String>(titles.size() - 1);
		for (String title : titles) {
			if (StringUtil.isEmpty(title)) {
				continue;
			}
			if (title.equalsIgnoreCase(keyText)) {
				ExcelColumn column = new ExcelColumn("key", title, ExcelUtil.CELL_STRING);
				columnList.add(column);
			} else {
				String locale = LocaleUtil.formatLocale(title);
				if (locale == null) {
					throw new RuntimeException("Bad locale for title " + title);
				}
				if (locales.contains(locale)) {
					throw new RuntimeException("Duplicate locale " + locale);
				}
				locales.add(locale);
				ExcelColumn column = new ExcelColumn(locale, title, ExcelUtil.CELL_STRING);
				columnList.add(column);
			}
		}
		ExcelColumn[] columns = new ExcelColumn[columnList.size()];
		columnList.toArray(columns);
		try {
			ExcelUtil.read(options.getSheet(), columns, new RowParser<Object>() {

				@Override
				public Object parse(Map<String, Object> dataAsMap, List<Object> list, int rowIndex) throws Exception {
					String key = StringUtil.trimToNull(FormatUtil.parseString(dataAsMap.get("key")));
					if (key == null) {
						return null;
					}
					if (options.isToLowerCaseKey()) {
						key = key.toLowerCase();
					}
					for (String locale : locales) {
						String value = StringUtil.trimToNull(FormatUtil.parseString(dataAsMap.get(locale)));
						if (value == null) {
							continue;
						}
						put(key, locale, value);
					}
					return null;
				}

			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		finishPut();
	}

	public static ExcelLocaleBundle newInstance(ExcelLocaleBundleOptions options) throws Exception {
		return new ExcelLocaleBundle(options);
	}

}
