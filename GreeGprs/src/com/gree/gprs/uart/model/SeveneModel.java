package com.gree.gprs.uart.model;

import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Device;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.UartVariable;
import com.gree.gprs.variable.Variable;

/**
 * 7E7E协议 model
 * 
 * @author lihaotian
 *
 */
public class SeveneModel {

	/**
	 * 解析协议
	 */
	public static void analyze() {

		if (UartVariable.Uart_In_Buffer[4] != (byte) 0x70 && UartVariable.Uart_In_Buffer[5] != (byte) 0xFF) {

			return;
		}

		switch (UartVariable.Uart_In_Buffer[8]) {

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

			ControlCenter.chooseRest();
		}

		if (!DoChoose.choose()) {

			return;
		}

		buildSendBufferHeader();
		buildSendDataHeader();
		buildServerData();

		UartVariable.Uart_Out_Buffer[94] = CRC.crc8(UartVariable.Uart_Out_Buffer, 2, 94);

		UartModel.build(95);
	}

	/**
	 * 点名
	 */
	private static void call() {

		if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		buildSendBufferHeader();
		buildSendDataHeader();
		buildServerData();

		UartVariable.Uart_Out_Buffer[94] = CRC.crc8(UartVariable.Uart_Out_Buffer, 2, 94);

		UartModel.build(95);

		if (!Variable.Gprs_Init_Success) {

			return;
		}

		// 选举上报
		if (!Variable.Gprs_Choosed && DoChoose.isChooseResp()) {

			ControlCenter.chooseGprs();
			return;
		}

		// 上电上报
		if (!DoChoose.isChooseResp() && !DataCenter.DoPowerTransmit) {

			DataCenter.powerTransmit();
			return;
		}

		ControlCenter.setMarker(Utils.byteGetBit(UartVariable.Uart_In_Buffer[10], 2),
				Utils.byteGetBit(UartVariable.Uart_In_Buffer[11], 0),
				Utils.byteGetBit(UartVariable.Uart_In_Buffer[11], 1),
				Utils.byteGetBit(UartVariable.Uart_In_Buffer[11], 2),
				Utils.byteGetBit(UartVariable.Uart_In_Buffer[10], 3),
				Utils.byteGetBit(UartVariable.Uart_In_Buffer[10], 4));
	}

	/**
	 * GPRS模块回复显示板 <br>
	 * 引导码 A5 A7 <br>
	 * 机型 00 00 <br>
	 * 目的地址 FF <br>
	 * 源地址 70 <br>
	 * 有效数据长度 固定 55
	 */
	private static void buildSendBufferHeader() {

		UartVariable.Uart_Out_Buffer[2] = (byte) 0xA5;
		UartVariable.Uart_Out_Buffer[3] = (byte) 0xA7;
		UartVariable.Uart_Out_Buffer[4] = (byte) 0x00;
		UartVariable.Uart_Out_Buffer[5] = (byte) 0x00;
		UartVariable.Uart_Out_Buffer[6] = (byte) 0xFF;
		UartVariable.Uart_Out_Buffer[7] = (byte) 0x70;
		UartVariable.Uart_Out_Buffer[8] = (byte) 0x55;
	}

	/**
	 * GPRS模块回复显示板，机组数据前面固定字节 <br>
	 * p0默认02 <br>
	 * p1-p16 模块序列号 现在没有实现 <br>
	 * p17 状态标记 <br>
	 * p18 故障代码 <br>
	 * p19 信号强度 0-31 99 表示无网络 <br>
	 * 
	 */
	private static void buildSendDataHeader() {

		// 机组数据从第6位开始
		UartVariable.Uart_Out_Buffer[9] = Constant.GPRS_MODEL;

		byte[] imeiBytes = Device.getInstance().getImei().getBytes();
		for (int i = 0; i < imeiBytes.length; i++) {

			UartVariable.Uart_Out_Buffer[i + 10] = imeiBytes[i];
		}
		UartVariable.Uart_Out_Buffer[25] = (byte) 0x00;

		// 状态标记
		if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO) {

			UartVariable.Uart_Out_Buffer[26] = (byte) 0x02;

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			UartVariable.Uart_Out_Buffer[26] = (byte) 0x00;

		} else {

			UartVariable.Uart_Out_Buffer[26] = (byte) 0x01;
		}

		// 故障代码
		switch (Variable.Gprs_Error_Type) {

		case Constant.GPRS_ERROR_TYPE_SIM:

			UartVariable.Uart_Out_Buffer[27] = (byte) 0x00;
			break;

		case Constant.GPRS_ERROR_TYPE_NETWORK:

			UartVariable.Uart_Out_Buffer[27] = (byte) 0x01;
			break;

		case Constant.GPRS_ERROR_TYPE_SERVER:

			UartVariable.Uart_Out_Buffer[27] = (byte) 0x03;
			break;

		default:
			UartVariable.Uart_Out_Buffer[27] = (byte) 0x00;
			break;
		}

		// 信号强度
		UartVariable.Uart_Out_Buffer[28] = (byte) DeviceConfigure.getNetworkSignalLevel();
	}

	/**
	 * 服务器下发有效数据
	 */
	private static void buildServerData() {

		for (int i = 29; i < 94; i++) {

			UartVariable.Uart_Out_Buffer[i] = UartVariable.Server_7E_Data[i - 29];
		}
		
		Utils.resetByteArray(UartVariable.Server_7E_Data);
	}

}
