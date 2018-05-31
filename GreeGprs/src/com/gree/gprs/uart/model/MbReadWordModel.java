package com.gree.gprs.uart.model;

import java.io.IOException;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.entity.Device;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.variable.Variable;
import com.joshvm.greenland.io.modbus.InputRegistersStack;
import com.joshvm.greenland.io.modbus.ModbusController;

/**
 * modbus 04 读word 协议
 * 
 * @author lihaotian
 *
 */
public class MbReadWordModel {

	private static byte[] data = new byte[720];
	private static InputRegistersStack inputRegistersStack;

	/**
	 * init
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {

		inputRegistersStack = ModbusController.getModbusController().allocateInputRegisters(0, 360, true);

		// word0
		data[0] = (byte) 0x00;
		data[1] = Variable.Gprs_Model;

		// word1~8
		byte[] imeiBytes = Device.getInstance().getImei().getBytes();
		for (int i = 0; i < imeiBytes.length; i++) {

			data[i + 2] = imeiBytes[i];
		}
		data[17] = (byte) 0x00;

		// word9
		data[18] = (byte) 0x00;
		data[19] = (byte) 0x00;

		// word10
		data[20] = (byte) 0x00;
		data[21] = (byte) 0x00;

		// word11
		data[22] = (byte) 0x00;
		data[23] = (byte) Variable.Network_Signal_Level;

		// word12
		data[24] = (byte) 0x00;
		data[25] = (byte) 0x00;

		UartModel.resetModbusData(data, 26);

		inputRegistersStack.setDefaultValues(data);
		inputRegistersStack.setVolatile(0, 12, false);
		inputRegistersStack.update(0, data, 0, data.length);
	}

	/**
	 * Receive Data From Server
	 * 
	 * @param serverData
	 * @param length
	 */
	public static void receiveServerData(byte[] serverData, int length) {

		for (int i = 1; i < length; i++) {

			data[25 + i] = serverData[i];
		}

		data[25] = (byte) 0xFF;
		inputRegistersStack.update(12, data, 24, length + 1);
	}

	/**
	 * Update Data
	 */
	public static void updateData() {

		if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO) {

			data[19] = (byte) 0x02;

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			data[19] = (byte) 0x00;

		} else {

			data[19] = (byte) 0x01;
		}

		// 故障代码
		switch (Variable.Gprs_Error_Type) {

		case Constant.GPRS_ERROR_TYPE_SIM:

			data[21] = (byte) 0x00;
			break;

		case Constant.GPRS_ERROR_TYPE_NETWORK:

			data[21] = (byte) 0x01;
			break;

		case Constant.GPRS_ERROR_TYPE_SERVER:

			data[21] = (byte) 0x03;
			break;

		default:
			data[21] = (byte) 0x00;
			break;
		}

		data[23] = (byte) Variable.Network_Signal_Level;

		if (inputRegistersStack != null) {

			inputRegistersStack.update(9, data, 18, 6);
		}
	}

	/**
	 * 处理
	 */
	public static void analyze() {

	}
}
