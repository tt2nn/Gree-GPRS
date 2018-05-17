package com.gree.gprs.variable;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.Constant;

public class Variable {

	// 版本号
	public static String App_Version = "V1.0";
	public static byte App_Version_First = (byte) 0x01;
	public static byte App_Version_Second = (byte) 0x00;

	// system time
	public static long System_Time = 0L;
	public static long System_Delta_Time = 0L;
	public static long Stop_Time = 0L;
	public static long Heart_Beat_Time = 0L;

	// mac address
	public static byte[] Gprs_Mac = new byte[7];
	public static byte[] Server_Mac = new byte[7];

	// tcp buffer
	public static byte[] Tcp_In_Buffer = new byte[1024];
	public static byte[] Tcp_Out_Buffer = new byte[1024];
	public static byte[] Tcp_Out_Data_Buffer = new byte[2048];

	// save data buffer
	public static byte[] Data_Cache_Buffer = new byte[4096];
	public static byte[] Data_Save_Buffer = new byte[2048];
	public static byte[] Data_Query_Buffer = new byte[2048];

	// cache transmit type
	public static byte Transmit_Cache_Type = Constant.TRANSMIT_TYPE_CHECK;

	// tcp ip and port
	public static String Tcp_Address_Ip = "";
	public static String Tcp_Address_Port = "";
	public static boolean Tcp_Address_Private = false;

	public static byte Transmit_Type = Constant.TRANSMIT_TYPE_STOP;

	public static boolean Gprs_Init_Success = false;
	public static boolean Gprs_Login = false;
	public static boolean Gprs_Choosed = false;
	public static boolean Change_Vpn = false;

	public static int Gprs_Error_Type = Constant.GPRS_ERROR_TYPE_NO;

	public static int Network_Signal_Level = 0;

	/**
	 * set private tcp address
	 */
	public static void setPrivateTcp() {

		Tcp_Address_Private = true;

		Tcp_Address_Ip = Configure.Tcp_Address_Ip_Private;
		Tcp_Address_Port = Configure.Tcp_Address_Port_Private;
	}

	/**
	 * set public tcp address
	 */
	public static void setPublicTcp() {

		Tcp_Address_Private = false;

		Tcp_Address_Ip = Configure.Tcp_Address_Ip_Public;
		Tcp_Address_Port = Configure.Tcp_Address_Port_Public;
	}
}
