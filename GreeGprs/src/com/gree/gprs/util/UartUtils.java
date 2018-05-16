package com.gree.gprs.util;

import com.gree.gprs.variable.UartVariable;
import com.joshvm.greenland.io.modbus.ModbusController;

public class UartUtils {

	/**
	 * Reset Modbus Data
	 * 
	 * @param data
	 * @param start
	 */
	public static void resetModbusData(byte[] data, int start) {

		for (int i = start; i < data.length; i++) {

			if (i % 2 == 0) {

				data[i] = (byte) 0x80;

			} else {

				data[i] = (byte) 0x00;
			}
		}
	}

	/**
	 * Enable Native Response
	 * 
	 * @param enable
	 */
	public static void enableNativeResponse(boolean enable) {

		UartVariable.Enable_Native_Response = enable;
		ModbusController.getModbusController().enableNativeResponse(enable);
	}

}
