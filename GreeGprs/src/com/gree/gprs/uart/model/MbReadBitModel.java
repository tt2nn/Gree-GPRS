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

			if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp()) {

				return;
			}

			Variable.Uart_Out_Buffer[2] = Variable.Uart_In_Buffer[0];
			Variable.Uart_Out_Buffer[3] = Variable.Uart_In_Buffer[1];

			// 读 bit长度 转化byte长度
			int dataLength = Utils.bytesToInt(Variable.Uart_In_Buffer, 4, 5) / 8;
			Variable.Uart_Out_Buffer[4] = (byte) dataLength;

			// 数据内容
			for (int i = 5; i < dataLength + 5; i++) {

				Variable.Uart_Out_Buffer[i] = Variable.Server_Data_Short_Buffer[i - 5];
			}

			// crc16
			byte[] crc16 = CRC.crc16(Variable.Uart_Out_Buffer, 2, dataLength + 5);
			Variable.Uart_Out_Buffer[dataLength + 5] = crc16[1];
			Variable.Uart_Out_Buffer[dataLength + 6] = crc16[0];

			Utils.resetData(Variable.Server_Data_Short_Buffer);

			UartModel.build(dataLength + 7);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
