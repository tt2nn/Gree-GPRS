package com.gree.gprs.uart;

import com.gree.gprs.data.DataCenter;
import com.gree.gprs.uart.model.FrockCheckModel;
import com.gree.gprs.uart.model.MbReadBitModel;
import com.gree.gprs.uart.model.MbReadWordModel;
import com.gree.gprs.uart.model.MbWriteModel;
import com.gree.gprs.uart.model.SeveneModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * Uart功能
 * 
 * @author lihaotian
 *
 */
public class UartModel {

	private static final byte[] FROCK_BYTES = { (byte) 0x55, (byte) 0xAA, (byte) 0x55, (byte) 0xAA, (byte) 0x15,
			(byte) 0x00, (byte) 0x00, (byte) 0x5D, (byte) 0x36 };

	/**
	 * 判断串口通信协议类型（7E7E / modbus）
	 */
	public static void analyze() {

		// time = System.currentTimeMillis();

		if (Variable.Uart_In_Buffer_Length == 4 && Variable.Uart_In_Buffer[0] == (byte) 0xA5
				&& Variable.Uart_In_Buffer[1] == (byte) 0xA7 && Variable.Uart_In_Buffer[2] == (byte) 0xB6
				&& Variable.Uart_In_Buffer[3] == (byte) 0xB4) {

			// A5 A7 B6 B4 是一个机组帧
			DataCenter.saveDataBuffer(Variable.Uart_In_Buffer, Variable.Uart_In_Buffer_Length);

			return;
		}

		// 判断是否是工装测试
		boolean isFrock = true;
		for (int i = 0; i < FROCK_BYTES.length; i++) {

			if (Variable.Uart_In_Buffer[i] != FROCK_BYTES[i]) {

				isFrock = false;
				break;
			}
		}
		if (isFrock) {

			FrockCheckModel.frockCheck();

			return;
		}

		// 如果是 A5A7 开头 则是 7E7E协议
		if (Variable.Uart_In_Buffer[0] == (byte) 0xA5 && Variable.Uart_In_Buffer[1] == (byte) 0xA7) {

			// 7E7E 下标5表示 有效数据长度
			int dataLength = Variable.Uart_In_Buffer[6] & 0xFF;

			// 有效数据长度是否符合条件，验证CRC8校验是否正确
			if (dataLength <= 85
					&& Variable.Uart_In_Buffer[7 + dataLength] == CRC.crc8(Variable.Uart_In_Buffer, 7 + dataLength)) {

				SeveneModel.analyze();
				logBuffer();
				DataCenter.saveDataBuffer(Variable.Uart_In_Buffer, Variable.Uart_In_Buffer_Length);
			}

			return;
		}

		// 判断是modbus协议
		if (Variable.Uart_In_Buffer[0] == (byte) 0xFF || Variable.Uart_In_Buffer[0] == (byte) 0XF7) {

			// System.out.println("message is modbus");

			switch (Variable.Uart_In_Buffer[1]) {

			case (byte) 0x10: // 10写功能

				if (Variable.Uart_In_Buffer_Length == 8) {

					return;
				}

				// modbus 下标6表示 有效数据长度
				int dataLength = Variable.Uart_In_Buffer[6] & 0xFF;

				byte[] crc10 = CRC.crc16(Variable.Uart_In_Buffer, 7 + dataLength);

				// 判断CRC16校验是否正确
				if (dataLength <= 246 && Variable.Uart_In_Buffer[7 + dataLength] == crc10[1]
						&& Variable.Uart_In_Buffer[8 + dataLength] == crc10[0]) {

					MbWriteModel.analyze();
					logBuffer();
					DataCenter.saveDataBuffer(Variable.Uart_In_Buffer, Variable.Uart_In_Buffer_Length);
				}

				return;

			case (byte) 0x04: // 读word CRC16的校验位在6和7

				if (Variable.Uart_In_Buffer_Length > 8) {

					return;
				}

				byte[] crc04 = CRC.crc16(Variable.Uart_In_Buffer, 6);

				if (Utils.bytesToInt(Variable.Uart_In_Buffer, 4, 5) <= 123 && Variable.Uart_In_Buffer[6] == crc04[1]
						&& Variable.Uart_In_Buffer[7] == crc04[0]) {

					// logBuffer();

					MbReadWordModel.analyze();
				}

				return;

			case (byte) 0x02:// 读bit

				if (Variable.Uart_In_Buffer_Length > 8) {

					return;
				}

				byte[] crc02 = CRC.crc16(Variable.Uart_In_Buffer, 6);

				if (Utils.bytesToInt(Variable.Uart_In_Buffer, 4, 5) <= 48 && Variable.Uart_In_Buffer[6] == crc02[1]
						&& Variable.Uart_In_Buffer[7] == crc02[0]) {

					// logBuffer();

					MbReadBitModel.analyze();
				}

				return;
			}
		}

		// 如果GPRS被选中则缓存机组数据
		DataCenter.saveDataBuffer(Variable.Uart_In_Buffer, Variable.Uart_In_Buffer_Length);
	}

	/**
	 * 建立 发出数据 并 调用 server 发送函数
	 */
	public static void build(int length) {

		Variable.Uart_Out_Buffer[0] = (byte) 0xFA;
		Variable.Uart_Out_Buffer[1] = (byte) 0xFB;

		// Logger.log(Constant.Uart_Out_Buffer, length);

		UartServer.sendData(length);
	}

	/**
	 * 打log用于测试
	 */
	private static void logBuffer() {

		Logger.log(Variable.Uart_In_Buffer, Variable.Uart_In_Buffer_Length);
	}

}
