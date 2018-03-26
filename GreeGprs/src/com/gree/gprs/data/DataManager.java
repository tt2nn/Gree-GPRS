package com.gree.gprs.data;

import com.gree.gprs.spi.Spi;
import com.gree.gprs.variable.Variable;

public class DataManager {

	private static byte[] dataSend = new byte[256];

	public static void init() {

		dataSend[0] = (byte) 0x01;
	}

	public static void saveData(byte[] data) {

		Spi.writeData();
	}

	public static boolean queryData(int address) {

		return Spi.readData(address);
	}

	public static boolean queryDataIsSend() {

		return Variable.Data_Query_Buffer[1792] == (byte) 0x01;
	}

	public static void saveDataIsSend(int address) {

		Spi.writeData(address, dataSend);
	}

}
