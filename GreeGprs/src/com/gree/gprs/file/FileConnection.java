package com.gree.gprs.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;

import com.gree.gprs.util.Utils;

public class FileConnection {

	private static OutputStream outputStream;
	private static InputStream inputStream;

	private static javax.microedition.io.file.FileConnection fileConn;

	/**
	 * 创建通信
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	private static void openFile(String fileName) throws IOException {

		fileConn = (javax.microedition.io.file.FileConnection) Connector.open("file:///Phone/" + fileName);

		if (!fileConn.exists()) {

			fileConn.create();
		}
	}

	/**
	 * 读文件
	 * 
	 * @param fileName
	 */
	public static int readFile(String fileName, byte[] buffer) {

		return readFile(fileName, buffer, 0, buffer.length);
	}

	public static int readFile(String fileName, byte[] buffer, int start, int length) {

		Utils.resetByteArray(buffer);

		try {

			openFile(fileName);
			inputStream = fileConn.openInputStream();

			int len = inputStream.read(buffer, 0, length);
			closeFile();

			return len;

		} catch (IOException e) {

			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 写文件
	 * 
	 * @param fileName
	 */
	public static void writeFile(String fileName, byte[] buffer, int start, int length) {

		try {

			openFile(fileName);
			outputStream = fileConn.openOutputStream();

			outputStream.write(buffer, start, length);
			closeFile();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 写文件
	 * 
	 * @param fileName
	 */
	public static void writeFile(String fileName, byte[] buffer) {

		writeFile(fileName, buffer, 0, buffer.length);
	}

	/**
	 * Delete File
	 * 
	 * @param fileName
	 */
	public static void deleteFile(String fileName) {

		try {

			fileConn = (javax.microedition.io.file.FileConnection) Connector.open("file:///Phone/" + fileName);

			if (fileConn.exists()) {

				fileConn.delete();
			}

			closeFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭File
	 * 
	 * @throws IOException
	 */
	private static void closeFile() throws IOException {

		if (outputStream != null) {

			outputStream.close();
			outputStream = null;
		}

		if (inputStream != null) {

			inputStream.close();
			inputStream = null;
		}

		if (fileConn != null) {

			fileConn.close();
			fileConn = null;
		}
	}

}
