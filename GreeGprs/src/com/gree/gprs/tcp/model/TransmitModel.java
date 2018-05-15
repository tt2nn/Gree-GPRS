package com.gree.gprs.tcp.model;

import java.util.Calendar;
import java.util.Date;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.tcp.TcpModel;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * 传输
 * 
 * @author lihaotian
 *
 */
public class TransmitModel {

	private static Date date = new Date();
	private static Calendar calendar = Calendar.getInstance();

	/**
	 * 开始传输 上报传输模式
	 */
	public static void start() {

		Variable.Tcp_Out_Buffer[18] = (byte) 0x91;

		if (Variable.Transmit_Cache_Type == Constant.TRANSMIT_TYPE_CHECK) {

			Variable.Tcp_Out_Buffer[19] = (byte) (Variable.Transmit_Type | Constant.TRANSMIT_TYPE_CHECK);

		} else {

			Variable.Tcp_Out_Buffer[19] = Variable.Transmit_Type;
		}

		// 获取年月日时分秒
		date.setTime(Variable.System_Time);
		calendar.setTime(date);
		Variable.Tcp_Out_Buffer[20] = (byte) (calendar.get(Calendar.YEAR) - 2000);
		Variable.Tcp_Out_Buffer[21] = (byte) (calendar.get(Calendar.MONTH) + 1);

		int localDay = calendar.get(Calendar.DATE);
		int localHour = calendar.get(Calendar.HOUR_OF_DAY) + 8;

		if (localHour > 23) {

			localDay++;
			localHour -= 24;
		}

		Variable.Tcp_Out_Buffer[22] = (byte) localDay;
		Variable.Tcp_Out_Buffer[23] = (byte) localHour;
		Variable.Tcp_Out_Buffer[24] = (byte) calendar.get(Calendar.MINUTE);
		Variable.Tcp_Out_Buffer[25] = (byte) calendar.get(Calendar.SECOND);

		TcpModel.build(8, 26);
	}

	/**
	 * 服务器响应开始传输
	 */
	public static void startResponse() {

		if (Variable.Tcp_In_Buffer[19] == (byte) 0x00) {

			DataCenter.notifyTransmit();
		}
	}

	/**
	 * GPRS响应服务器停止传输
	 */
	public static void stop() {

		Variable.Tcp_Out_Buffer[18] = (byte) 0x92;
		Variable.Tcp_Out_Buffer[19] = (byte) 0x00;

		TcpModel.build(2, 20);
	}

	/**
	 * 解析服务器停止传输请求
	 */
	public static void stopResponse() {

		DataCenter.destoryTransmit();
		stop();
	}

	/**
	 * 实时监控
	 */
	public static void monitorResponse() {

		switch (Variable.Tcp_In_Buffer[19]) {

		case (byte) 0x01:

			DataCenter.alwaysTransmit();

			break;

		case (byte) 0x02:

			DataCenter.registerCheckTransmit();

			break;
		}
	}

	/**
	 * GPRS模块上传机组数据
	 */
	public static void dataTransm(int dataLength, long time) {

		Variable.Tcp_Out_Data_Buffer[18] = (byte) 0x96;

		// 获取年月日时分秒
		date.setTime(time);
		calendar.setTime(date);
		Variable.Tcp_Out_Data_Buffer[19] = (byte) (calendar.get(Calendar.YEAR) - 2000);
		Variable.Tcp_Out_Data_Buffer[20] = (byte) (calendar.get(Calendar.MONTH) + 1);

		int localDay = calendar.get(Calendar.DATE);
		int localHour = calendar.get(Calendar.HOUR_OF_DAY) + 8;

		if (localHour > 23) {

			localDay++;
			localHour -= 24;
		}

		Variable.Tcp_Out_Data_Buffer[21] = (byte) localDay;
		Variable.Tcp_Out_Data_Buffer[22] = (byte) localHour;
		Variable.Tcp_Out_Data_Buffer[23] = (byte) calendar.get(Calendar.MINUTE);
		Variable.Tcp_Out_Data_Buffer[24] = (byte) calendar.get(Calendar.SECOND);

		TcpModel.buildForTransm(dataLength + 7, dataLength + 25);
	}

	/**
	 * GPRS解析服务器下发机组数据
	 */
	public static void dataTransmResponse() {

		int length = Utils.bytesToInt(Variable.Tcp_In_Buffer, 16, 17);

		if (length <= 7) {

			// 有效数据长度不超过7 说明没有机组数据。
			return;
		}

		int dataLength = length - 7;

		// modbus模拟量第一组数据
		if (Variable.Tcp_In_Buffer[25] == (byte) 0x11 || Variable.Tcp_In_Buffer[25] == (byte) 0x21
				|| Variable.Tcp_In_Buffer[25] == (byte) 0x31) {

			Utils.resetByteArray(Variable.Server_Data_Long_Buffer);

			for (int i = 0; i < dataLength; i++) {

				Variable.Server_Data_Long_Buffer[i] = Variable.Tcp_In_Buffer[26 + i];
			}

			if (Variable.Tcp_In_Buffer[25] == (byte) 0x11) {

				Variable.Server_Data_Change = true;
			}

			return;
		}

		// modbus模拟量第二组数据
		if (Variable.Tcp_In_Buffer[25] == (byte) 0x22 || Variable.Tcp_In_Buffer[25] == (byte) 0x32) {

			for (int i = 214; i < 214 + dataLength; i++) {

				Variable.Server_Data_Long_Buffer[i] = Variable.Tcp_In_Buffer[i - 214 + 26];
			}

			if (Variable.Tcp_In_Buffer[25] == (byte) 0x22) {

				Variable.Server_Data_Change = true;
			}

			return;
		}

		// modbus模拟量第三组数据
		if (Variable.Tcp_In_Buffer[25] == (byte) 0x33) {

			for (int i = 254; i < 254 + dataLength; i++) {

				Variable.Server_Data_Long_Buffer[i] = Variable.Tcp_In_Buffer[i - 254 + 26];
			}

			Variable.Server_Data_Change = true;

			return;
		}

		Utils.resetByteArray(Variable.Server_Data_Short_Buffer);
		// modbus开关量、7E7E
		for (int i = 0; i < dataLength; i++) {

			Variable.Server_Data_Short_Buffer[i] = Variable.Tcp_In_Buffer[i + 25];
		}
	}

}
