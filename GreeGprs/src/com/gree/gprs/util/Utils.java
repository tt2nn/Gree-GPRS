package com.gree.gprs.util;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.entity.Device;
import com.gree.gprs.entity.Time;

/**
 * 工具类
 * 
 * @author lihaotian
 *
 */
public class Utils {

	/**
	 * int 转 byte[2] 高位在前地位在后
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] intToBytes(int src) {

		byte[] value = new byte[2];

		value[0] = (byte) ((src >> 8) & 0xFF);
		value[1] = (byte) (src & 0xFF);

		return value;
	}

	/**
	 * int 转 byte[len] 高位在前地位在后
	 * 
	 * @param src
	 * @param len
	 * @return
	 */
	public static byte[] intToBytes(int src, int len) {

		byte[] value = new byte[len];

		for (int i = len - 1; i >= 0; i--) {

			if (i == len - 1) {

				value[i] = (byte) (src & 0xFF);

			} else {

				value[i] = (byte) ((src >> ((len - 1 - i) * 8)) & 0xFF);
			}
		}

		return value;
	}

	/**
	 * byte[2] 转 int 高位在前地位在后
	 * 
	 * @param src
	 * @return
	 */
	public static int bytesToInt(byte[] src) {

		int value;

		value = (int) (((src[0] & 0xFF) << 8) | (src[1] & 0xFF));

		return value;
	}

	/**
	 * byte[len] 转 int 高位在前地位在后
	 * 
	 * @param src
	 * @param start
	 * @param len
	 * @return
	 */
	public static int bytesToIntValue(byte[] src, int start, int len) {

		int value = 0;

		switch (len) {

		case 4:

			value = (int) (((src[start] & 0xFF) << 24) | ((src[start + 1] & 0xFF) << 16)
					| ((src[start + 2] & 0xFF) << 8) | (src[start + 3] & 0xFF));
			break;

		case 3:

			value = (int) (((src[start] & 0xFF) << 16) | ((src[start + 1] & 0xFF) << 8) | (src[start + 2] & 0xFF));
			break;

		case 2:

			value = (int) (((src[start] & 0xFF) << 8) | (src[start + 1] & 0xFF));
			break;

		case 1:

			value = (int) ((src[start] & 0xFF));
			break;

		}

		return value;
	}

	/**
	 * byte[2] 转 int 高位在前地位在后
	 * 
	 * @param src
	 * @param one
	 * @param two
	 * @return
	 */
	public static int bytesToInt(byte[] src, int one, int two) {

		int value;

		value = (int) (((src[one] & 0xFF) << 8) | (src[two] & 0xFF));

		return value;
	}

	/**
	 * byte 读 bit
	 * 
	 * @param data
	 * @return
	 */
	public static int[] byteGetBit(byte data) {

		int[] bits = new int[8];

		for (int i = 0; i < bits.length; i++) {

			bits[i] = (data >> i) & 0x1;
		}

		return bits;
	}

	/**
	 * byte 读 bit
	 * 
	 * @param data
	 *            数据
	 * @param position
	 *            位置
	 * @return
	 */
	public static int byteGetBit(byte data, int position) {

		int value = (data >> position) & 0x1;

		return value;
	}

	/**
	 * 从 byte[] 中取 time
	 * 
	 * @param res
	 * @param start
	 * @return
	 */
	public static Time bytesToTime(byte[] res, int start) {

		Time time = new Time();

		time.setYear((res[start] & 0xFF) + 2000);
		time.setMonth(res[start + 1] & 0xFF);
		time.setDay(res[start + 2] & 0xFF);
		time.setHours(res[start + 3] & 0xFF);
		time.setMinutes(res[start + 4] & 0xFF);
		time.setSeconds(res[start + 5] & 0xFF);

		return time;
	}

