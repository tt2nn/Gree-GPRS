package com.gree.gprs.tcp.model;

import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.tcp.TcpModel;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * 时间
 * 
 * @author lihaotian
 *
 */
public class TimeModel {

	/**
	 * 心跳
	 */
	public static void heart() {

		Variable.Tcp_Out_Buffer[18] = (byte) 0xF3;

		TcpModel.build(1, 19);
	}

	/**
	 * 心跳响应
	 */
	public static void heartResponse() {

		int year = (Variable.Tcp_In_Buffer[19] & 0xFF) + 2000;
		int month = Variable.Tcp_In_Buffer[20] & 0xFF;
		int date = Variable.Tcp_In_Buffer[21] & 0xFF;
		int hour = Variable.Tcp_In_Buffer[22] & 0xFF;
		int min = Variable.Tcp_In_Buffer[23] & 0xFF;
		int sec = Variable.Tcp_In_Buffer[24] & 0xFF;

		Variable.System_Time = Utils.getTime(year, month, date, hour, min, sec);
		Variable.Heart_Beat_Time = Variable.System_Time;
		Variable.System_Delta_Time = Variable.System_Time - System.currentTimeMillis();

		Logger.log("Tcp Heart Time", Variable.System_Time + "");

		ControlCenter.heartBeatResp();
	}

	/**
	 * 静默时间 响应
	 */
	public static void stopTime() {

		Variable.Tcp_Out_Buffer[18] = (byte) 0x98;
		Variable.Tcp_Out_Buffer[19] = (byte) 0x00;

		TcpModel.build(1, 20);
	}

	/**
	 * 服务器下发静默时间
	 */
	public static void stopTimeResponse() {

		int year = (Variable.Tcp_In_Buffer[19] & 0xFF) + 2000;
		int month = Variable.Tcp_In_Buffer[20] & 0xFF;
		int date = Variable.Tcp_In_Buffer[21] & 0xFF;
		int hour = Variable.Tcp_In_Buffer[22] & 0xFF;
		int min = Variable.Tcp_In_Buffer[23] & 0xFF;
		int sec = Variable.Tcp_In_Buffer[24] & 0xFF;

		// 如果下发的时间 是 2000-1-1 0-0-0 清空静默时间
		if (year == 2000 && month == 1 && date == 1 && hour == 0 && min == 0 && sec == 0) {

			Variable.Stop_Time = 0;
			stopTime();

			return;
		}

		Variable.Stop_Time = Utils.getTime(year, month, date, hour, min, sec);

		if (Variable.System_Time < Variable.Stop_Time) {

			// 如果当前时间 小于静默时间 则 停止传输
			DataCenter.destoryTransmit();
		}

		stopTime();
	}

}
