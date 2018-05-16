package com.gree.gprs.uart.model;

import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.entity.Device;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.UartVariable;
import com.gree.gprs.variable.Variable;

/**
 * modbus 04 读word 协议
 * 
 * @author lihaotian
 *
 */
public class MbReadWordModel {

	private static byte[] heardBytes = new byte[26];

	/**
	 * 处理
	 */
	public static void analyze() {

		try {

			if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp()) {

				return;
			}

			UartVariable.Uart_Out_Buffer[2] = UartVariable.Uart_In_Buffer[0];
			UartVariable.Uart_Out_Buffer[3] = UartVariable.Uart_In_Buffer[1];

			// 获取读数据长度
			int dataLength = Utils.bytesToInt(UartVariable.Uart_In_Buffer, 4, 5) * 2;
			UartVariable.Uart_Out_Buffer[4] = (byte) dataLength;

			int readStart = Utils.bytesToInt(UartVariable.Uart_In_Buffer, 2, 3);

			buildHeardBytes();

			for (int i = readStart; i < readStart + dataLength; i++) {

				if (i < heardBytes.length) {

					UartVariable.Uart_Out_Buffer[5 + i - readStart] = heardBytes[i];

				} else {

					UartVariable.Uart_Out_Buffer[5 + i - readStart] = UartVariable.Server_Modbus_Word_Data[i
							- heardBytes.length];
				}
			}

			// crc16校验
			byte[] crc16 = CRC.crc16(UartVariable.Uart_Out_Buffer, 2, dataLength + 5);
			UartVariable.Uart_Out_Buffer[dataLength + 5] = crc16[1];
			UartVariable.Uart_Out_Buffer[dataLength + 6] = crc16[0];

			UartModel.build(dataLength + 7);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private static void buildHeardBytes() {

		// word0
		heardBytes[0] = (byte) 0x00;
		heardBytes[1] = Constant.GPRS_MODEL;

		// word1~8
		byte[] imeiBytes = Device.getInstance().getImei().getBytes();
		for (int i = 0; i < imeiBytes.length; i++) {

			heardBytes[i + 2] = imeiBytes[i];
		}
		heardBytes[17] = (byte) 0x00;

		// word9
		heardBytes[18] = (byte) 0x00;

		if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO) {

			heardBytes[19] = (byte) 0x02;

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			heardBytes[19] = (byte) 0x00;

		} else {

			heardBytes[19] = (byte) 0x01;
		}

		// word10
		heardBytes[20] = (byte) 0x00;
		// 故障代码
		switch (Variable.Gprs_Error_Type) {

		case Constant.GPRS_ERROR_TYPE_SIM:

			heardBytes[21] = (byte) 0x00;
			break;

		case Constant.GPRS_ERROR_TYPE_NETWORK:

			heardBytes[21] = (byte) 0x01;
			break;

		case Constant.GPRS_ERROR_TYPE_SERVER:

			heardBytes[21] = (byte) 0x03;
			break;

		default:
			heardBytes[21] = (byte) 0x00;
			break;
		}

		// word11
		heardBytes[22] = (byte) 0x00;
		heardBytes[23] = (byte) DeviceConfigure.getNetworkSignalLevel();

		// word12
		heardBytes[24] = (byte) 0x00;
//		if (Variable.Server_Data_Change) {
//
//			heardBytes[25] = (byte) 0xFF;
//			Variable.Server_Data_Change = false;
//
//		} else {

			heardBytes[25] = (byte) 0x00;
//		}
	}

}
