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

	private static int chooseNum;
	private static boolean nextChoose;

	/**
	 * 解析
	 */
	public static void analyze() {

		UartModel.Uart_Type = UartModel.UART_TYPE_MODBUS;

		switch (UartModel.Uart_In_Buffer[10]) {

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

			chooseNum = 0;
			ControlCenter.chooseRest();
		}

		if (nextChoose) {

			nextChoose = false;
			return;
		}

		boolean needFase = chooseNum == 2 ? true : false;

		if (!DoChoose.choose(needFase)) {

			chooseNum = chooseNum == 2 ? 0 : chooseNum++;
			return;
		}

		chooseNum = 0;
		nextChoose = true;
		UartModel.nativeResponseVoting();
		UartModel.enableNativeResponse(true);

		// buildSendBuffer();
		// UartModel.build(10);

	}

	/**
	 * 点名
	 */
	private static void call() {

		if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		if (nextChoose) {

			return;
		}

		if (!UartModel.Enable_Native_Response) {

			buildSendBuffer();
			UartModel.build(10);
		}

		if (!Variable.Gprs_Init_Success) {

			return;
		}

		if (!Variable.Gprs_Choosed && DoChoose.isChooseResp()) {

			UartModel.enableNativeResponse(true);
			ControlCenter.chooseGprs();
			return;
		}

		// 判断是否是 上电是状态为选中
		if (!DoChoose.isChooseResp() && !DataCenter.Do_Power_Transmit) {

			UartModel.enableNativeResponse(true);
			DataCenter.powerTransmit();
			return;
		}

		ControlCenter.setMarker(0, UartModel.Uart_In_Buffer[30], UartModel.Uart_In_Buffer[32],
				UartModel.Uart_In_Buffer[34], UartModel.Uart_In_Buffer[16], UartModel.Uart_In_Buffer[18]);
	}

	/**
	 * 组装数据
	 */
	private static void buildSendBuffer() {

		UartModel.Uart_Out_Buffer[2] = UartModel.Uart_In_Buffer[0];
		UartModel.Uart_Out_Buffer[3] = UartModel.Uart_In_Buffer[1];
		UartModel.Uart_Out_Buffer[4] = UartModel.Uart_In_Buffer[2];
		UartModel.Uart_Out_Buffer[5] = UartModel.Uart_In_Buffer[3];
		UartModel.Uart_Out_Buffer[6] = UartModel.Uart_In_Buffer[4];
		UartModel.Uart_Out_Buffer[7] = UartModel.Uart_In_Buffer[5];

		byte[] crc16 = CRC.crc16(UartModel.Uart_Out_Buffer, 2, 8);
		UartModel.Uart_Out_Buffer[8] = crc16[1];
		UartModel.Uart_Out_Buffer[9] = crc16[0];
	}

}
