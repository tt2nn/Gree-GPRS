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

	private static final int FILE_LENGTH = 64 * 1024;
	private static int writePoi = -1;
	private static int readPoi = -1;

	private static byte[] writeBuffer;
	private static byte[] readBuffer;

	private static byte[] dataSendBuffer = new byte[DataCenter.BUFFER_MARK_SIZE];

	/**
	 * init
	 */
	public static void init() {

		writeAddress = FileReadModel.queryDataAddress();
		writeBuffer = new byte[FILE_LENGTH];
		readBuffer = new byte[FILE_LENGTH];

		try {
			FileConnection dir = (FileConnection) Connector.open("file:///Phone/CanData");

			if (dir != null && !dir.exists()) {

				dir.mkdir();
			}

			FileConnection dataSendFileConnect = (FileConnection) Connector.open("file:///Phone/CanData/DataSend");
			if (dataSendFileConnect != null) {

				if (!dataSendFileConnect.exists()) {

					dataSendFileConnect.create();
				}

				InputStream dataSendInputStream = dataSendFileConnect.openInputStream();
				if (dataSendInputStream != null) {

					dataSendInputStream.read(dataSendBuffer);
					dataSendInputStream.close();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
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

			if (poi != writePoi || outputStream == null) {

				closeWriteFile();

				writePoi = poi;
				openWriteFile(FILE_NAME_CAN_DATA + poi);
			}

			if (outputStream != null) {

				int offset = writeAddress % FILE_LENGTH;

				for (int i = 0; i < Variable.Data_Save_Buffer.length; i++) {

					writeBuffer[i + offset] = Variable.Data_Save_Buffer[i];
				}

				outputStream.write(writeBuffer, offset, Variable.Data_Save_Buffer.length);
				outputStream.flush();

				if (readBuffer[writeAddress / DataCenter.BUFFER_SIZE] == (byte) 0x01) {

					sendDataMark(writeAddress, false);
				}

				writeAddress += DataCenter.BUFFER_SIZE;
				FileWriteModel.saveDataAddress(writeAddress);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Data Has Send Mark
	 * 
	 * @param address
	 * @param mark
	 */
	public static void sendDataMark(int address, boolean mark) {

		dataSendBuffer[address / DataCenter.BUFFER_SIZE] = mark ? (byte) 0x01 : (byte) 0x00;

		try {

			FileConnection dataSendFileConnect = (FileConnection) Connector.open("file:///Phone/CanData/DataSend");
			if (dataSendFileConnect != null) {

				if (!dataSendFileConnect.exists()) {

					dataSendFileConnect.create();
				}

				OutputStream dataSendOutputStream = dataSendFileConnect.openOutputStream();
				if (dataSendOutputStream != null) {

					dataSendOutputStream.write(dataSendBuffer);
					dataSendOutputStream.close();
				}

				dataSendFileConnect.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Query Data Mark
	 * 
	 * @param address
	 * @return
	 */
	public static byte queryDataMark(int address) {

		return dataSendBuffer[address / DataCenter.BUFFER_SIZE];
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
			int offset = address % FILE_LENGTH;

			if (poi == writePoi) {

				for (int i = 0; i < Variable.Data_Query_Buffer.length; i++) {
					Variable.Data_Query_Buffer[i] = writeBuffer[i + offset];
				}

			} else {

				if (inputStream == null || poi != readPoi) {

					closeReadFile();
					readPoi = poi;
					openReadFile(FILE_NAME_CAN_DATA + poi);

					if (inputStream != null) {

						inputStream.read(readBuffer);
					}
				}

				for (int i = 0; i < Variable.Data_Query_Buffer.length; i++) {
					Variable.Data_Query_Buffer[i] = readBuffer[i + offset];
				}

				if (poi == writePoi) {

					closeReadFile();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * open write file
	 * 
	 * @throws IOException
	 */
	private static void openWriteFile(String fileName) throws IOException {

		fileConnectionWrite = (FileConnection) Connector.open("file:///Phone/" + fileName);

		if (fileConnectionWrite != null) {

			if (!fileConnectionWrite.exists()) {

				fileConnectionWrite.create();
			}

			outputStream = fileConnectionWrite.openOutputStream();
		}
	}

	/**
	 * close write file
	 * 
	 * @throws IOException
	 */
	private static void closeWriteFile() throws IOException {

		if (outputStream != null) {

			outputStream.close();
			outputStream = null;
		}

		if (fileConnectionWrite != null) {

			fileConnectionWrite.close();
			fileConnectionWrite = null;
		}
	}

	/**
	 * open read file
	 * 
	 * @throws IOException
	 */
	private static void openReadFile(String fileName) throws IOException {

		fileConnectionRead = (FileConnection) Connector.open("file:///Phone/" + fileName);

		if (fileConnectionRead != null && fileConnectionRead.exists()) {

			inputStream = fileConnectionRead.openInputStream();
		}
	}

	/**
	 * close read file
	 * 
	 * @throws IOException
	 */
	private static void closeReadFile() throws IOException {

		if (inputStream != null) {

			inputStream.close();
			inputStream = null;
		}

		if (fileConnectionRead != null) {

			fileConnectionRead.close();
			fileConnectionRead = null;
		}
	}

}