	/**
	 * 年月日时分秒转时间措
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public static long getTime(int year, int month, int day, int hours, int minutes, int seconds) {

		long sumDay = 0;

		for (int i = 1970; i < year; i++) {

			if ((i % 4 == 0 && i % 100 != 0) || i % 400 == 0) {

				sumDay += 366;
			} else {

				sumDay += 365;
			}
		}

		for (int j = 1; j < month; j++) {

			if (j == 2) {
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					sumDay += 29;
				} else {
					sumDay += 28;
				}
				continue;
			}

			if (j == 1 || j == 3 || j == 5 || j == 7 || j == 8 || j == 10) {

				sumDay += 31;
			} else {

				sumDay += 30;
			}
		}

		long d = (day + sumDay - 1) * 24 * 60 * 60 * 1000L;
		long h = (hours - 8) * 60 * 60 * 1000L;
		long m = minutes * 60 * 1000L;
		long s = seconds * 1000L;

		long time = d + h + m + s;

		return time;
	}

	/**
	 * long 转 byte[8] 高位在前地位在后
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] longToBytes(long src) {

		byte[] value = new byte[8];

		long temp = src / 1000;

		value[0] = (byte) ((temp >> (8 * 7)) & 0xFF);
		value[1] = (byte) ((temp >> (8 * 6)) & 0xFF);
		value[2] = (byte) ((temp >> (8 * 5)) & 0xFF);
		value[3] = (byte) ((temp >> (8 * 4)) & 0xFF);
		value[4] = (byte) ((temp >> (8 * 3)) & 0xFF);
		value[5] = (byte) ((temp >> (8 * 2)) & 0xFF);
		value[6] = (byte) ((temp >> 8) & 0xFF);
		value[7] = (byte) (temp & 0xFF);

		return value;
	}

	/**
	 * byte[8] 转 long 高位在前地位在后
	 * 
	 * @param src
	 * @return
	 */
	public static long bytesToLong(byte[] src, int start) {

		long value;

		value = (long) (((src[start] & 0xFF) << (8 * 7)) | ((src[start + 1] & 0xFF) << (8 * 6))
				| ((src[start + 2] & 0xFF) << (8 * 5)) | ((src[start + 3] & 0xFF) << (8 * 4))
				| ((src[start + 4] & 0xFF) << (8 * 3)) | ((src[start + 5] & 0xFF) << (8 * 2))
				| ((src[start + 6] & 0xFF) << 8) | (src[start + 7] & 0xFF));

		return value * 1000;
	}

	/**
	 * 判断字符串不为空
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNotEmpty(String string) {

		if (string != null && !string.equals("")) {
			return true;

		}
		return false;

	}

	/**
	 * String 转 int
	 * 
	 * @param src
	 * @return
	 */
	public static int stringToInt(String src) {

		int res = -1;

		try {

			res = Integer.parseInt(src.trim());

		} catch (Exception e) {

			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 重置数据数据
	 * 
	 * @param data
	 */
	public static void resetData(byte[] data) {

		for (int i = 0; i < data.length; i++) {

			data[i] = (byte) 0x00;
		}
	}

	/**
	 * 重置数据数据
	 * 
	 * @param data
	 */
	public static void resetModbusData(byte[] data) {

		for (int i = 0; i < data.length; i++) {

			if (i % 2 == 0) {

				data[i] = (byte) 0x80;

			} else {

				data[i] = (byte) 0x00;
			}
		}
	}

	/**
	 * String 转 16进制 byte
	 * 
	 * @param res
	 * @return
	 */
	public static byte stringToByte(String res) {

		int resIntHex = 0x00;

		try {

			resIntHex = Integer.parseInt(res, 16);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return (byte) resIntHex;
	}

	/**
	 * String Contains String
	 * 
	 * @param res
	 * @param param
	 * @return
	 */
	public static boolean stringContains(String res, String param) {

		if (res.indexOf(param) != -1) {

			return true;
		}

		return false;
	}

	/**
	 * 获取APN
	 * 
	 * @return
	 */
	public static Apn getApn() {

		Apn apn = new Apn();

		if (Device.getInstance().getMnc() == 1) {

			apn.setApnName(Configure.Apn_Cucc);

		} else if (Device.getInstance().getMnc() == 0) {

			apn.setApnName(Configure.Apn_Cmcc);
		}

		apn.setUserName(Configure.Apn_Name);
		apn.setPassword(Configure.Apn_Pwd);

		return apn;
	}

}
