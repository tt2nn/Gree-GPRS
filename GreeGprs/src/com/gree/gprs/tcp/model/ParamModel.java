package com.gree.gprs.tcp.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.entity.Device;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.tcp.TcpModel;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * 参数
 * 
 * @author lihaotian
 *
 */
public class ParamModel {

	/**
	 * 服务器查询GPRS模块参数，GPRS回复
	 */
	public static void query() {

		Variable.Tcp_Out_Buffer[18] = (byte) 0x95;

		int poi = 19;

		String apn = "APN:" + Utils.getApn().getApnName();

		String[] res = { "PWD:" + Configure.Sms_Pwd, apn, "APNU:" + Configure.Apn_Name, "APNP:" + Configure.Apn_Pwd,
				"IP:" + Variable.Tcp_Address_Ip, "PORT:" + Variable.Tcp_Address_Port, "IPR:" + Variable.Baud_Rate,
				"WT:" + Configure.Tcp_Heart_Beat_Period, "ADM*1:" + Configure.Sms_Admin_List[0],
				"ADM*2:" + Configure.Sms_Admin_List[1], "ADM*3:" + Configure.Sms_Admin_List[2],
				"ADM*4:" + Configure.Sms_Admin_List[3], "ADM*5:" + Configure.Sms_Admin_List[4],
				"USRON*1:" + Configure.Sms_User_List[0], "USRON*2:" + Configure.Sms_User_List[1],
				"USRON*3:" + Configure.Sms_User_List[2], "USRON*4:" + Configure.Sms_User_List[3],
				"USRON*5:" + Configure.Sms_User_List[4], "USRON*6:" + Configure.Sms_User_List[5],
				"USRON*7:" + Configure.Sms_User_List[6], "USRON*8:" + Configure.Sms_User_List[7],
				"USRON*9:" + Configure.Sms_User_List[8], "USRON*10:" + Configure.Sms_User_List[9],
				"ERRT:" + (Configure.Transmit_Error_Start_Time / 60),
				"DEBT:" + (Configure.Transmit_Error_End_Time / 60),
				"BUTT:" + (Configure.Transmit_Pushkey_End_Time / 60),
				"HEALT:" + (Configure.Transmit_Change_End_Time / 60), "SIG:" + (Configure.Tcp_Sig_Period / 60),
				"ONT1:" + (Configure.Transmit_Open_Start_Time / 60), "ONT2:" + (Configure.Transmit_Open_End_Time / 60),
				"OFFT1:" + (Configure.Transmit_Close_Start_Time / 60),
				"OFFT2:" + (Configure.Transmit_Close_End_Time / 60),
				"CHECKPERIOD:" + (Configure.Transmit_Check_Period / 60),
				"CHECKTIME:" + Configure.Transmit_Check_End_Time };

		// "FTP:" + CoVariablecp_Address_Ip + ":" + Constant.Tcp_Address_Port,

		for (int i = 0; i < res.length; i++) {

			byte[] b = res[i].getBytes();

			for (int j = 0; j < b.length; j++) {

				Variable.Tcp_Out_Buffer[poi] = (byte) b[j];
				poi++;
			}

			Variable.Tcp_Out_Buffer[poi] = (byte) 0x00;
			poi++;
		}

		TcpModel.build(poi - 18, poi);

	}

	/**
	 * 修改GPRS模块参数 结果响应
	 */
	public static void update() {

		Variable.Tcp_Out_Buffer[18] = (byte) 0x95;
		Variable.Tcp_Out_Buffer[19] = (byte) 0x00;

		TcpModel.build(2, 20);
	}

