package com.github.sunnysuperman.commons;

import java.io.InputStream;

/**
 * 资源文件类
 * 
 * 
 *
 */
public class Resources {

	/**
	 * 获取资源文件的输入流
	 * 
	 * @param fileName
	 *            文件名，为com.petkit.base.conf下面的文件
	 * @return 文件输入流
	 */
	public static InputStream getResourceAsStream(String fileName) {
		if (fileName.charAt(0) == '/') {
			throw new RuntimeException("Bad file name");
		}
		return Resources.class.getResourceAsStream("resources/" + fileName);
	}
}
