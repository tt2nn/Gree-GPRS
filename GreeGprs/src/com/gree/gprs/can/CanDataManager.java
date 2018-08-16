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

	private static final int FILE_LENGTH = 256 * 1024;
	private static int writePoi = -1;
	private static int readPoi = -1;

	private static byte[] writeBuffer;

	/**
	 * init
	 */
	public static void init() {

		writeAddress = FileReadModel.queryDataAddress();
		writeBuffer = new byte[FILE_LENGTH];

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
	 * 
	 * @throws IOException
	 */
	private static void openWriteFile(String fileName) throws IOException {

		fileConnectionWrite = (FileConnection) Connector.open("file:///Phone/" + fileName);

		if (!fileConnectionWrite.exists()) {

			fileConnectionWrite.create();
		}

		outputStream = fileConnectionWrite.openOutputStream();
	}

	/**
	 * close write file
	 * 
	 * @throws IOException
	 */
	private static void closeWriteFile() throws IOException {

		if (outputStream != null) {

			outputStream.close();
		}

		if (fileConnectionWrite != null) {

			fileConnectionWrite.close();
		}
	}

	/**
	 * open read file
	 * 
	 * @throws IOException
	 */
	private static boolean openReadFile(String fileName) throws IOException {

		fileConnectionRead = (FileConnection) Connector.open("file:///Phone/" + fileName);

		if (fileConnectionRead.exists()) {

			inputStream = fileConnectionRead.openInputStream();

			return true;
		}

		return false;
	}

	/**
	 * close read file
	 * 
	 * @throws IOException
	 */
	private static void closeReadFile() throws IOException {

		if (inputStream != null) {

			inputStream.close();
		}

		if (fileConnectionRead != null) {

			fileConnectionRead.close();
		}
	}

	/**
	 * write data
	 */
	public static void writeData() {

		try {

			if (writeAddress >= DataCenter.TOTAL_SIZE) {

				writeAddress = 0;
			}

			int poi = writeAddress / FILE_LENGTH;

			if (poi != writePoi) {

				closeWriteFile();

				writePoi = poi;
				String fileName = FILE_NAME_CAN_DATA + (writeAddress / poi);
				openWriteFile(fileName);
			}

			int offset = writeAddress % FILE_LENGTH;

			for (int i = 0; i < Variable.Data_Save_Buffer.length; i++) {

				writeBuffer[i + offset] = Variable.Data_Save_Buffer[i];
			}

			outputStream.write(Variable.Data_Save_Buffer, offset, Variable.Data_Save_Buffer.length);
			outputStream.flush();
			writeAddress += DataCenter.BUFFER_SIZE;
			FileWriteModel.saveDataAddress(writeAddress);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write data
	 * 
	 * @param address
	 * @param data
	 * @param start
	 * @param length
	 */
	public static void writeData(int address, byte[] data, int start, int length) {

		try {

			int poi = address / FILE_LENGTH;

			if (poi != writePoi) {

				closeWriteFile();

				writePoi = poi;
				String fileName = FILE_NAME_CAN_DATA + (writeAddress / poi);
				openWriteFile(fileName);
			}

			int offset = address % FILE_LENGTH;

			for (int i = start; i < length; i++) {

				writeBuffer[i + offset] = data[i];
			}

			outputStream.write(writeBuffer, offset + start, length);
			outputStream.flush();

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

		try {

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

			int poi = address / FILE_LENGTH;

			if (poi != readPoi) {

				readPoi = poi;
				String fileName = FILE_NAME_CAN_DATA + (writeAddress / poi);
				openReadFile(fileName);
			}

			inputStream.reset();
			inputStream.skip(address % FILE_LENGTH);
			inputStream.read(Variable.Data_Query_Buffer);
			closeReadFile();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

}
