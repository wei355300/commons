package com.github.sunnysuperman.commons.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import com.github.sunnysuperman.commons.Resources;
import com.github.sunnysuperman.commons.utils.FileUtil;
import com.github.sunnysuperman.commons.utils.StringUtil;
import com.github.sunnysuperman.commons.utils.FileUtil.FileListHandler;
import com.github.sunnysuperman.commons.utils.FileUtil.ReadLineHandler;

public class FileUtilTest extends TestCase {

	public void testGetFile() throws Exception {
		assertTrue(FileUtil.getFile(new String[] { "/rd", "\\a\\a/", "\\bc/", "", "/de", "\\", "//", "/" })
				.getAbsolutePath().equals("/rd/a/a/bc/de"));
	}

	public void testlistClassPathFiles() throws Exception {
		FileUtil.listClassPathFiles("/opt/rd/projects/petkit-base/target/petkit-base.jar", "", new FileListHandler() {

			@Override
			public boolean willOpenStream(String fileName, String fullPath, boolean isDirectory) throws Exception {
				System.out.println("fileName: " + fileName + ", fullPath : " + fullPath);
				return true;
			}

			@Override
			public void streamOpened(String fileName, String fullPath, InputStream in) throws Exception {
			}

		});
	}

	public void testlistClassPathFiles2() throws Exception {
		FileUtil.listClassPathFiles(Resources.class, "", new FileListHandler() {

			@Override
			public boolean willOpenStream(String fileName, String fullPath, boolean isDirectory) throws Exception {
				System.out.println("fileName: " + fileName + ", fullPath : " + fullPath);
				return true;
			}

			@Override
			public void streamOpened(String fileName, String fullPath, InputStream in) throws Exception {
			}

		});
	}

	private static final String ALPHA_NUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static boolean isAlphaNumeric(char c) {
		return StringUtil.isTargetChar(ALPHA_NUMERIC, c);
	}

	public void testProcessJavaFile() throws Exception {
		final StringBuilder sb = new StringBuilder();
		FileUtil.read(new FileInputStream("/home/jesse/Desktop/ziputil.java"), null, new ReadLineHandler() {

			@Override
			public boolean handle(String s, int line) throws Exception {
				int offset = -1;
				for (int i = 0; i < s.length(); i++) {
					char c = s.charAt(i);
					if (isAlphaNumeric(c) || c == '/' || c == '@' || c == '{' || c == '}' || c == '[' || c == ']') {
						offset = i;
						break;
					}
				}
				if (offset < 0) {
					return true;
				}
				if (offset > 0) {
					s = s.substring(offset);
				}
				sb.append(s).append("\n");
				return true;
			}

		});
		FileUtil.write(new File("/home/jesse/Desktop/ZipUtilNew.java"), sb.toString());
	}
}
