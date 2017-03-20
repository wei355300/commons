package com.github.sunnysuperman.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUtil {

	public static final byte CELL_STRING = 1;
	public static final byte CELL_INT = 2;
	public static final byte CELL_FLOAT = 3;
	public static final byte CELL_DOUBLE = 4;
	public static final byte CELL_LONG = 5;

	public static String getStringCellValue(HSSFCell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			return StringUtil.trimToNull(cell.getStringCellValue());
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				return FormatUtil.formatISO8601Date(date);
			}
			double d = cell.getNumericCellValue();
			long l = (long) d;
			if (l == d) {
				return String.valueOf(l);
			} else {
				return String.valueOf(d);
			}
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			return null;
		}
		throw new RuntimeException("Failed to find string cell value: " + cell.getCellType());
	}

	public static Number getNumericCellValue(HSSFCell cell) {
		if (cell == null) {
			return null;
		}
		Object object = null;
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			object = cell.getNumericCellValue();
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			object = StringUtil.trimToNull(cell.getStringCellValue());
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			return null;
		}
		return FormatUtil.parseNumber(object);
	}

	public static int getIntCellValue(HSSFCell cell) {
		Number number = getNumericCellValue(cell);
		return number == null ? 0 : number.intValue();
	}

	public static float getFloatCellValue(HSSFCell cell) {
		Number number = getNumericCellValue(cell);
		return number == null ? 0f : number.floatValue();
	}

	public static double getDoubleCellValue(HSSFCell cell) {
		Number number = getNumericCellValue(cell);
		return number == null ? 0d : number.doubleValue();
	}

	public static long getLongCellValue(HSSFCell cell) {
		Number number = getNumericCellValue(cell);
		return number == null ? 0L : number.longValue();
	}

	private static int findCellIndex(HSSFRow row, String cellName) throws ExcelException {
		int firstCell = row.getFirstCellNum();
		int lastCell = row.getLastCellNum();
		for (int i = firstCell; i <= lastCell; i++) {
			HSSFCell cell = row.getCell(i);
			if (cell == null) {
				continue;
			}
			String title = StringUtil.trimToEmpty(getStringCellValue(cell));
			if (title.equals(cellName)) {
				return i;
			}
		}
		throw new ExcelException(ExcelException.ERROR_COULD_NOT_FIND_COLUMN, cellName);
	}

	public static HSSFWorkbook getWorkbook(InputStream in) throws ExcelException {
		HSSFWorkbook wb = null;
		// POIFSFileSystem fs = null;
		try {
			// fs = new POIFSFileSystem(in);
			wb = new HSSFWorkbook(in);
		} catch (IOException e) {
			throw new ExcelException(ExcelException.ERROR_NOT_AN_EXCEL_FILE);
		} finally {
			FileUtil.close(in);
		}
		return wb;
	}

	public static HSSFWorkbook getWorkbook(File file) throws ExcelException {
		try {
			return getWorkbook(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new ExcelException(ExcelException.ERROR_NOT_AN_EXCEL_FILE);
		}
	}

	public static List<String> readHeader(HSSFSheet sheet) {
		HSSFRow row = sheet.getRow(0);
		int firstCell = row.getFirstCellNum();
		int lastCell = row.getLastCellNum();
		List<String> keys = new ArrayList<String>(lastCell - firstCell + 1);
		for (int i = firstCell; i <= lastCell; i++) {
			HSSFCell cell = row.getCell(i);
			if (cell != null) {
				keys.add(getStringCellValue(cell));
			}
		}
		return keys;
	}

	public static <M> List<M> read(File file, ExcelColumn[] columns, RowParser<M> parser) throws ExcelException,
			Exception {
		HSSFWorkbook wb = getWorkbook(file);
		HSSFSheet sheet = wb.getSheetAt(0);
		return read(sheet, columns, parser);
	}

	public static <M> List<M> read(HSSFSheet sheet, ExcelColumn[] columns, RowParser<M> parser) throws ExcelException,
			Exception {

		int firstRow = sheet.getFirstRowNum();
		int lastRow = sheet.getLastRowNum();
		if (firstRow != 0) {
			throw new ExcelException(ExcelException.ERROR_DATA_IS_EMPTY);
		}
		HSSFRow titleRow = sheet.getRow(firstRow);
		int[] indexes = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			indexes[i] = findCellIndex(titleRow, columns[i].getTitle());
		}

		List<M> list = new ArrayList<M>();

		for (int i = firstRow + 1; i <= lastRow; i++) {
			HSSFRow row = sheet.getRow(i);
			if (row == null) {
				continue;
			}

			Map<String, Object> item = new HashMap<String, Object>();

			for (int k = 0; k < indexes.length; k++) {
				HSSFCell cell = row.getCell(indexes[k]);
				if (cell == null) {
					continue;
				}
				ExcelColumn column = columns[k];
				Object value = null;
				switch (column.getType()) {
				case CELL_STRING:
					value = getStringCellValue(cell);
					break;
				case CELL_INT:
					value = getIntCellValue(cell);
					break;
				case CELL_FLOAT:
					value = getFloatCellValue(cell);
					break;
				case CELL_DOUBLE:
					value = getDoubleCellValue(cell);
					break;
				case CELL_LONG:
					value = getLongCellValue(cell);
					break;
				default:
					throw new ExcelException(ExcelException.ERROR_UNKNOWN_CELL_TYPE, column.getType());
				}
				item.put(column.getKey(), value);
			}

			M object = parser.parse(item, list, i);
			if (object == null) {
				continue;
			}
			list.add(object);

		}

		return list;
	}

	public static <D> void export(HSSFSheet sheet, String[] titles, Iterable<D> iter, RowRenderer<D> renderer) {

		int rowIndex = 0;

		HSSFRow titleRow = sheet.createRow(rowIndex);
		for (int i = 0; i < titles.length; i++) {
			titleRow.createCell(i).setCellValue(titles[i]);
		}

		for (D item : iter) {
			HSSFRow row = sheet.createRow(++rowIndex);
			renderer.render(row, item);
		}
	}

	public static class ExcelColumn {
		private String key;
		private String title;
		private byte type;

		public ExcelColumn(String key, String title, byte type) {
			super();
			this.key = key;
			this.title = title == null ? key : title;
			this.type = type;
		}

		public String getKey() {
			return key;
		}

		public String getTitle() {
			return title;
		}

		public byte getType() {
			return type;
		}

	}

	public static interface RowRenderer<R> {
		void render(HSSFRow row, R item);
	}

	public static interface RowParser<P> {
		P parse(Map<String, Object> dataAsMap, List<P> list, int rowIndex) throws Exception;
	}

	public static class ExcelException extends Exception {
		private static final long serialVersionUID = 1170540747666980151L;
		public static final int ERROR_NOT_AN_EXCEL_FILE = 1;
		public static final int ERROR_DATA_IS_EMPTY = 2;
		public static final int ERROR_COULD_NOT_FIND_COLUMN = 3;
		public static final int ERROR_UNKNOWN_CELL_TYPE = 4;

		private int errorCode;
		private Object[] errorParams;

		public ExcelException(int errorCode) {
			super();
			this.errorCode = errorCode;
		}

		public ExcelException(int errorCode, Object... errorParams) {
			super();
			this.errorCode = errorCode;
			this.errorParams = errorParams;
		}

		public int getErrorCode() {
			return errorCode;
		}

		public Object[] getErrorParams() {
			return errorParams;
		}

	}

}
