package com.gree.gprs.uart.model;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.variable.Variable;

/**
 * modbus 10 写 协议 model
 * 
 * @author lihaotian
 *
 */
public class MbWriteModel {

	/**
	 * 解析
	 */
	public static void analyze() {

		switch (Variable.Uart_In_Buffer[10]) {

		case (byte) Constant.FUNCTION_CALL: // 点名

			call();

			break;

		case (byte) Constant.FUNCTION_CHOOSE: // 选举

			choose();

			break;
		}
	}

	/**
	 * 选举
	 */
	private static void choose() {

		if (Variable.Gprs_Choosed) {

			ControlCenter.chooseRest();
		}

		if (!DoChoose.choose()) {

			return;
		}

		buildSendBuffer();

		UartModel.build(10);

	}

	/**
	 * 点名
	 */
	private static void call() {

		if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		buildSendBuffer();

		UartModel.build(10);

		if (!Variable.Gprs_Init_Success) {

			return;
		}

		if (!Variable.Gprs_Choosed && DoChoose.isChooseResp()) {

			ControlCenter.chooseGprs();
			return;
		}

		// 判断是否是 上电是状态为选中
		if (!DoChoose.isChooseResp() && !DataCenter.DoPowerTransmit) {

			DataCenter.powerTransmit();
			return;
		}

		ControlCenter.setMarker(0, Variable.Uart_In_Buffer[30], Variable.Uart_In_Buffer[32],
				Variable.Uart_In_Buffer[34], Variable.Uart_In_Buffer[16], Variable.Uart_In_Buffer[18]);
	}

	/**
	 * 组装数据
	 */
	private static void buildSendBuffer() {

		Variable.Uart_Out_Buffer[2] = Variable.Uart_In_Buffer[0];
		Variable.Uart_Out_Buffer[3] = Variable.Uart_In_Buffer[1];
		Variable.Uart_Out_Buffer[4] = Variable.Uart_In_Buffer[2];
		Variable.Uart_Out_Buffer[5] = Variable.Uart_In_Buffer[3];
		Variable.Uart_Out_Buffer[6] = Variable.Uart_In_Buffer[4];
		Variable.Uart_Out_Buffer[7] = Variable.Uart_In_Buffer[5];

		byte[] crc16 = CRC.crc16(Variable.Uart_Out_Buffer, 2, 8);
		Variable.Uart_Out_Buffer[8] = crc16[1];
		Variable.Uart_Out_Buffer[9] = crc16[0];
	}

}
