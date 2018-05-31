package com.gree.gprs.uart;

import java.io.IOException;

import com.gree.gprs.data.DataCenter;
import com.gree.gprs.uart.model.FrockCheckModel;
import com.gree.gprs.uart.model.MbReadBitModel;
import com.gree.gprs.uart.model.MbReadWordModel;
import com.gree.gprs.uart.model.MbWriteModel;
import com.gree.gprs.uart.model.SeveneModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.joshvm.greenland.io.modbus.DTU7E7EController;
import com.joshvm.greenland.io.modbus.ModbusController;

/**
 * Uart功能
 * 
 * @author lihaotian
 *
 */
public class UartModel {

	private static byte[] frockBytes = { (byte) 0x55, (byte) 0xAA, (byte) 0x55, (byte) 0xAA, (byte) 0x15, (byte) 0x00,
			(byte) 0x00, (byte) 0x5D, (byte) 0x36 };

	public static int Uart_Type;
	public static final int UART_TYPE_7E = 1;
	public static final int UART_TYPE_MODBUS = 2;

	// 串口通讯Buffer
	public static byte[] Uart_In_Buffer = new byte[512];
	public static byte[] Uart_Out_Buffer = new byte[512];
	public static int Uart_In_Buffer_Length = 0;

	/**
	 * init
	 */
	public static void init() {

		try {

			MbWriteModel.init();
			MbReadWordModel.init();
			MbReadBitModel.init();
			SeveneModel.init();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断串口通信协议类型（7E7E / modbus）
	 */
	public static void analyze() {

		if (Uart_In_Buffer_Length == 4 && Uart_In_Buffer[0] == (byte) 0xA5 && Uart_In_Buffer[1] == (byte) 0xA7
				&& Uart_In_Buffer[2] == (byte) 0xB6 && Uart_In_Buffer[3] == (byte) 0xB4) {

			// A5 A7 B6 B4 是一个机组帧
			DataCenter.saveDataBuffer(Uart_In_Buffer, Uart_In_Buffer_Length);

			return;
		}

		// 判断是否是工装测试
		boolean isFrock = true;
		for (int i = 0; i < frockBytes.length; i++) {

			if (Uart_In_Buffer[i] != frockBytes[i]) {

				isFrock = false;
				break;
			}
		}
		if (isFrock) {

			FrockCheckModel.frockCheck();

			return;
		}

		// 如果是 A5A7 开头 则是 7E7E协议
		if (Uart_In_Buffer[0] == (byte) 0xA5 && Uart_In_Buffer[1] == (byte) 0xA7) {

			// 7E7E 下标5表示 有效数据长度
			int dataLength = Uart_In_Buffer[6] & 0xFF;

			// 有效数据长度是否符合条件，验证CRC8校验是否正确
			if (dataLength <= 85 && Uart_In_Buffer[7 + dataLength] == CRC.crc8(Uart_In_Buffer, 7 + dataLength)) {

				SeveneModel.analyze();
				logBuffer();
				DataCenter.saveDataBuffer(Uart_In_Buffer, Uart_In_Buffer_Length);
			}

			return;
		}

		// 判断是modbus协议
		if (Uart_In_Buffer[0] == (byte) 0xFF || Uart_In_Buffer[0] == (byte) 0XF7) {

			// System.out.println("message is modbus");

			switch (Uart_In_Buffer[1]) {

			case (byte) 0x10: // 10写功能

				if (Uart_In_Buffer_Length == 8) {

					return;
				}

				// modbus 下标6表示 有效数据长度
				int dataLength = Uart_In_Buffer[6] & 0xFF;

				byte[] crc10 = CRC.crc16(Uart_In_Buffer, 7 + dataLength);

				// 判断CRC16校验是否正确
				if (dataLength <= 246 && Uart_In_Buffer[7 + dataLength] == crc10[1]
						&& Uart_In_Buffer[8 + dataLength] == crc10[0]) {

					MbWriteModel.analyze();
					logBuffer();
					DataCenter.saveDataBuffer(Uart_In_Buffer, Uart_In_Buffer_Length);
				}

				return;

			case (byte) 0x04: // 读word CRC16的校验位在6和7

				if (Uart_In_Buffer_Length > 8) {

					return;
				}

				byte[] crc04 = CRC.crc16(Uart_In_Buffer, 6);

				if (Utils.bytesToInt(Uart_In_Buffer, 4, 5) <= 123 && Uart_In_Buffer[6] == crc04[1]
						&& Uart_In_Buffer[7] == crc04[0]) {

					// logBuffer();

					MbReadWordModel.analyze();
				}

				return;

			case (byte) 0x02:// 读bit

				if (Uart_In_Buffer_Length > 8) {

					return;
				}

				byte[] crc02 = CRC.crc16(Uart_In_Buffer, 6);

				if (Utils.bytesToInt(Uart_In_Buffer, 4, 5) <= 48 && Uart_In_Buffer[6] == crc02[1]
						&& Uart_In_Buffer[7] == crc02[0]) {

					// logBuffer();

					MbReadBitModel.analyze();
				}

				return;
			}
		}

		// 如果GPRS被选中则缓存机组数据
		DataCenter.saveDataBuffer(Uart_In_Buffer, Uart_In_Buffer_Length);
	}

	/**
	 * 建立 发出数据 并 调用 server 发送函数
	 */
	public static void build(int length) {

		Uart_Out_Buffer[0] = (byte) 0xFA;
		Uart_Out_Buffer[1] = (byte) 0xFB;

		// Logger.log(Constant.Uart_Out_Buffer, length);

		UartServer.sendData(length);
	}

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
	 * Native Response Select
	 */
	public static void nativeResponseSelect() {

		switch (UartModel.Uart_Type) {

		case UART_TYPE_MODBUS:

			ModbusController.getModbusController().enableNativeResponseSelect(true);
			break;

		case UART_TYPE_7E:

			DTU7E7EController.getDTU7E7EController().enableNativeResponseSelect(true);
			break;
		}
	}

	/**
	 * Native Response Voting
	 */
	public static void nativeResponseVoting() {

		switch (UartModel.Uart_Type) {

		case UART_TYPE_MODBUS:

			ModbusController.getModbusController().enableNativeResponseVoting(true);
			break;

		case UART_TYPE_7E:

			DTU7E7EController.getDTU7E7EController().enableNativeResponseVoting(true);
			break;
		}
	}

	/**
	 * 打log用于测试
	 */
	private static void logBuffer() {

		Logger.log(Uart_In_Buffer, Uart_In_Buffer_Length);
	}

}
