package com.github.sunnysuperman.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	private static void addFileToZip(File srcFile, ZipOutputStream out, String base) throws IOException {
		if (!base.isEmpty() && base.charAt(base.length() - 1) != '/') {
			base += '/';
		}
		String entryName = base + srcFile.getName();
		if (srcFile.isFile()) {
			out.putNextEntry(new ZipEntry(entryName));
			FileInputStream in = null;
			try {
				in = new FileInputStream(srcFile);
				FileUtil.copy(in, out);
			} finally {
				FileUtil.close(in);
			}
			out.closeEntry();
		} else {
			File[] files = srcFile.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					addFileToZip(files[i], out, entryName);
				}
			}
		}
	}

	public static void zip(File archiveFile, File[] srcFiles, String encoding) throws IOException {
		FileUtil.ensureFile(archiveFile);
		if (encoding == null) {
			encoding = StringUtil.UTF8;
		}
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(archiveFile), Charset.forName(encoding));
			for (File srcFile : srcFiles) {
				addFileToZip(srcFile, out, StringUtil.EMPTY);
			}
		} finally {
			FileUtil.close(out);
		}
	}

	public static void zip(File archiveFile, File srcFile, String encoding) throws IOException {
		if (!srcFile.exists()) {
			throw new IOException("Source file " + srcFile.getAbsolutePath() + " does not exists");
		}
		FileUtil.ensureFile(archiveFile);
		if (encoding == null) {
			encoding = StringUtil.UTF8;
		}
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(archiveFile), Charset.forName(encoding));
			addFileToZip(srcFile, out, StringUtil.EMPTY);
		} finally {
			FileUtil.close(out);
		}
	}

	public static void unzip(File archiveFile, File destDir, String encoding) throws IOException {
		if (!archiveFile.exists()) {
			throw new IOException("archive file " + archiveFile.getAbsolutePath() + " does not exists");
		}
		if (!destDir.exists()) {
			destDir.mkdirs();
		} else if (!destDir.isDirectory()) {
			throw new IOException(destDir.getAbsolutePath() + " is not a valid decompress destination folder");
		}
		if (encoding == null) {
			encoding = StringUtil.UTF8;
		}

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(archiveFile, Charset.forName(encoding));
			Enumeration<? extends ZipEntry> en = zipFile.entries();
			ZipEntry zipEntry = null;
			while (en.hasMoreElements()) {
				zipEntry = en.nextElement();
				String name = zipEntry.getName();
				File destFile = FileUtil.getFile(new String[] { destDir.getAbsolutePath(), name });
				if (zipEntry.isDirectory()) {
					destFile.mkdirs();
				} else {
					FileUtil.ensureFile(destFile);
					InputStream in = null;
					FileOutputStream out = null;
					try {
						in = zipFile.getInputStream(zipEntry);
						out = new FileOutputStream(destFile);
						FileUtil.copy(in, out);
					} finally {
						FileUtil.close(in);
						FileUtil.close(out);
					}
				}
			}
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (Exception ex) {
				}
			}
		}
	}
}
