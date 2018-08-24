package com.gree.gprs.tcp.model;

import java.util.Calendar;
import java.util.Date;

import com.gree.gprs.entity.Struct7E;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.Utils;
import com.gree.gprs.util.lzo.LzoCompressor1x_1;
import com.gree.gprs.util.lzo.lzo_uintp;
import com.gree.gprs.variable.Variable;

public class TransmitLzoModel {

	private static Date date = new Date();
	private static Calendar calendar = Calendar.getInstance();

	private static byte[] transmData = new byte[64 * 1024];
	private static byte[] lzoData = new byte[64 * 1024];
	private static int transmLen = 0;

	private static LzoCompressor1x_1 lzo = new LzoCompressor1x_1();
	private static lzo_uintp lzoUintp = new lzo_uintp();

	private static int sendNum = 0;
	private static Struct7E[] struct7es = new Struct7E[50];

	private static byte[] compareBuffer = new byte[4096];

	public static void dataTransmLzo(int dataLength, long time, boolean send) {

		if (send) {

			transmitData();
			transmLen = 0;
			return;
		}

		if (transmLen + dataLength * 2 > transmData.length) {

			transmitData();
			transmLen = 0;
		}

		int len = 0;
		if (sendNum < 15) {

			len = dataTransm(dataLength, time);
			transmLen++;

		} else {

			len = dataTransmCompare(dataLength, time);
		}

		for (int i = 0; i < len; i++) {

			transmData[transmLen] = Variable.Tcp_Out_Data_Buffer[i];
			transmLen++;
		}
	}

	private static void transmitData() {

		sendNum = 0;

		transmData[18] = (byte) 0x83;

		lzo.compress(transmData, 0, transmLen, lzoData, 0, lzoUintp);

		for (int i = 0; i < lzoUintp.value; i++) {

			transmData[i + 19] = lzoData[i];
		}

		int len = buildBufferData(transmData, lzoUintp.value + 1, lzoUintp.value + 18 + 1);

		TcpServer.sendData(transmData, len);
	}

	/**
	 * GPRS模块上传机组数据
	 * 
	 * @param data
	 * @param start
	 * @param dataLength
	 * @param time
	 */
	private static int dataTransm(int dataLength, long time) {

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

		for (int i = 0; i < dataLength; i++) {

			Variable.Tcp_Out_Data_Buffer[i + 25] = Variable.Data_Query_Buffer[i + 12];
		}

		return buildBufferData(Variable.Tcp_Out_Data_Buffer, dataLength + 7, dataLength + 25);
	}

	/**
	 * GPRS模块上传机组数据
	 * 
	 * @param data
	 * @param start
	 * @param dataLength
	 * @param time
	 */
	private static int dataTransmCompare(int dataLength, long time) {

		int len = split(dataLength);
		if (len > 0) {

			Variable.Tcp_Out_Data_Buffer[18] = (byte) 0x86;

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

			for (int i = 0; i < len; i++) {

				Variable.Tcp_Out_Data_Buffer[i + 25] = compareBuffer[i];
			}

			return buildBufferData(Variable.Tcp_Out_Data_Buffer, len + 7, len + 25);
		}

		return 0;
	}

	private static int split(int dataLength) {

		int realLength = 0;
		int i = 12;
		while (i < dataLength) {

			if (Variable.Data_Query_Buffer[i] == (byte) 0x7E) {

				if (Variable.Data_Query_Buffer[i + 1] == (byte) 0x7E) {

					int len = Variable.Data_Query_Buffer[i + 5] - 4 + 10;
					realLength += compare(i, realLength);

					i += len;

					continue;
				}
			}

			compareBuffer[realLength] = Variable.Data_Query_Buffer[i];
			realLength++;
			i++;
		}

		return realLength;
	}

	private static int compare(int start, int cacheLen) {

		int compareLen = cacheLen;
		int len = Variable.Data_Query_Buffer[start + 5] - 4;

		for (int i = 0; i < struct7es.length; i++) {

			if (struct7es[i] == null) {

				struct7es[i] = new Struct7E();
				struct7es[i].insertData(Variable.Data_Query_Buffer, start, len + 10);

				for (int j = 0; j < len; j++) {

					compareBuffer[compareLen + 11] = (byte) j;
					compareLen++;

					compareBuffer[compareLen + 11] = Variable.Data_Query_Buffer[start + 9 + j];
					compareLen++;
				}

				break;

			} else {

				if (struct7es[i].compare(Variable.Data_Query_Buffer, start)) {

					boolean different = false;

					for (int j = 0; j < len; j++) {

						if (Variable.Data_Query_Buffer[start + len + j + 9] != struct7es[i].getData()[j + 9]) {

							different = true;

							compareBuffer[compareLen + 11] = (byte) j;
							compareLen++;

							compareBuffer[compareLen + 11] = Variable.Data_Query_Buffer[start + 9 + j];
							compareLen++;
						}
					}

					if (different) {

						struct7es[i].insertData(Variable.Data_Query_Buffer, start, len + 10);
					}

					break;
				}
			}
		}

		if (compareLen - cacheLen > 0) {

			for (int i = 0; i < 9; i++) {

				compareBuffer[cacheLen + i] = Variable.Data_Query_Buffer[start + i];
			}

			byte[] lenBytes = Utils.intToBytes(len);
			compareBuffer[cacheLen + 9] = lenBytes[0];
			compareBuffer[cacheLen + 10] = lenBytes[1];

			compareLen += 11;

			byte check = compareBuffer[cacheLen];
			for (int i = cacheLen; i < compareLen; i++) {

				check = (byte) (check ^ compareBuffer[i]);
			}
			compareBuffer[compareLen] = check;
			compareLen++;
		}

		return compareLen = cacheLen;
	}

	/**
	 * 构建通用数据
	 * 
	 * @param buffer
	 * @param dataLength
	 * @param crcPosition
	 */
	private static int buildBufferData(byte[] buffer, int dataLength, int crcPosition) {

		// 引导码
		buffer[0] = (byte) 0x7E;
		buffer[1] = (byte) 0x7E;

		// 目标地址
		for (int i = 0; i < Variable.Server_Mac.length; i++) {

			buffer[i + 2] = Variable.Server_Mac[i];
		}

		// 源地址
		for (int i = 0; i < Variable.Gprs_Mac.length; i++) {

			buffer[i + 9] = Variable.Gprs_Mac[i];
		}

		// 数据长度
		byte[] lengthBytes = Utils.intToBytes(dataLength);
		buffer[16] = lengthBytes[0];
		buffer[17] = lengthBytes[1];

		// crc8校验码
		buffer[crcPosition] = CRC.crc8(buffer, 2, crcPosition);

		return crcPosition + 1;
	}
}
