package com.gree.gprs.uart.model;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.UartUtils;
import com.gree.gprs.variable.UartVariable;
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

		UartVariable.Uart_Type = UartModel.UART_TYPE_MODBUS;

		switch (UartVariable.Uart_In_Buffer[10]) {

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

			UartUtils.enableNativeResponse(false);
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

		if (!UartVariable.Enable_Native_Response) {

			buildSendBuffer();
			UartModel.build(10);
		}

		if (!Variable.Gprs_Init_Success) {

			return;
		}

		if (!Variable.Gprs_Choosed && DoChoose.isChooseResp()) {

			UartUtils.enableNativeResponse(true);
			ControlCenter.chooseGprs();
			return;
		}

		// 判断是否是 上电是状态为选中
		if (!DoChoose.isChooseResp() && !DataCenter.Do_Power_Transmit) {

			UartUtils.enableNativeResponse(true);
			DataCenter.powerTransmit();
			return;
		}

		ControlCenter.setMarker(0, UartVariable.Uart_In_Buffer[30], UartVariable.Uart_In_Buffer[32],
				UartVariable.Uart_In_Buffer[34], UartVariable.Uart_In_Buffer[16], UartVariable.Uart_In_Buffer[18]);
	}

	/**
	 * 组装数据
	 */
	private static void buildSendBuffer() {

		UartVariable.Uart_Out_Buffer[2] = UartVariable.Uart_In_Buffer[0];
		UartVariable.Uart_Out_Buffer[3] = UartVariable.Uart_In_Buffer[1];
		UartVariable.Uart_Out_Buffer[4] = UartVariable.Uart_In_Buffer[2];
		UartVariable.Uart_Out_Buffer[5] = UartVariable.Uart_In_Buffer[3];
		UartVariable.Uart_Out_Buffer[6] = UartVariable.Uart_In_Buffer[4];
		UartVariable.Uart_Out_Buffer[7] = UartVariable.Uart_In_Buffer[5];

		byte[] crc16 = CRC.crc16(UartVariable.Uart_Out_Buffer, 2, 8);
		UartVariable.Uart_Out_Buffer[8] = crc16[1];
		UartVariable.Uart_Out_Buffer[9] = crc16[0];
	}

}
