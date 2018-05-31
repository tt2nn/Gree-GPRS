package com.gree.gprs.uart.model;

import java.io.IOException;

import com.gree.gprs.util.Utils;
import com.joshvm.greenland.io.modbus.DiscreteInputsBuffer;
import com.joshvm.greenland.io.modbus.ModbusController;

/**
 * modbus 02 读bit 协议
 * 
 * @author lihaotian
 *
 */
public class MbReadBitModel {

	private static byte[] data = new byte[6];
	private static DiscreteInputsBuffer discreteInputsBuffer;

	/**
	 * init
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {

		discreteInputsBuffer = ModbusController.getModbusController().allocateDiscreteInputsBuffer(0, 48, true);
		discreteInputsBuffer.setDefaultValues(data);
		discreteInputsBuffer.update(0, data, 0, data.length);
	}

	/**
	 * Receive Data From Server
	 * 
	 * @param serverData
	 * @param length
	 */
	public static void receiveServerData(byte[] serverData, int length) {

		for (int i = 0; i < length; i++) {

			data[i] = serverData[i];
		}

		discreteInputsBuffer.update(0, data, 0, length);
		Utils.resetByteArray(data);
	}

	/**
	 * 处理
	 */
	public static void analyze() {

	}

}
