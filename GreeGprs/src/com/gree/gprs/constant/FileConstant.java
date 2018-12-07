package com.gree.gprs.constant;

public class FileConstant {

	// 短信密码文件名
	public static final String FILE_NAME_SMS_PASSWORD = "SmsPassword";
	// 存储上报模式
	public static final String FILE_NAME_DATA_TRANSM = "DataTransm";
	// 模块选中状态
	public static final String FILE_NAME_GPRS_CHOOSED = "GprsChoosed";
	// 模块TCP连接地址
	public static final String FILE_NAME_TCP_ADDRESS_PUBLIC = "TcpAddressPublic";
	public static final String FILE_NAME_TCP_ADDRESS_PRIVATE = "TcpAddressPrivate";
	// 心跳时间
	public static final String FILE_NAME_TCP_HEART_BEAT_PERIOD = "TcpHeartBeatPeriod";
	// 故障点前传输时间
	public static final String FILE_NAME_TRANSMIT_ERROR_START_TIME = "TransmitErrorStartTime";
	// 故障点后传输时间
	public static final String FILE_NAME_TRANSMIT_ERROR_END_TIME = "TransmitErrorEndTime";
	// 厂家参数改变前传输时间
	public static final String FILE_NAME_TRANSMIT_CHANGE_START_TIME = "TransmitChangeStartTime";
	// 按键调试周期
	public static final String FILE_NAME_TRANSMIT_PUSHKEY_END_TIME = "TransmitPushkeyEndTime";
	// 信号信息周期
	public static final String FILE_NAME_TCP_SIG_PERIOD = "TcpSigPeriod";
	// 打卡周期
	public static final String FILE_NAME_TRANSMIT_CHECK_PERIOD = "TransmitCheckPeriod";
	// 打卡时长
	public static final String FILE_NAME_TRANSMIT_CHECK_END_TIME = "TransmitCheckEndTime";
	// APN 移动
	public static final String FILE_NAME_APN_CMCC = "File_Name_Apn_Cmcc";
	// APN 联通
	public static final String FILE_NAME_APN_CUCC = "File_Name_Apn_Cucc";
	// sms user
	public static final String FILE_NAME_SMS_USER = "File_Name_Sms_User";
	// sms admin
	public static final String FILE_NAME_SMS_ADMIN = "File_Name_Sms_Admin";
	// Transmit Open File
	public static final String FILE_NAME_OPEN_START_TIME = "File_Name_Open_Start_Time";
	public static final String FILE_NAME_OPEN_END_TIME = "File_Name_Open_End_Time";
	// Transmit Close File
	public static final String FILE_NAME_CLOSE_START_TIME = "File_Name_Close_Start_Time";
	public static final String FILE_NAME_CLOSE_END_TIME = "File_Name_Close_End_Time";
	// derep period file name
	public static final String FILE_NAME_DEREP_PERIOD_TIME = "File_Name_Derep_Period_Time";

	public static final String[] FILE_NAME_ARRAY = { FILE_NAME_SMS_PASSWORD, FILE_NAME_DATA_TRANSM,
			FILE_NAME_GPRS_CHOOSED, FILE_NAME_TCP_ADDRESS_PUBLIC, FILE_NAME_TCP_ADDRESS_PRIVATE,
			FILE_NAME_TCP_HEART_BEAT_PERIOD, FILE_NAME_TRANSMIT_ERROR_START_TIME, FILE_NAME_TRANSMIT_ERROR_END_TIME,
			FILE_NAME_TRANSMIT_CHANGE_START_TIME, FILE_NAME_TRANSMIT_PUSHKEY_END_TIME, FILE_NAME_TCP_SIG_PERIOD,
			FILE_NAME_TRANSMIT_CHECK_PERIOD, FILE_NAME_TRANSMIT_CHECK_END_TIME, FILE_NAME_APN_CMCC, FILE_NAME_APN_CUCC,
			FILE_NAME_SMS_USER, FILE_NAME_SMS_ADMIN, FILE_NAME_OPEN_START_TIME, FILE_NAME_OPEN_END_TIME,
			FILE_NAME_CLOSE_START_TIME, FILE_NAME_CLOSE_END_TIME, FILE_NAME_DEREP_PERIOD_TIME };

	// spi写入的页码
	public static final String FILE_NAME_DATA_SAVE_ADDRESS = "File_Name_Data_Save_Address";

	public static final String FILE_STRING_SPLIP_SYMBOL = ";";
}
