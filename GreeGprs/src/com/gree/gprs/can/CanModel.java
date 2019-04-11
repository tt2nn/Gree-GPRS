package com.gree.gprs.can;

import com.gree.gprs.Boot;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Device;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.uart.UartServer;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class CanModel implements Runnable {

	public static byte[] Can_Data_In_Buffer = new byte[128];
	public static byte[] Can_Data_Out_Buffer = new byte[128];
	public static int Can_Data_Length = 0;

	private static int time = 0;
	private static int callPeriod = 0;

	private static int Can_Type = 0;
	private static final int CAN_TYPE1 = 1;
	private static final int CAN_TYPE2 = 2;
	private static byte[] Can_Header1 = { (byte) 0x14, (byte) 0x3F, (byte) 0xE0, (byte) 0x9E };
	private static byte[] Can_Header2 = { (byte) 0x14, (byte) 0xFF, (byte) 0xFF, (byte) 0x9E };

	public static byte[] Server_Can_Data = new byte[1024];
	public static int Receive_Server_Data_Length = 0;

	private boolean canResp = false;
	private int chooseNum = 0;
	private int checkNum = 0;

	public void analyze() {

		if (Can_Data_In_Buffer[0] == (byte) 0x14 && Can_Data_In_Buffer[1] == (byte) 0x3F
				&& Can_Data_In_Buffer[2] == (byte) 0xE0
				&& (Can_Data_In_Buffer[3] == (byte) 0x9C || Can_Data_In_Buffer[3] == (byte) 0x1C)) {

			// 选举 or 点名
			Logger.log("Can1 Get Message", CanModel.Can_Data_In_Buffer, 0, Can_Data_Length);

			Can_Type = CAN_TYPE1;

			if (Can_Data_In_Buffer[9] == Constant.FUNCTION_CALL) {

				call();

			} else if (Can_Data_In_Buffer[9] == Constant.FUNCTION_CHOOSE) {

				choose();
			}

		} else if (Can_Data_In_Buffer[0] == (byte) 0x14 && Can_Data_In_Buffer[1] == (byte) 0xFF
				&& Can_Data_In_Buffer[2] == (byte) 0xFF
				&& (Can_Data_In_Buffer[3] == (byte) 0x9C || Can_Data_In_Buffer[3] == (byte) 0x1C)) {

			// 选举 or 点名
			Logger.log("Can2 Get Message", CanModel.Can_Data_In_Buffer, 0, Can_Data_Length);

			Can_Type = CAN_TYPE2;

			if (Can_Data_In_Buffer[9] == Constant.FUNCTION_CALL) {

				call();

			} else if (Can_Data_In_Buffer[9] == Constant.FUNCTION_CHOOSE) {

				choose();
			}

		} else if (Can_Data_In_Buffer[0] == (byte) 0x14 && Can_Data_In_Buffer[1] == (byte) 0x3F
				&& Can_Data_In_Buffer[2] == (byte) 0xC0
				&& (Can_Data_In_Buffer[3] == (byte) 0x9E || Can_Data_In_Buffer[3] == (byte) 0x1E)) {
			// 工装检测

			checkNum++;
			return;
		}
		// else if (Can_Data_In_Buffer[0] == (byte) 0x90 && Can_Data_In_Buffer[1] ==
		// (byte) 0x00
		// && Can_Data_In_Buffer[2] == (byte) 0xE0
		// && (Can_Data_In_Buffer[3] == (byte) 0x9E || Can_Data_In_Buffer[3] == (byte)
		// 0x1E)) {
		//
		// Can_Data_In_Buffer[9] = (byte) 0x67;
		// }

		DataCenter.saveDataBuffer(Can_Data_In_Buffer, Can_Data_Length);
	}

	/**
	 * 选举
	 */
	private void choose() {

		if (Variable.Gprs_Choosed) {

			canResp = false;
			chooseNum = 0;
			ControlCenter.chooseRest();
		}

		boolean needFase = chooseNum == 2 ? true : false;

		if (!DoChoose.choose(needFase)) {

			chooseNum = chooseNum == 2 ? 0 : chooseNum++;
			return;
		}

		chooseNum = 0;
		buildCallMess();
	}

	/**
	 * 点名
	 */
	private void call() {

		if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		// 选举上报
		if (!Variable.Gprs_Choosed && DoChoose.isChooseResp()) {

			resetCanTransmit();
			canResp = true;
			ControlCenter.chooseGprs();
			return;
		}

		// 上电上报
		if (!DoChoose.isChooseResp() && !DataCenter.Do_Power_Transmit) {

			if (!canResp) {

				buildCallMess();
				resetCanTransmit();
				canResp = true;
			}

			DataCenter.powerTransmit();
			return;
		}

		ControlCenter.setMarker(Can_Data_In_Buffer[12], Can_Data_In_Buffer[10], Can_Data_In_Buffer[11],
				Can_Data_In_Buffer[13], Can_Data_In_Buffer[14], Can_Data_In_Buffer[15]);
	}

	/**
	 * 响应0f and f0 帧
	 */
	private static void buildCallMess() {

		Can_Data_Out_Buffer[8] = (byte) 0x07;
		Can_Data_Out_Buffer[9] = Variable.Gprs_Model;

		// 状态标记
		if (Variable.Gprs_Error_Type != Constant.GPRS_ERROR_TYPE_NO) {

			Can_Data_Out_Buffer[10] = (byte) 0x02;

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			Can_Data_Out_Buffer[10] = (byte) 0x00;

		} else {

			Can_Data_Out_Buffer[10] = (byte) 0x01;
		}

		// 故障代码
		switch (Variable.Gprs_Error_Type) {

		case Constant.GPRS_ERROR_TYPE_SIM:

			Can_Data_Out_Buffer[11] = (byte) 0x00;
			break;

		case Constant.GPRS_ERROR_TYPE_DIAL:

			Can_Data_Out_Buffer[11] = (byte) 0x02;
			break;

		case Constant.GPRS_ERROR_TYPE_NETWORK:

			Can_Data_Out_Buffer[11] = (byte) 0x01;
			break;

		case Constant.GPRS_ERROR_TYPE_SERVER:

			Can_Data_Out_Buffer[11] = (byte) 0x03;
			break;

		default:
			Can_Data_Out_Buffer[11] = (byte) 0x00;
			break;
		}

		// 信号强度
		Can_Data_Out_Buffer[12] = (byte) DeviceConfigure.getNetworkSignalLevel();
		Can_Data_Out_Buffer[13] = Variable.Gprs_Net_Generation;

		buildMessage(true, 6);
	}

	/**
	 * send can data
	 */
	public static void buildMessage(boolean defultId, int length) {

		if (defultId) {

			if (Can_Type == CAN_TYPE1) {

				for (int i = 0; i < Can_Header1.length; i++) {

					Can_Data_Out_Buffer[i] = Can_Header1[i];
				}

			} else if (Can_Type == CAN_TYPE2) {

				for (int i = 0; i < Can_Header2.length; i++) {

					Can_Data_Out_Buffer[i] = Can_Header2[i];
				}
			}
		}

		Can_Data_Out_Buffer[4] = (byte) length;
		Can_Data_Out_Buffer[5] = (byte) 0x00;
		Can_Data_Out_Buffer[6] = (byte) 0x00;
		Can_Data_Out_Buffer[7] = (byte) 0x00;

		for (int i = 8 + length; i < 16; i++) {

			Can_Data_Out_Buffer[i] = (byte) 0x00;
		}

		// CanServer.sendData(16);

		for (int i = 0; i < Can_Data_Out_Buffer.length; i++) {

			UartModel.Uart_Out_Buffer[i + 2] = Can_Data_Out_Buffer[i];
		}

		UartModel.Uart_Out_Buffer[0] = (byte) 0xFA;
		UartModel.Uart_Out_Buffer[1] = (byte) 0xFB;
		UartModel.Uart_Out_Buffer[18] = (byte) 0xFC;
		UartModel.Uart_Out_Buffer[19] = (byte) 0xFD;

		UartServer.sendData(20);
	}

	private void resetCanTransmit() {

		time = 0;
		callPeriod = 0;
	}

	public void run() {

		while (Boot.Gprs_Running) {

			try {

				long sTime = Variable.System_Time;

				if (Variable.Gprs_Choosed && canResp) {

					if (callPeriod == 10) {

						callPeriod = 0;
						buildCallMess();
					}

					if (time == 0 || time == 1 || time == 2 || time == 60 || time == 61 || time == 62 || time == 120
							|| time == 121 || time == 122) {

						sendGprsMessage();
					}

					if (Receive_Server_Data_Length > 0) {

						sendTcpData(Server_Can_Data, 0, Receive_Server_Data_Length);
						Receive_Server_Data_Length = 0;
					}

					callPeriod++;

					if (time < 150) {

						time++;
					}
				}

				if (checkNum > 0) {

					sendGprsMessage();
					checkNum--;
				}

				long sleepTime = Variable.System_Time - sTime;
				sleepTime = 1000 - sleepTime > 0 ? 1000 - sleepTime : 0;
				Thread.sleep(sleepTime);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send Gprs Message
	 * 
	 * @throws InterruptedException
	 */
	private static void sendGprsMessage() throws InterruptedException {

		final byte[] iccid = Utils.isNotEmpty(Device.getInstance().getIccid())
				? Device.getInstance().getIccid().getBytes()
				: new byte[20];
		final byte[] imei = Device.getInstance().getImei().getBytes();

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x0E;
		for (int i = 0; i < 7; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = iccid[i];
		}
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x15;
		for (int i = 0; i < 7; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = iccid[7 + i];
		}
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x1C;
		for (int i = 0; i < 6; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = iccid[14 + i];
		}
		CanModel.Can_Data_Out_Buffer[15] = imei[0];
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x23;
		for (int i = 0; i < 7; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = imei[1 + i];
		}
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x2A;
		for (int i = 0; i < 7; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = imei[8 + i];
		}
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x31;
		CanModel.Can_Data_Out_Buffer[9] = Variable.App_Version_First;
		CanModel.Can_Data_Out_Buffer[10] = Variable.App_Version_Second;
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);
	}

	/**
	 * Send Server Message
	 * 
	 * @param data
	 * @param start
	 * @param length
	 * @throws InterruptedException
	 */
	private void sendTcpData(byte[] data, int start, int length) throws InterruptedException {

		while (start < length) {

			if (data[start] == (byte) 0xAA) {

				start += 1;

				if (data[start] == (byte) 0xAA) {

					start += 1;

					int len = (data[start] ^ (byte) 0x80);
					start += 1;

					if (len <= 8 && len > 0) {

						byte[] canIds = getCanIds(data, start);

						for (int i = 0; i < 4; i++) {

							Can_Data_Out_Buffer[i] = canIds[i];
						}

						for (int i = 0; i < len; i++) {

							Can_Data_Out_Buffer[8 + i] = data[start + 4 + i];
						}

						start = start + 4 + len + 2;

						buildMessage(false, len);

						Thread.sleep(50);

						continue;
					}
				}
			}

			start += 1;
		}
	}

	/**
	 * Get CanIds
	 * 
	 * @param data
	 * @param start
	 * @return
	 */
	private byte[] getCanIds(byte[] data, int start) {

		byte[] canIds = new byte[4];

		byte b1 = data[start + 3];
		if (Utils.byteGetBit(data[start + 2], 0) == 1) {

			b1 = (byte) (b1 | (byte) 0x80);
		}
		canIds[0] = b1;

		byte b2 = (byte) (data[start + 2] >> 1);
		if (Utils.byteGetBit(data[start + 1], 0) == 1) {

			b2 = (byte) (b2 | (byte) 0x40);
		}
		if (Utils.byteGetBit(data[start + 1], 1) == 1) {

			b2 = (byte) (b2 | (byte) 0x80);
		}
		canIds[1] = b2;

		byte b3 = (byte) (data[start + 1] >> 2);
		byte b4 = (byte) (data[start] << 5);
		canIds[2] = (byte) (b3 | b4);

		byte b5 = (byte) (data[start] >> 3);
		if (Utils.byteGetBit(b5, 7) == 1) {

			b5 = (byte) (b5 ^ (byte) 0x60);

		} else {

			b5 = (byte) (b5 & (byte) 0x80);
		}

		canIds[3] = b5;

		return canIds;
	}

}