	/**
	 * 服务器修改GPRS模块参数 解析
	 */
	public static void updateResponse() {

		String apn = "";
		String apnu = "";
		String apnp = "";

		String ip = "";
		String port = "";

		int poi = 19;

		int length = Utils.bytesToInt(Variable.Tcp_In_Buffer, 16, 17);

		for (int i = 19; i < 19 + length; i++) {

			// 查看所有的参数
			if (Variable.Tcp_In_Buffer[i] == (byte) 0x00) {

				String param = new String(Variable.Tcp_In_Buffer, poi, (i - poi));
				int start = param.indexOf(":", 0) + 1;

				if (start > 0) {

					String key = param.substring(0, start - 1);
					String value = param.substring(start, param.length());

					if (Utils.isNotEmpty(value)) {

						if (key.equals("PWD")) {

							Configure.setSmsPwd(value);

						} else if (key.equals("APN")) { // apn

							apn = value;

						} else if (key.equals("APNU")) { // apn name

							apnu = value;

						} else if (key.equals("APNP")) { // apn pwd

							apnp = value;

						} else if (key.equals("IP")) { // tcp ip

							ip = value;

						} else if (key.equals("PORT")) { // tcp port

							port = value;

						} else if (key.equals("WT")) { // heart beat period

							int time = Utils.stringToInt(value);
							Configure.setHbPeriodTime(time);

						} else if (key.equals("ERRT")) { // error transmit start time

							int time = Utils.stringToInt(value);
							Configure.setErrorStartTime(time * 60);

						} else if (key.equals("DEBT")) { // error transmit end time

							int time = Utils.stringToInt(value);
							Configure.setErrorEndTime(time * 60);

						} else if (key.equals("BUTT")) { // push key transmit end time

							int time = Utils.stringToInt(value);
							Configure.setPushKeyEndTime(time * 60);

						} else if (key.equals("HEALT")) { // change transmit end time

							int time = Utils.stringToInt(value);
							Configure.setChangeEndTime(time * 60);

						} else if (key.equals("SIG")) { // signal period time

							int time = Utils.stringToInt(value);
							Configure.setSigPeriodTime(time * 60);

						} else if (key.equals("ONT1")) {

							int time = Utils.stringToInt(value);
							Configure.setOpenStartTime(time * 60);

						} else if (key.equals("ONT2")) {

							int time = Utils.stringToInt(value);
							Configure.setOpenEndTime(time * 60);

						} else if (key.equals("OFFT1")) {

							int time = Utils.stringToInt(value);
							Configure.setCloseStartTime(time * 60);

						} else if (key.equals("OFFT2")) {

							int time = Utils.stringToInt(value);
							Configure.setCloseEndTime(time * 60);

						} else if (key.equals("CHECKPERIOD")) { // check transmit period time

							int time = Utils.stringToInt(value);
							Configure.setCheckPeriodTime(time * 60);

						} else if (key.equals("CHECKTIME")) { // check transmit end time

							int time = Utils.stringToInt(value);
							Configure.setCheckEndTime(time);

						} else if (key.equals("ADM*1")) {

							Configure.Sms_Admin_List[0] = value;

						} else if (key.equals("ADM*2")) {

							Configure.Sms_Admin_List[1] = value;

						} else if (key.equals("ADM*3")) {

							Configure.Sms_Admin_List[2] = value;

						} else if (key.equals("ADM*4")) {

							Configure.Sms_Admin_List[3] = value;

						} else if (key.equals("ADM*5")) {

							Configure.Sms_Admin_List[4] = value;

						} else if (key.equals("USRON*1")) {

							Configure.Sms_User_List[0] = value;

						} else if (key.equals("USRON*2")) {

							Configure.Sms_User_List[1] = value;

						} else if (key.equals("USRON*3")) {

							Configure.Sms_User_List[2] = value;

						} else if (key.equals("USRON*4")) {

							Configure.Sms_User_List[3] = value;

						} else if (key.equals("USRON*5")) {

							Configure.Sms_User_List[4] = value;

						} else if (key.equals("USRON*6")) {

							Configure.Sms_User_List[5] = value;

						} else if (key.equals("USRON*7")) {

							Configure.Sms_User_List[6] = value;

						} else if (key.equals("USRON*8")) {

							Configure.Sms_User_List[7] = value;

						} else if (key.equals("USRON*9")) {

							Configure.Sms_User_List[8] = value;

						} else if (key.equals("USRON*10")) {

							Configure.Sms_User_List[9] = value;
						}
					}
				}

				poi = i + 1;
			}
		}

		Configure.setApn(Utils.simCucc(), apn, apnu, apnp);
		Configure.setTcpAddress(Variable.Tcp_Address_Private, ip, port);

		FileWriteModel.saveSmsAdmins(Configure.Sms_Admin_List);
		FileWriteModel.saveSmsUsers(Configure.Sms_User_List);

		update();
	}

	/**
	 * 发送GPRS信号
	 */
	public static void gprsSignal() {

		Variable.Tcp_Out_Buffer[18] = (byte) 0xF4;

		// 模块型号
		Variable.Tcp_Out_Buffer[19] = (byte) 0x02;

		// 模块序列号
		byte[] imeiBytes = Device.getInstance().getImei().getBytes();
		for (int i = 20; i < 20 + imeiBytes.length; i++) {

			Variable.Tcp_Out_Buffer[i] = imeiBytes[i - 20];
		}
		Variable.Tcp_Out_Buffer[35] = (byte) 0x00;

		// 状态标记
		Variable.Tcp_Out_Buffer[36] = (byte) 0x00;
		// 故障代码
		Variable.Tcp_Out_Buffer[37] = (byte) 0x00;
		// 信号强度
		Variable.Tcp_Out_Buffer[38] = (byte) DeviceConfigure.getNetworkSignalLevel();

		TcpModel.build(21, 39);
	}

}
