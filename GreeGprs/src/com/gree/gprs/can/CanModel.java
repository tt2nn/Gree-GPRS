package com.gree.gprs.can;

import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Device;
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

	private static byte[] Can_Header = { (byte) 0x14, (byte) 0x3F, (byte) 0xE0, (byte) 0x9E };

	public static byte[] Server_Can_Data = new byte[256];
	public static int Receive_Server_Data_Length = 0;

	public static void analyze() {

		if (Can_Data_In_Buffer[0] == (byte) 0x14 && Can_Data_In_Buffer[1] == (byte) 0x3F
				&& Can_Data_In_Buffer[2] == (byte) 0xE0 && Can_Data_In_Buffer[3] == (byte) 0x9C
				&& Can_Data_In_Buffer[4] == (byte) 0x06) {

			Logger.log("Can Get Message", CanModel.Can_Data_In_Buffer, 0, Can_Data_Length);

			if (Can_Data_In_Buffer[9] == Constant.FUNCTION_CALL) {

				call();

			} else if (Can_Data_In_Buffer[9] == Constant.FUNCTION_CHOOSE) {

				choose();
			}
		}

		DataCenter.saveDataBuffer(Can_Data_In_Buffer, Can_Data_Length);
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

		buildCallMess();
		time = 0;
		callPeriod = 0;
		new Thread(new CanModel()).start();
	}

	/**
	 * 点名
	 */
	private static void call() {

		if (!Variable.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		if (!Variable.Gprs_Init_Success) {

			return;
		}

		// 选举上报
		if (!Variable.Gprs_Choosed && DoChoose.isChooseResp()) {

			buildCallMess();
			time = 0;
			callPeriod = 0;
			new Thread(new CanModel()).start();

			ControlCenter.chooseGprs();
			return;
		}

		// 上电上报
		if (!DoChoose.isChooseResp() && !DataCenter.Do_Power_Transmit) {

			DataCenter.powerTransmit();
			return;
		}

		ControlCenter.setMarker(Can_Data_In_Buffer[12], Can_Data_In_Buffer[10], Can_Data_In_Buffer[11],
				Can_Data_In_Buffer[13], 0, 0);
	}

	/**
	 * 响应0f and f0 帧
	 */
	private static void buildCallMess() {

		Can_Data_Out_Buffer[8] = (byte) 0x07;
		Can_Data_Out_Buffer[9] = (byte) 0x05;

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
		Can_Data_Out_Buffer[13] = (byte) 0x04;

		buildMessage(true, 6);
	}

	/**
	 * send can data
	 */
	public static void buildMessage(boolean defultId, int length) {

		if (defultId) {

			for (int i = 0; i < Can_Header.length; i++) {

				Can_Data_Out_Buffer[i] = Can_Header[i];
			}
		}

		Can_Data_Out_Buffer[4] = (byte) length;
		Can_Data_Out_Buffer[5] = (byte) 0x00;
		Can_Data_Out_Buffer[6] = (byte) 0x00;
		Can_Data_Out_Buffer[7] = (byte) 0x00;

		for (int i = 8 + length; i < 16; i++) {

			Can_Data_Out_Buffer[i] = (byte) 0x00;
		}

		CanServer.sendData(16);
	}

	public void run() {

		while (Variable.Gprs_Choosed && Variable.Gprs_Init_Success) {

			try {

				long sTime = Variable.System_Time;

				if (callPeriod == 10) {

					callPeriod = 0;
					buildCallMess();
				}

				if (time == 0 || time == 1 || time == 2 || time == 60 || time == 61 || time == 62 || time == 120
						|| time == 121 || time == 122) {

					sendGprsMessage();
					sendGprsMessage();
					sendGprsMessage();
				}

				if (Can_Data_Length > 0) {

					sendTcpData(Server_Can_Data, 0, Can_Data_Length);
					Can_Data_Length = 0;
				}

				callPeriod++;
				time++;

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

		final byte[] iccid = Device.getInstance().getIccid().getBytes();
		final byte[] imei = Device.getInstance().getImei().getBytes();

		// try {

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x14;
		for (int i = 0; i < 7; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = iccid[19 - i];
		}
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x21;
		for (int i = 0; i < 7; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = iccid[12 - i];
		}
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x28;
		for (int i = 0; i < 6; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = iccid[5 - i];
		}
		CanModel.Can_Data_Out_Buffer[15] = imei[14];
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x35;
		for (int i = 0; i < 7; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = imei[13 - i];
		}
		CanModel.buildMessage(true, 8);
		Thread.sleep(50);

		CanModel.Can_Data_Out_Buffer[8] = (byte) 0x42;
		for (int i = 0; i < 7; i++) {

			CanModel.Can_Data_Out_Buffer[9 + i] = imei[6 - i];
		}
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

		while (start < start + length) {

			if (data[start] == (byte) 0xAA) {

				start += 1;

				if (data[start] == (byte) 0xAA) {

					start += 1;

					int len = Utils.byteGetBit(data[start], 0);
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

			b2 = (byte) (b2 | (byte) 0x80);
		}
		canIds[1] = b2;

		byte b3 = (byte) (data[start + 1] >> 1);
		if (Utils.byteGetBit(data[start], 0) == 1) {

			b3 = (byte) (b3 | (byte) 0x80);
		}
		canIds[2] = b3;

		byte b4 = (byte) (data[start] >> 3);
		if (Utils.byteGetBit(b4, 7) == 1) {

			b4 = (byte) (b4 ^ (byte) 0x60);

		} else {

			b4 = (byte) (b4 & (byte) 0x80);
		}

		canIds[3] = b4;

		return canIds;
	}

}
