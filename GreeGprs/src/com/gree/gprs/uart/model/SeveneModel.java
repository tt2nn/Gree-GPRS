package com.gree.gprs.uart.model;

import java.io.IOException;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Device;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;
import com.joshvm.greenland.io.modbus.DTU7E7EController;
import com.joshvm.greenland.io.modbus.DTU7E7EDataBuffer;

/**
 * 7E7E协议 model
 * 
 * @author lihaotian
 *
 */
public class SeveneModel {

	private static int chooseNum;
	private static boolean nextChoose;

	private static byte[] data = new byte[85];
	private static DTU7E7EDataBuffer dtu7e7eDataBuffer;

	/**
	 * init
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {

		dtu7e7eDataBuffer = DTU7E7EController.getDTU7E7EController().allocateDataBuffer(85, false);

		byte[] imeiBytes = Device.getInstance().getImei().getBytes();
		data[0] = Variable.Gprs_Model;

		for (int i = 0; i < imeiBytes.length; i++) {

			data[1 + i] = imeiBytes[i];
		}
		data[16] = (byte) 0x00;

		// 状态标记
		data[17] = (byte) 0x00;

		// 故障代码
		data[18] = (byte) 0x00;

		// 信号强度
		data[19] = (byte) Variable.Network_Signal_Level;

		dtu7e7eDataBuffer.setVotingFrameFilter0(8, Constant.FUNCTION_CHOOSE);
		dtu7e7eDataBuffer.setDefaultValues(data);
		dtu7e7eDataBuffer.setVolatile(20, 65, true);
		dtu7e7eDataBuffer.update(0, data, 0, data.length);

		// nextChoose = true;
		// UartModel.nativeResponseVoting(UartModel.UART_TYPE_7E);
	}

	/**
	 * Receive Data From Server
	 * 
	 * @param data
	 * @param length
	 */
	public static void receiveServerData(byte[] serverData, int length) {

		for (int i = 0; i < length; i++) {

			data[20 + i] = serverData[i];
		}

		dtu7e7eDataBuffer.update(20, data, 20, length);
	}

	/**
	 * Update Data
	 */
	public static void updateData() {

		// 状态标记
		if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO) {

			data[17] = (byte) 0x02;

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			data[17] = (byte) 0x00;

		} else {

			data[17] = (byte) 0x01;
		}

		// 故障代码
		switch (Variable.Gprs_Error_Type) {

		case Constant.GPRS_ERROR_TYPE_SIM:

			data[18] = (byte) 0x00;
			break;

		case Constant.GPRS_ERROR_TYPE_NETWORK:

			data[18] = (byte) 0x01;
			break;

		case Constant.GPRS_ERROR_TYPE_SERVER:

			data[18] = (byte) 0x03;
			break;

		default:
			data[18] = (byte) 0x00;
			break;
		}

		// 信号强度
		UartModel.Uart_Out_Buffer[19] = (byte) Variable.Network_Signal_Level;

		dtu7e7eDataBuffer.update(17, data, 17, 3);
	};

	/**
	 * 解析协议
	 */
	public static void analyze() {

		UartModel.Uart_Type = UartModel.UART_TYPE_7E;

		if (UartModel.Uart_In_Buffer[4] != (byte) 0x70 && UartModel.Uart_In_Buffer[5] != (byte) 0xFF) {

			return;
		}

		switch (UartModel.Uart_In_Buffer[8]) {

		case (byte) Constant.FUNCTION_CALL:// 点名

			call();

			break;

		case (byte) Constant.FUNCTION_CHOOSE:// 选举

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
			UartModel.nativeResponseSelect(UartModel.UART_TYPE_7E);
			return;
		}

		boolean needFase = chooseNum == 2 ? true : false;

		if (!DoChoose.choose(needFase)) {

			chooseNum = chooseNum == 2 ? 0 : chooseNum++;
			return;
		}

		chooseNum = 0;
		nextChoose = true;
		UartModel.nativeResponseVoting(UartModel.UART_TYPE_7E);
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

		// 选举上报
		if (!Variable.Gprs_Choosed && DoChoose.isChooseResp()) {

			UartModel.nativeResponseSelect(UartModel.UART_TYPE_7E);
			ControlCenter.chooseGprs();
			return;
		}

		// 上电上报
		if (!DoChoose.isChooseResp() && !DataCenter.Do_Power_Transmit) {

			UartModel.nativeResponseSelect(UartModel.UART_TYPE_7E);
			DataCenter.powerTransmit();
			return;
		}

		ControlCenter.setMarker(Utils.byteGetBit(UartModel.Uart_In_Buffer[10], 2),
				Utils.byteGetBit(UartModel.Uart_In_Buffer[11], 0), Utils.byteGetBit(UartModel.Uart_In_Buffer[11], 1),
				Utils.byteGetBit(UartModel.Uart_In_Buffer[11], 2), Utils.byteGetBit(UartModel.Uart_In_Buffer[10], 3),
				Utils.byteGetBit(UartModel.Uart_In_Buffer[10], 4));
	}

}
