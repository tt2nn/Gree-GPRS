package com.gree.gprs.can;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import com.gree.gprs.data.DataCenter;
import com.gree.gprs.file.FileReadModel;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.variable.Variable;

/**
 * Manager data
 * 
 * @author lihaotian
 *
 */
public class CanDataManager {

	private static final String FILE_NAME_CAN_DATA = "CanData/CanData";
	private static int writeAddress = 0;

	private static FileConnection fileConnectionWrite;
	private static OutputStream outputStream;

	private static FileConnection fileConnectionRead;
	private static InputStream inputStream;

	/**
	 * init
	 */
	public static void init() {

		writeAddress = FileReadModel.queryDataAddress();

		try {
			FileConnection dir = (FileConnection) Connector.open("file:///Phone/CanData");

			if (!dir.exists()) {

				dir.mkdir();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * open write file
	 */
	private static void openWriteFile(String fileName, boolean newFile) {

		try {

			fileConnectionWrite = (FileConnection) Connector.open("file:///Phone/" + fileName);

			if (!fileConnectionWrite.exists()) {

				fileConnectionWrite.create();

			} else if (newFile) {

				fileConnectionWrite.delete();
				fileConnectionWrite.create();
			}

			outputStream = fileConnectionWrite.openOutputStream();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * close write file
	 */
	private static void closeWriteFile() {

		try {

			if (outputStream != null) {

				outputStream.close();
			}

			if (fileConnectionWrite != null) {

				fileConnectionWrite.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * open read file
	 */
	private static boolean openReadFile(String fileName) {

		try {

			fileConnectionRead = (FileConnection) Connector.open("file:///Phone/" + fileName);

			if (fileConnectionRead.exists()) {

				inputStream = fileConnectionRead.openInputStream();

				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * close read file
	 */
	private static void closeReadFile() {

		try {

			if (inputStream != null) {

				inputStream.close();
			}

			if (fileConnectionRead != null) {

				fileConnectionRead.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write data
	 */
	public static void writeData() {

		if (writeAddress >= DataCenter.TOTAL_SIZE) {

			writeAddress = 0;
		}

		String fileName = FILE_NAME_CAN_DATA + (writeAddress / DataCenter.BUFFER_SIZE);

		try {

			openWriteFile(fileName, true);

			outputStream.write(Variable.Data_Save_Buffer, 0, Variable.Data_Save_Buffer.length);
			writeAddress += DataCenter.BUFFER_SIZE;
			FileWriteModel.saveDataAddress(writeAddress);
			closeWriteFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write data
	 * 
	 * @param address
	 * @param data
	 */
	public static void writeData(int address, byte[] data) {

		String fileName = FILE_NAME_CAN_DATA + (address / DataCenter.BUFFER_SIZE);
		int start = address % DataCenter.BUFFER_SIZE;

		try {

			openWriteFile(fileName, false);
			outputStream.write(data, start, 256);
			closeWriteFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * read data
	 * 
	 * @param address
	 * @return
	 */
	public static boolean readData(int address) {

		if (address >= writeAddress) {

			return false;
		}

		if (address > writeAddress
				&& !(address > DataCenter.TOTAL_SIZE / 2 && writeAddress < DataCenter.TOTAL_SIZE / 2)) {

			return false;
		}

		if (address < writeAddress && writeAddress - address > DataCenter.TOTAL_SIZE / 2) {

			return false;
		}

		String fileName = FILE_NAME_CAN_DATA + (address / DataCenter.BUFFER_SIZE);

		try {

			if (!openReadFile(fileName)) {

				return false;
			}
			inputStream.read(Variable.Data_Query_Buffer);
			closeReadFile();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

}
