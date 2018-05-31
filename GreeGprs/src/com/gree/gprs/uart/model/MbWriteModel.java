package com.gree.gprs.uart.model;

import java.io.IOException;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.variable.Variable;
import com.joshvm.greenland.io.modbus.HoldingRegistersStack;
import com.joshvm.greenland.io.modbus.ModbusController;

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
	 * init
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {

		HoldingRegistersStack holdingRegistersStack = ModbusController.getModbusController().allocateHoldingRegisters(0,
				0, false);
		holdingRegistersStack.setVotingFrameFilter0(10, Constant.FUNCTION_CHOOSE);
	}

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
			UartModel.nativeResponseSelect();
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

		if (!Variable.Gprs_Init_Success) {

			return;
		}

		if (!Variable.Gprs_Choosed && DoChoose.isChooseResp()) {

			UartModel.nativeResponseSelect();
			ControlCenter.chooseGprs();
			return;
		}

		// 判断是否是 上电是状态为选中
		if (!DoChoose.isChooseResp() && !DataCenter.Do_Power_Transmit) {

			UartModel.nativeResponseSelect();
			DataCenter.powerTransmit();
			return;
		}

		ControlCenter.setMarker(0, UartModel.Uart_In_Buffer[30], UartModel.Uart_In_Buffer[32],
				UartModel.Uart_In_Buffer[34], UartModel.Uart_In_Buffer[16], UartModel.Uart_In_Buffer[18]);
	}

}
