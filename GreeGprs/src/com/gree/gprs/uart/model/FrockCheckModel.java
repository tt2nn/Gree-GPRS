package com.gree.gprs.uart.model;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.entity.Device;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.UartVariable;

/**
 * 工装检测
 * 
 * @author zhangzhuang
 *
 */
public class FrockCheckModel {

	private static final byte[] header = { (byte) 0xAA, (byte) 0x55, (byte) 0xAA, (byte) 0x55, (byte) 0x15, (byte) 0x01,
			(byte) 0x39, (byte) 0x81, (byte) 0xCA, (byte) 0x00, (byte) 0x0F };

	private static final byte[] imsiHeard = { (byte) 0x81, (byte) 0xCB, (byte) 0x00, (byte) 0x0F };

	private static final byte[] versionHeard = { (byte) 0x81, (byte) 0xC1 };

	private static int poi = 0;

	/**
	 * 将工装检测帧响应给服务器
	 */
	public static void frockCheck() {

		poi = 0;

		for (int i = 0; i < header.length; i++) {

			UartVariable.Uart_Out_Buffer[poi] = header[i];
			poi++;
		}

		/* IMEI码 */
		byte[] imei = Device.getInstance().getImei().getBytes();
		for (int i = 0; i < imei.length; i++) {

			UartVariable.Uart_Out_Buffer[poi] = imei[i];
			poi++;
		}

		/* IMSI码 */
		for (int i = 0; i < imsiHeard.length; i++) {

			UartVariable.Uart_Out_Buffer[poi] = imsiHeard[i];
			poi++;
		}
		byte[] imsi = Device.getInstance().getImsi().getBytes();
		for (int i = 0; i < imsi.length; i++) {

			UartVariable.Uart_Out_Buffer[poi] = imsi[i];
			poi++;
		}

		/* 版本信息 */
		for (int i = 0; i < versionHeard.length; i++) {

			UartVariable.Uart_Out_Buffer[poi] = versionHeard[i];
			poi++;
		}

		byte[] versionLen = Utils.intToBytes(Constant.APP_VERSION.length());
		for (int i = 0; i < versionLen.length; i++) {

			UartVariable.Uart_Out_Buffer[poi] = versionLen[i];
			poi++;
		}

		byte[] version = Constant.APP_VERSION.getBytes();
		for (int i = 0; i < version.length; i++) {

			UartVariable.Uart_Out_Buffer[poi] = version[i];
			poi++;
		}

		UartModel.build(poi);
	}

}
