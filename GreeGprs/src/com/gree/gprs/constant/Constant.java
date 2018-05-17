package com.gree.gprs.constant;

public class Constant {

	// 波特率
	public static final int BAUD_RATE = 9600;
	// 模块型号
	public static final byte GPRS_MODEL = (byte) 0x02;

	// 实时监控上报
	public static final byte TRANSMIT_TYPE_ALWAYS = (byte) 0x00;
	// 调试上报
	public static final byte TRANSMIT_TYPE_DEBUG = (byte) 0x01;
	// 按键上报
	public static final byte TRANSMIT_TYPE_PUSHKEY = (byte) 0x02;
	// 故障上报
	public static final byte TRANSMIT_TYPE_ERROR = (byte) 0x03;
	// 亚健康（异常）上报
	public static final byte TRANSMIT_TYPE_WARNING = (byte) 0x04;
	// 厂家参数变化上报
	public static final byte TRANSMIT_TYPE_CHANGE = (byte) 0x05;
	// 关机上报
	public static final byte TRANSMIT_TYPE_CLOSE = (byte) 0x06;
	// 选举上报
	public static final byte TRANSMIT_TYPE_CHOOSE = (byte) 0x07;
	// 上电上报
	public static final byte TRANSMIT_TYPE_POWER = (byte) 0x08;
	// 打卡上报
	public static final byte TRANSMIT_TYPE_CHECK = (byte) 0x80;
	// 开机上报
	public static final byte TRANSMIT_TYPE_OPEN = (byte) 0X0A;
	// 停止上报
	public static final byte TRANSMIT_TYPE_STOP = (byte) 0xFF;

	// GPRS ERROR TYPE
	public static final int GPRS_ERROR_TYPE_NO = 0;
	public static final int GPRS_ERROR_TYPE_SIM = 1;
	public static final int GPRS_ERROR_TYPE_NETWORK = 2;
	public static final int GPRS_ERROR_TYPE_SERVER = 3;

	// 选举
	public static final byte FUNCTION_CHOOSE = (byte) 0XF0;
	// 点名
	public static final byte FUNCTION_CALL = (byte) 0X0F;

}
