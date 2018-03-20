package com.gree.gprs.uart.model;

import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.entity.Device;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * modbus 04 读word 协议
 * 
 * @author lihaotian
 *
 */
public class MbReadWordModel {

	/**
	 * 处理
	 */
	public static void analyze() {

		if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		Variable.Uart_Out_Buffer[2] = Variable.Uart_In_Buffer[0];
		Variable.Uart_Out_Buffer[3] = Variable.Uart_In_Buffer[1];

		// 获取读数据长度
		int dataLength = Utils.bytesToInt(Variable.Uart_In_Buffer, 4, 5) * 2;
		Variable.Uart_Out_Buffer[4] = (byte) dataLength;

		// word0
		Variable.Uart_Out_Buffer[5] = (byte) 0x00;
		Variable.Uart_Out_Buffer[6] = Constant.GPRS_MODEL;

		// word1~8
		byte[] imeiBytes = Device.getInstance().getImei().getBytes();
		for (int i = 7; i < 7 + imeiBytes.length; i++) {

			Variable.Uart_Out_Buffer[i] = imeiBytes[i - 7];
		}
		Variable.Uart_Out_Buffer[22] = (byte) 0x00;

		// word9
		Variable.Uart_Out_Buffer[23] = (byte) 0x00;

		if (Variable.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO) {

			Variable.Uart_Out_Buffer[24] = (byte) 0x02;

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			Variable.Uart_Out_Buffer[24] = (byte) 0x00;

		} else {

			Variable.Uart_Out_Buffer[24] = (byte) 0x01;
		}

		// word10
		Variable.Uart_Out_Buffer[25] = (byte) 0x00;
		// 故障代码
		switch (Variable.GPRS_ERROR_TYPE) {

		case Constant.GPRS_ERROR_TYPE_SIM:

			Variable.Uart_Out_Buffer[26] = (byte) 0x00;
			break;

		case Constant.GPRS_ERROR_TYPE_NETWORK:

			Variable.Uart_Out_Buffer[26] = (byte) 0x01;
			break;

		case Constant.GPRS_ERROR_TYPE_SERVER:

			Variable.Uart_Out_Buffer[26] = (byte) 0x03;
			break;

		default:
			Variable.Uart_Out_Buffer[26] = (byte) 0x00;
			break;
		}

		// word11
		Variable.Uart_Out_Buffer[27] = (byte) 0x00;
		Variable.Uart_Out_Buffer[28] = (byte) DeviceConfigure.getNetworkSignalLevel();

		// word12
		Variable.Uart_Out_Buffer[29] = (byte) 0x00;
		if (Variable.Data_Word_Change) {

			Variable.Uart_Out_Buffer[30] = (byte) 0xFF;
			Variable.Data_Word_Change = false;

		} else {

			Variable.Uart_Out_Buffer[30] = (byte) 0x00;
		}

		int readStart = Utils.bytesToInt(Variable.Uart_In_Buffer, 2, 3);
		// 回复读数据内容
		for (int i = 31; i < dataLength + 5; i++) {

			Variable.Uart_Out_Buffer[i] = Variable.Server_Data_Word_Buffer[i - 31 + readStart];
		}

		// crc16校验
		byte[] crc16 = CRC.crc16(Variable.Uart_Out_Buffer, 2, dataLength + 5);
		Variable.Uart_Out_Buffer[dataLength + 5] = crc16[1];
		Variable.Uart_Out_Buffer[dataLength + 6] = crc16[0];

		UartModel.build(dataLength + 7);
	}

}
