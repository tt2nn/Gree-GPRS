package com.gree.gprs.tcp.model;

import java.util.Calendar;
import java.util.Date;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.entity.CompressStruct;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.util.CRC;
import com.gree.gprs.util.Utils;
import com.gree.gprs.util.lzo.LzoCompressor1x_1;
import com.gree.gprs.util.lzo.lzo_uintp;
import com.gree.gprs.variable.Variable;

public class TransmitCompressModel {

	private static Date date = new Date();
	private static Calendar calendar = Calendar.getInstance();

	private static byte[] transmData = new byte[64 * 1024];
	private static byte[] lzoData = new byte[64 * 1024];
	private static int transmLen = 0;

	private static LzoCompressor1x_1 lzo = new LzoCompressor1x_1();
	private static lzo_uintp lzoUintp = new lzo_uintp();

	private static int sendNum = 0;
	private static CompressStruct[] struct7es = new CompressStruct[50];

	private static byte[] compareBuffer = new byte[4096];

	/**
	 * 去重上传
	 * 
	 * @param dataLength
	 * @param time
	 * @param send
	 */
	public static void dataTransmCompress(int dataLength, long time, boolean send) {

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
			sendNum++;

		} else {

			len = dataTransmCompare(dataLength, time);
		}

		if (Configure.Transmit_Derep_Period > 0) {

			for (int i = 0; i < len; i++) {

				transmData[transmLen] = Variable.Tcp_Out_Data_Buffer[i];
				transmLen++;
			}

		} else {

			TcpServer.sendData(Variable.Tcp_Out_Data_Buffer, len);
		}
	}

	/**
	 * 重置发送次数
	 */
	public static void resetSendNumber() {

		sendNum = 0;
	}

	/**
	 * 上传数据
	 */
	private static void transmitData() {

		sendNum = 0;

		lzo.compress(transmData, 0, transmLen, lzoData, 0, lzoUintp);

		transmData[18] = (byte) 0x83;

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

		lzo.compress(Variable.Data_Query_Buffer, 12, dataLength, lzoData, 0, lzoUintp);

		for (int i = 0; i < lzoUintp.value; i++) {

			Variable.Tcp_Out_Data_Buffer[i + 25] = lzoData[i];
		}

		return buildBufferData(Variable.Tcp_Out_Data_Buffer, lzoUintp.value + 7, lzoUintp.value + 25);
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

		int len = 0;

		if (UartModel.Uart_Type == UartModel.UART_TYPE_7E) {

			len = split7E(dataLength);

		} else {

			len = splitModbus(dataLength);
		}

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

		lzo.compress(compareBuffer, 0, len, lzoData, 0, lzoUintp);

		for (int i = 0; i < lzoUintp.value; i++) {

			Variable.Tcp_Out_Data_Buffer[i + 25] = lzoData[i];
		}

		return buildBufferData(Variable.Tcp_Out_Data_Buffer, lzoUintp.value + 7, lzoUintp.value + 25);
	}

	/**
	 * 将3s的数据进行拆分，去重，7E
	 * 
	 * @param dataLength
	 * @return
	 */
	private static int split7E(int dataLength) {

		int realLength = 0;
		int i = 12;
		while (i < dataLength) {

			if (Variable.Data_Query_Buffer[i] == (byte) 0x7E) {

				if (Variable.Data_Query_Buffer[i + 1] == (byte) 0x7E) {

					int len = (Variable.Data_Query_Buffer[i + 5] & 0xFF) - 4 + 10;
					realLength += compare7E(i, realLength);

					i += len;
					continue;
				}
			}

			i++;
		}

		return realLength;
	}

	/**
	 * 7E数据比较去重
	 * 
	 * @param start
	 * @param cacheLen
	 * @return
	 */
	private static int compare7E(int start, int cacheLen) {

		int compareLen = cacheLen;
		int len = (Variable.Data_Query_Buffer[start + 5] & 0xFF) - 4;

		for (int i = 0; i < struct7es.length; i++) {

			if (struct7es[i] == null) {

				struct7es[i] = new CompressStruct(9);
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

						if (Variable.Data_Query_Buffer[start + j + 9] != struct7es[i].getData()[j + 9]) {

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

		for (int i = 0; i < 9; i++) {

			compareBuffer[cacheLen + i] = Variable.Data_Query_Buffer[start + i];
		}

		byte[] lenBytes = Utils.intToBytes(compareLen - cacheLen);
		compareBuffer[cacheLen + 9] = lenBytes[0];
		compareBuffer[cacheLen + 10] = lenBytes[1];

		compareLen += 11;

		byte check = compareBuffer[cacheLen];
		for (int i = cacheLen + 1; i < compareLen; i++) {

			check = (byte) (check ^ compareBuffer[i]);
		}
		compareBuffer[compareLen] = check;
		compareLen++;

		return compareLen - cacheLen;
	}

	/**
	 * 将3s数据进行拆分Modbus
	 * 
	 * @param dataLength
	 * @return
	 */
	private static int splitModbus(int dataLength) {

		int realLength = 0;
		int i = 12;
		while (i < dataLength) {

			if (Variable.Data_Query_Buffer[i] == (byte) 0x01 || Variable.Data_Query_Buffer[i] == (byte) 0x02
					|| Variable.Data_Query_Buffer[i] == (byte) 0x03 || Variable.Data_Query_Buffer[i] == (byte) 0x04) {

				int len = Variable.Data_Query_Buffer[i + 1] & 0xFF;

				if (len <= 250) {

					byte[] crc16 = CRC.crc16(Variable.Data_Query_Buffer, i - 1, i + 2 + len);

					if (crc16[1] == Variable.Data_Query_Buffer[i + 2 + len]
							&& crc16[0] == Variable.Data_Query_Buffer[i + 3 + len]) {

						realLength += compareModbusRead(i - 1, realLength);

						i += len + 4;
						continue;
					}
				}

			} else if (Variable.Data_Query_Buffer[i] == (byte) 0x0f || Variable.Data_Query_Buffer[i] == (byte) 0x10) {

				int len = Variable.Data_Query_Buffer[i + 5] & 0xFF;

				if (len <= 246) {

					byte[] crc16 = CRC.crc16(Variable.Data_Query_Buffer, i - 1, i + 6 + len);

					if (crc16[1] == Variable.Data_Query_Buffer[i + 6 + len]
							&& crc16[0] == Variable.Data_Query_Buffer[i + 7 + len]) {

						realLength += compareModbusWrite(i - 1, realLength);

						i += len + 8;
						continue;
					}
				}
			}

			i++;
		}

		return realLength;
	}

	/**
	 * Modbus Read 数据比较去重
	 * 
	 * @param start
	 * @param cacheLen
	 * @return
	 */
	private static int compareModbusRead(int start, int cacheLen) {

		int compareLen = cacheLen;
		int len = Variable.Data_Query_Buffer[start + 2] & 0xFF;

		for (int i = 0; i < struct7es.length; i++) {

			if (struct7es[i] == null) {

				struct7es[i] = new CompressStruct(3);
				struct7es[i].insertData(Variable.Data_Query_Buffer, start, len + 5);

				for (int j = 0; j < len; j++) {

					compareBuffer[compareLen + 5] = (byte) j;
					compareLen++;

					compareBuffer[compareLen + 5] = Variable.Data_Query_Buffer[start + 3 + j];
					compareLen++;
				}

				break;

			} else {

				if (struct7es[i].compare(Variable.Data_Query_Buffer, start)) {

					boolean different = false;

					for (int j = 0; j < len; j++) {

						if (Variable.Data_Query_Buffer[start + j + 3] != struct7es[i].getData()[j + 3]) {

							different = true;

							compareBuffer[compareLen + 5] = (byte) j;
							compareLen++;

							compareBuffer[compareLen + 5] = Variable.Data_Query_Buffer[start + 3 + j];
							compareLen++;
						}
					}

					if (different) {

						struct7es[i].insertData(Variable.Data_Query_Buffer, start, len + 5);
					}

					break;
				}
			}
		}

		for (int i = 0; i < 3; i++) {

			compareBuffer[cacheLen + i] = Variable.Data_Query_Buffer[start + i];
		}

		byte[] lenBytes = Utils.intToBytes(compareLen - cacheLen);
		compareBuffer[cacheLen + 3] = lenBytes[0];
		compareBuffer[cacheLen + 4] = lenBytes[1];
		compareLen += 5;

		byte[] crc16 = CRC.crc16(compareBuffer, cacheLen, compareLen);
		compareBuffer[compareLen] = crc16[1];
		compareLen++;
		compareBuffer[compareLen] = crc16[0];
		compareLen++;

		return compareLen - cacheLen;
	}

	/**
	 * Modbus Write 数据比较去重
	 * 
	 * @param start
	 * @param cacheLen
	 * @return
	 */
	private static int compareModbusWrite(int start, int cacheLen) {

		int compareLen = cacheLen;
		int len = Variable.Data_Query_Buffer[start + 5] & 0xFF;

		for (int i = 0; i < struct7es.length; i++) {

			if (struct7es[i] == null) {

				struct7es[i] = new CompressStruct(7);
				struct7es[i].insertData(Variable.Data_Query_Buffer, start, len + 9);

				for (int j = 0; j < len; j++) {

					compareBuffer[compareLen + 9] = (byte) j;
					compareLen++;

					compareBuffer[compareLen + 9] = Variable.Data_Query_Buffer[start + 7 + j];
					compareLen++;
				}

				break;

			} else {

				if (struct7es[i].compare(Variable.Data_Query_Buffer, start)) {

					boolean different = false;

					for (int j = 0; j < len; j++) {

						if (Variable.Data_Query_Buffer[start + j + 7] != struct7es[i].getData()[j + 7]) {

							different = true;

							compareBuffer[compareLen + 9] = (byte) j;
							compareLen++;

							compareBuffer[compareLen + 9] = Variable.Data_Query_Buffer[start + 7 + j];
							compareLen++;
						}
					}

					if (different) {

						struct7es[i].insertData(Variable.Data_Query_Buffer, start, len + 9);
					}

					break;
				}
			}
		}

		for (int i = 0; i < 7; i++) {

			compareBuffer[cacheLen + i] = Variable.Data_Query_Buffer[start + i];
		}

		byte[] lenBytes = Utils.intToBytes(compareLen - cacheLen);
		compareBuffer[cacheLen + 7] = lenBytes[0];
		compareBuffer[cacheLen + 8] = lenBytes[1];
		compareLen += 9;

		byte[] crc16 = CRC.crc16(compareBuffer, cacheLen, compareLen);
		compareBuffer[compareLen] = crc16[1];
		compareLen++;
		compareBuffer[compareLen] = crc16[0];
		compareLen++;

		return compareLen - cacheLen;
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
