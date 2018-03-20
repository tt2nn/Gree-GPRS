package com.gree.gprs.tcp.model;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.entity.Device;
import com.gree.gprs.tcp.TcpModel;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * 登录
 * 
 * @author lihaotian
 *
 */
public class LoginModel {

	/**
	 * 登录
	 */
	public static void login() {

		Variable.Tcp_Out_Buffer[18] = (byte) 0x90;

		byte[] macCheck = Utils.intToBytes((Variable.Gprs_Mac[0] & 0xFF) * 3 + (Variable.Gprs_Mac[1] & 0xFF) * 6
				+ (Variable.Gprs_Mac[2] & 0xFF) * 0 + (Variable.Gprs_Mac[3] & 0xFF) * 9
				+ (Variable.Gprs_Mac[4] & 0xFF) * 7 + (Variable.Gprs_Mac[5] & 0xFF) * 4
				+ (Variable.Gprs_Mac[6] & 0xFF) * 10);

		// 源地址检验码
		Variable.Tcp_Out_Buffer[19] = macCheck[0];
		Variable.Tcp_Out_Buffer[20] = macCheck[1];

		// 版本号
		Variable.Tcp_Out_Buffer[21] = Constant.APP_VERSION_FIRST;
		Variable.Tcp_Out_Buffer[22] = Constant.APP_VERSKON_SECOND;

		// IMEI
		byte[] imeiBytes = Device.getInstance().getImei().getBytes();
		for (int i = 23; i < 38; i++) {

			Variable.Tcp_Out_Buffer[i] = imeiBytes[i - 23];
		}
		Variable.Tcp_Out_Buffer[38] = (byte) 0x00;

		byte[] mncBytes = Utils.intToBytes(Device.getInstance().getMnc());
		Variable.Tcp_Out_Buffer[39] = mncBytes[0];
		Variable.Tcp_Out_Buffer[40] = mncBytes[1];

		byte[] lacBytes = Utils.intToBytes(Device.getInstance().getLac());
		Variable.Tcp_Out_Buffer[41] = lacBytes[0];
		Variable.Tcp_Out_Buffer[42] = lacBytes[1];

		byte[] cidBytes = Utils.intToBytes(Device.getInstance().getMcc());
		Variable.Tcp_Out_Buffer[43] = cidBytes[0];
		Variable.Tcp_Out_Buffer[44] = cidBytes[1];

		Variable.Tcp_Out_Buffer[45] = (byte) 0x00;

		// 手机序列号
		byte[] ccidBytes = Device.getInstance().getIccid().getBytes();
		for (int i = 46; i < 66; i++) {

			if (i - 46 < ccidBytes.length) {

				Variable.Tcp_Out_Buffer[i] = ccidBytes[i - 46];

			} else {

				Variable.Tcp_Out_Buffer[i] = (byte) 0x00;
			}
		}
		Variable.Tcp_Out_Buffer[66] = (byte) 0x00;

		// 模块型号
		Variable.Tcp_Out_Buffer[67] = Constant.GPRS_MODEL;

		TcpModel.build(50, 68);
	}

	/**
	 * 登录响应
	 */
	public static void loginResponse() {

		for (int i = 9; i < 16; i++) {

			Variable.Server_Mac[i - 9] = Variable.Tcp_In_Buffer[i];
		}

		ControlCenter.heartBeat();
	}

}
