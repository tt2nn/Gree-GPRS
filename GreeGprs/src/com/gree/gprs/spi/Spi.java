package com.gree.gprs.spi;

import java.io.IOException;

import com.gree.gprs.data.DataCenter;
import com.gree.gprs.file.FileReadModel;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.spi.driver.W25Q64_driver;
import com.gree.gprs.spi.jedi.DriverException;
import com.gree.gprs.spi.jedi.FlashROM;
import com.gree.gprs.spi.jedi.FlashROMDeviceFactory;
import com.gree.gprs.spi.jedi.UnsupportedPageSizeException;
import com.gree.gprs.variable.Variable;

public class Spi {

	private static final int ERASE_SIZE = W25Q64_driver.SIZE_32K; // Size of erasing test

	private static int rwSize;

	private static FlashROM flashRom;
	private static int pageSize; // Page size which get from specific ROM device

	private static int writeAddress;
	private static final int romSize = 8 * 1024 * 1024;

	/**
	 * 初始化
	 * 
	 * @param rwSize
	 */
	public static void init(int rwSize) {

		try {

			// 初始化FlashRom 端口、读写的频率
			flashRom = FlashROMDeviceFactory.getDevice(W25Q64_driver.getDeviceDescriptor(0, 0, 20 * 1024 * 1024));
			pageSize = flashRom.getPageSize();
			Spi.rwSize = rwSize;
			writeAddress = FileReadModel.queryDataAddress();

			/**
			 * Read Manufacture/Device information
			 */
			/*
			 * byte[] manufacture_info = flashROM.readManufacturInfo(); byte[] device_info =
			 * flashROM.readDeviceInfo(); System.out.println("Manufacture ID: " +
			 * manufacture_info[0]); System.out.println("Device ID: " + device_info[0]);
			 */

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 写入数据
	 */
	public static void writeData() {

		try {

			// 当spi写入新的一页时，需要先擦除下一页
			int address = writeAddress;
			int res = address % ERASE_SIZE;

			if (res == 0) {

				if (address == romSize) {

					address = 0;
				}

				erase(address);
			}

			for (int i = 0; i < 15; i++) {

				byte[] spiData = new byte[256];

				for (int j = 0; j < pageSize; j++) {

					spiData[j] = Variable.Data_Save_Buffer[i * pageSize + j];
				}

				flashRom.pageProgram(address, spiData);
				address += pageSize;
			}
			address += pageSize;

			FileWriteModel.saveDataAddress(address);
			writeAddress = address;

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 写flash
	 * 
	 * @param address
	 * @param res
	 */
	public static void writeData(int address, byte[] res) {

		try {

			flashRom.pageProgram(address, res);

		} catch (UnsupportedPageSizeException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (DriverException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 读数据
	 * 
	 * @param readAddress
	 * @return 是否读取到数据
	 */
	public static boolean readData(int readAddress) {

		if (readAddress >= writeAddress) {

			return false;
		}

		if (readAddress > writeAddress
				&& !(readAddress > DataCenter.TOTAL_SIZE / 2 && writeAddress < DataCenter.TOTAL_SIZE / 2)) {

			return false;
		}

		if (readAddress < writeAddress && writeAddress - readAddress > DataCenter.TOTAL_SIZE / 2) {

			return false;
		}

		try {

			Variable.Data_Query_Buffer = flashRom.read(readAddress, rwSize);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 全部擦除
	 */
	public static void chipErase() {

		try {

			flashRom.chipErase();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 擦除
	 * 
	 * @param address
	 */
	private static void erase(int address) {

		try {

			flashRom.erase(address, ERASE_SIZE);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
