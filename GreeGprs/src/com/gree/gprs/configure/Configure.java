package com.gree.gprs.configure;

import com.gree.gprs.constant.FileConstant;
import com.gree.gprs.file.FileReadModel;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class Configure {

	// 手机短信
	public static String Sms_Pwd = "123456";

	// 域名IP、port
	public static String Tcp_Address_Ip_Private = "192.13.182.157";
	public static String Tcp_Address_Port_Private = "7005";

	public static String Tcp_Address_Ip_Public = "157.122.146.133";
	public static String Tcp_Address_Port_Public = "7000";

	// 普通手机号白名单
	public static String[] Sms_User_List = { "18926932769", "13128540406", "13113444079", "13128541143", "18666911714",
			"07568663110", "07568522593", "07568668938", "07568669703", "07568668717" };
	// 管理员手机号白名单
	public static String[] Sms_Admin_List = { "18023036958", "13128553002", "1069800006512610", "18926932781",
			"15992681809" };

	// APN信息
	// 联通
	public static String Apn_Cucc = "greeac.gd";
	// 移动
	public static String Apn_Cmcc = "cmiotgree.gd";
	public static String Apn_Name = "GPRS";
	public static String Apn_Pwd = "GPRS";

	// 心跳间隔
	public static int Tcp_Heart_Beat_Period = 5 * 60;
	// 故障点前传输时间
	public static int Transmit_Error_Start_Time = 30 * 60;
	// 故障点后传输时间
	public static int Transmit_Error_End_Time = 5 * 60;
	// 厂家参数改变前传输结束时间
	public static int Transmit_Change_End_Time = 1 * 60;
	// 按键调试周期
	public static int Transmit_Pushkey_End_Time = 240 * 60;
	// 信号信息周期
	public static int Tcp_Sig_Period = 10 * 60;
	// 打卡周期
	public static int Transmit_Check_Period = 60 * 60;
	// 打卡时长
	public static int Transmit_Check_End_Time = 1 * 60;
	// 开机上报时间
	public static int Transmit_Open_Start_Time = 10 * 60;
	public static int Transmit_Open_End_Time = 30 * 60;
	// 关机上报时间
	public static int Transmit_Close_Start_Time = 30 * 60;
	public static int Transmit_Close_End_Time = 10 * 60;

	/**
	 * 初始化
	 */
	public static void init() {

		// sms pwd
		String pwd = FileReadModel.querySmsPwd();
		if (Utils.isNotEmpty(pwd)) {

			Sms_Pwd = pwd;
		}

		initTcpAddress(true, Tcp_Address_Ip_Private, Tcp_Address_Port_Private);
		initTcpAddress(false, Tcp_Address_Ip_Public, Tcp_Address_Port_Public);

		initApn(true, Apn_Cucc, Apn_Name, Apn_Pwd);
		initApn(false, Apn_Cmcc, Apn_Name, Apn_Pwd);

		initTime(FileReadModel.queryHbTime(), Tcp_Heart_Beat_Period);
		initTime(FileReadModel.queryErrorStartTime(), Transmit_Error_Start_Time);
		initTime(FileReadModel.queryErrorStartTime(), Transmit_Error_End_Time);
		initTime(FileReadModel.queryChangeEndTime(), Transmit_Change_End_Time);
		initTime(FileReadModel.queryPushKeyEndTime(), Transmit_Pushkey_End_Time);
		initTime(FileReadModel.querySigPeriod(), Tcp_Sig_Period);
		initTime(FileReadModel.queryCheckPeriod(), Transmit_Check_Period);
		initTime(FileReadModel.queryCheckEndTime(), Transmit_Check_End_Time);
		initTime(FileReadModel.queryOpenStartTime(), Transmit_Open_Start_Time);
		initTime(FileReadModel.queryOpenEndTime(), Transmit_Open_End_Time);
		initTime(FileReadModel.queryCloseStartTime(), Transmit_Close_Start_Time);
		initTime(FileReadModel.queryCloseEndTime(), Transmit_Close_End_Time);

		initSmsPhone(FileReadModel.querySmsUser(), Sms_User_List);
		initSmsPhone(FileReadModel.querySmsAdmin(), Sms_Admin_List);
	}

	/**
	 * set sms password
	 * 
	 * @param pwd
	 * @return
	 */
	public static boolean setSmsPwd(String pwd) {

		if (Utils.isNotEmpty(pwd) && pwd.length() == 6) {

			Sms_Pwd = pwd;
			FileWriteModel.saveSmsPwd(pwd);

			return true;
		}

		return false;
	}

	/**
	 * set heart beat period time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setHbPeriodTime(int time) {

		if (checkTime(time)) {

			Tcp_Heart_Beat_Period = time;
			FileWriteModel.saveHbTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set error transmit start time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setErrorStartTime(int time) {

		if (checkTime(time)) {

			Transmit_Error_Start_Time = time;
			FileWriteModel.saveErrorStartTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set error transmit end time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setErrorEndTime(int time) {

		if (checkTime(time)) {

			Transmit_Error_End_Time = time;
			FileWriteModel.saveErrorEndTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set change transmit end time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setChangeEndTime(int time) {

		if (checkTime(time)) {

			Transmit_Change_End_Time = time;
			FileWriteModel.saveChangeEndTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set push key transmit end time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setPushKeyEndTime(int time) {

		if (checkTime(time)) {

			Transmit_Pushkey_End_Time = time;
			FileWriteModel.savePushKeyEndTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set tcp sig period time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setSigPeriodTime(int time) {

		if (checkTime(time)) {

			Tcp_Sig_Period = time;
			FileWriteModel.saveSigPeriod(time);

			return true;
		}

		return false;
	}

	/**
	 * set check transmit period time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setCheckPeriodTime(int time) {

		if (checkTime(time)) {

			Transmit_Check_Period = time;
			FileWriteModel.saveCheckPeriod(time);

			return true;
		}

		return false;
	}

	/**
	 * set check transmit end time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setCheckEndTime(int time) {

		if (checkTime(time)) {

			Transmit_Check_End_Time = time;
			FileWriteModel.saveCheckPeriod(time);

			return true;
		}

		return false;
	}

	/**
	 * set open transmit start time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setOpenStartTime(int time) {

		if (checkTime(time)) {

			Transmit_Open_Start_Time = time;
			FileWriteModel.saveOpenStartTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set open transmit end time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setOpenEndTime(int time) {

		if (checkTime(time)) {

			Transmit_Open_End_Time = time;
			FileWriteModel.saveOpenEndTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set close transmit start time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setCloseStartTime(int time) {

		if (checkTime(time)) {

			Transmit_Close_Start_Time = time;
			FileWriteModel.saveCloseStartTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set close transmit end time
	 * 
	 * @param time
	 * @return
	 */
	public static boolean setCloseEndTime(int time) {

		if (checkTime(time)) {

			Transmit_Close_End_Time = time;
			FileWriteModel.saveCloseEndTime(time);

			return true;
		}

		return false;
	}

	/**
	 * set apn
	 * 
	 * @param cucc
	 * @param apn
	 * @param name
	 * @param pwd
	 * @return
	 */
	public static boolean setApn(boolean cucc, String apn, String name, String pwd) {

		if (Utils.isNotEmpty(apn) && apn.length() < 20 && Utils.isNotEmpty(name) && name.length() < 20
				&& Utils.isNotEmpty(pwd) && pwd.length() < 20) {

			Apn_Name = name;
			Apn_Pwd = pwd;

			if (cucc) {

				Apn_Cucc = apn;

			} else {

				Apn_Cmcc = apn;
			}

			FileWriteModel.saveApn(cucc, apn, name, pwd);
			Variable.Change_Vpn = true;

			return true;
		}

		return false;
	}

	/**
	 * set tcp address
	 * 
	 * @param priAdd
	 * @param ip
	 * @param port
	 * @return
	 */
	public static boolean setTcpAddress(boolean priAdd, String ip, String port) {

		if (Utils.isNotEmpty(ip) && ip.length() < 50 && Utils.isNotEmpty(port) && port.length() < 5) {

			if (priAdd) {

				Tcp_Address_Ip_Private = ip;
				Tcp_Address_Port_Private = port;

			} else {

				Tcp_Address_Ip_Public = ip;
				Tcp_Address_Port_Public = port;
			}

			FileWriteModel.saveServAddress(priAdd, ip, port);
		}

		return false;
	}

	/**
	 * chekc time
	 * 
	 * @param time
	 * @return
	 */
	private static boolean checkTime(int time) {

		if (time > 0 && time <= 65535) {

			return true;
		}

		return false;
	}

	/**
	 * init public and private tcp address
	 * 
	 * @param priAdd
	 * @param ip
	 * @param port
	 */
	private static void initTcpAddress(boolean priAdd, String ip, String port) {

		String address = FileReadModel.queryTcpAddress(priAdd);

		if (Utils.isNotEmpty(address)) {

			int start = 0;
			int end = address.indexOf(FileConstant.FILE_STRING_SPLIP_SYMBOL, start);

			if (end < address.length()) {

				ip = address.substring(start, end);

				start = end + 1;
				end = address.length();

				port = address.substring(start, end);
			}
		}
	}

	/**
	 * init cucc or cmcc apn
	 * 
	 * @param cucc
	 * @param apn
	 * @param user
	 * @param pwd
	 */
	private static void initApn(boolean cucc, String apn, String user, String pwd) {

		String apnString = FileReadModel.queryApn(cucc);

		if (Utils.isNotEmpty(apnString)) {

			int start = 0;
			int end = apnString.indexOf(FileConstant.FILE_STRING_SPLIP_SYMBOL, start);
			apn = apnString.substring(start, end);

			start = end + 1;
			end = apnString.indexOf(FileConstant.FILE_STRING_SPLIP_SYMBOL, start);
			user = apnString.substring(start, end);

			start = end + 1;
			end = apnString.length();
			pwd = apnString.substring(start, end);
		}
	}

	/**
	 * init all time value
	 * 
	 * @param value
	 * @param time
	 */
	private static void initTime(int value, int time) {

		if (value > 0) {

			time = value;
		}
	}

	/**
	 * 获取Sms User
	 */
	private static void initSmsPhone(String value, String[] phones) {

		if (Utils.isNotEmpty(value)) {

			int symbolPoi = 0;
			int phonePoi = 0;

			for (int i = 0; i < value.length(); i++) {

				if (value.substring(i, i + 1).equals(FileConstant.FILE_STRING_SPLIP_SYMBOL)) {

					String phone = value.substring(symbolPoi, i);
					phones[phonePoi] = phone;
					phonePoi++;
					symbolPoi = i + 1;

				} else if (i == value.length() - 1) {

					String phone = value.substring(symbolPoi, value.length());
					phones[phonePoi] = phone;
				}
			}
		}
	}

}
