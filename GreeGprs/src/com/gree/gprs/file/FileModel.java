package com.gree.gprs.file;

import com.gree.gprs.constant.FileConstant;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class FileModel {

	/**
	 * write file with String
	 * 
	 * @param FileName
	 * @param value
	 */
	protected static void writeFile(String FileName, String value) {

		byte[] valueBytes = value.getBytes();

		writeFile(FileName, valueBytes);
	}

	/**
	 * write file with int
	 * 
	 * @param FileName
	 * @param value
	 */
	protected static void writeFile(String FileName, int value) {

		byte[] valueBytes = Utils.intToBytes(value);

		writeFile(FileName, valueBytes);
	}

	/**
	 * write file with byte
	 * 
	 * @param FileName
	 * @param value
	 */
	protected static void writeFile(String FileName, byte value) {

		byte[] valueBytes = { value };

		writeFile(FileName, valueBytes);
	}

	/**
	 * write file with byte[]
	 * 
	 * @param FileName
	 * @param value
	 */
	protected static synchronized void writeFile(String FileName, byte[] value) {

		byte[] dataLength = Utils.intToBytes(value.length);

		Variable.File_Buffer[0] = dataLength[0];
		Variable.File_Buffer[1] = dataLength[1];

		for (int i = 0; i < value.length; i++) {

			Variable.File_Buffer[i + 2] = value[i];
		}

		FileConnection.writeFile(FileName);
	}

	/**
	 * 获取文件内容长度
	 * 
	 * @param fileName
	 * @return
	 */
	protected static synchronized int readFileLength(String fileName) {

		FileConnection.readFile(fileName);

		if (Variable.File_Buffer_Length > 0) {

			return Utils.bytesToInt(Variable.File_Buffer, 0, 1);
		}

		return 0;
	}

	/**
	 * 读文件 return boolean
	 * 
	 * @param fileName
	 * @return
	 */
	protected static boolean readFileBool(String fileName) {

		int length = readFileLength(fileName);

		if (length > 0) {

			if (Variable.File_Buffer[2] == (byte) 0x01) {

				return true;
			}
		}

		return false;
	}

	/**
	 * 读文件 return Int
	 * 
	 * @param FileName
	 * @return
	 */
	protected static int readFileInt(String fileName) {

		int length = readFileLength(fileName);

		if (length > 0 && length < 5) {

			return Utils.bytesToIntValue(Variable.File_Buffer, 2, length);
		}

		return 0;
	}

	/**
	 * 读文件 return String
	 * 
	 * @param fileName
	 * @return
	 */
	protected static String readFileString(String fileName) {

		int length = readFileLength(fileName);

		if (length > 0) {

			try {

				return new String(Variable.File_Buffer, 2, length);

			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return "";
	}

	/**
	 * 清空所有的File
	 */
	public static void deleteAllFile() {

		for (int i = 0; i < FileConstant.FILE_NAME_ARRAY.length; i++) {

			FileConnection.deleteFile(FileConstant.FILE_NAME_ARRAY[i]);
		}
	}

}
