package com.gree.gprs.data;

import com.gree.gprs.spi.Spi;

public class DataSave {

	public static void saveData(byte[] data) {

		Spi.writeData();
	}

	public static void saveData(int address, byte[] data) {

		Spi.writeData(address, data);
	}
}
