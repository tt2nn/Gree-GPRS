package com.gree.gprs.uart.model;

import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * modbus 02 读bit 协议
 * 
 * @author lihaotian
 *
 */
public class MbReadBitModel {

	/**
	 * 处理
	 */
	public static void analyze() {

		try {

			if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp() && UartModel.Enable_Native_Response) {

				return;
			}

			UartModel.Uart_Out_Buffer[2] = UartModel.Uart_In_Buffer[0];
			UartModel.Uart_Out_Buffer[3] = UartModel.Uart_In_Buffer[1];

			// 读 bit长度 转化byte长度
			int dataLength = Utils.bytesToInt(UartModel.Uart_In_Buffer, 4, 5) / 8;
			UartModel.Uart_Out_Buffer[4] = (byte) dataLength;

			// 数据内容
			// for (int i = 5; i < dataLength + 5; i++) {
			//
			// UartModel.Uart_Out_Buffer[i] = UartModel.Server_Data_Short_Buffer[i -
			// 5];
			// }

			// crc16
			byte[] crc16 = CRC.crc16(UartModel.Uart_Out_Buffer, 2, dataLength + 5);
			UartModel.Uart_Out_Buffer[dataLength + 5] = crc16[1];
			UartModel.Uart_Out_Buffer[dataLength + 6] = crc16[0];

			// Utils.resetData(UartModel.Server_Data_Short_Buffer);

			UartModel.build(dataLength + 7);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
