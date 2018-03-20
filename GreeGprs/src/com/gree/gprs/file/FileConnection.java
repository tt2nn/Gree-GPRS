package com.gree.gprs.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;

import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

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
	public static void readFile(String fileName, FileInterface fileInterface) {

		try {

			openFile(fileName);
			inputStream = fileConn.openInputStream();

			Utils.resetData(Variable.File_Buffer);
			fileInterface.readFile(inputStream);

			closeFile();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 读文件
	 * 
	 * @param fileName
	 */
	public static void readFile(String fileName) {

		try {

			openFile(fileName);
			inputStream = fileConn.openInputStream();

			Utils.resetData(Variable.File_Buffer);
			Variable.File_Buffer_Length = inputStream.read(Variable.File_Buffer, 0, Variable.File_Buffer.length);

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
	public static void writeFile(String fileName, int start, int length) {

		try {

			openFile(fileName);
			outputStream = fileConn.openOutputStream();
			outputStream.write(Variable.File_Buffer, start, length);

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
	public static void writeFile(String fileName) {

		try {

			openFile(fileName);
			outputStream = fileConn.openOutputStream();
			outputStream.write(Variable.File_Buffer, 0, Variable.File_Buffer.length);

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

	public interface FileInterface {

		void readFile(InputStream inputStream);
	}

}
