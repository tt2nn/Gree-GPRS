package com.gree.gprs.data;

import com.gree.gprs.spi.Spi;

public class DataQuery {

	public static boolean queryData(int address) {

		return Spi.readData(address);
	}
}
