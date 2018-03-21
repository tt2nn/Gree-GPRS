package com.gree.gprs.file;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.constant.FileConstant;
import com.gree.gprs.util.Utils;

public class FileWriteModel extends FileModel {

	/**
	 * save sms password
	 * 
	 * @param password
	 */
	public static void saveSmsPwd(String password) {

		writeFile(FileConstant.FILE_NAME_SMS_PASSWORD, password);
	}

	/**
	 * save server address
	 * 
	 * @param privateAddress
	 * @param ip
	 * @param port
	 */
	public static void saveServAddress(boolean privateAddress, String ip, String port) {

		String value = ip + FileConstant.FILE_STRING_SPLIP_SYMBOL + port;

		if (privateAddress) {

			writeFile(FileConstant.FILE_NAME_TCP_ADDRESS_PRIVATE, value);

		} else {

			writeFile(FileConstant.FILE_NAME_TCP_ADDRESS_PUBLIC, value);
		}
	}

	/**
	 * save heartBeat time
	 * 
	 * @param time
	 */
	public static void saveHbTime(int time) {

		writeFile(FileConstant.FILE_NAME_TCP_HEART_BEAT_PERIOD, time);
	}

	/**
	 * save error transmit start time
	 * 
	 * @param time
	 */
	public static void saveErrorStartTime(int time) {

		writeFile(FileConstant.FILE_NAME_TRANSMIT_ERROR_START_TIME, time);
	}

	/**
	 * save error transmit end time
	 * 
	 * @param time
	 */
	public static void saveErrorEndTime(int time) {

		writeFile(FileConstant.FILE_NAME_TRANSMIT_ERROR_END_TIME, time);
	}

	/**
	 * save change transmit end time
	 * 
	 * @param time
	 */
	public static void saveChangeEndTime(int time) {

		writeFile(FileConstant.FILE_NAME_TRANSMIT_CHANGE_END_TIME, time);
	}

	/**
	 * save push key transmit end time
	 * 
	 * @param time
	 */
	public static void savePushKeyEndTime(int time) {

		writeFile(FileConstant.FILE_NAME_TRANSMIT_PUSHKEY_END_TIME, time);
	}

	/**
	 * save sig tcp period time
	 * 
	 * @param time
	 */
	public static void saveSigPeriod(int time) {

		writeFile(FileConstant.FILE_NAME_TCP_SIG_PERIOD, time);
	}

	/**
	 * save check transmit period time
	 * 
	 * @param time
	 */
	public static void saveCheckPeriod(int time) {

		writeFile(FileConstant.FILE_NAME_TRANSMIT_CHECK_PERIOD, time);
	}

	/**
	 * save check transmit end time
	 * 
	 * @param time
	 */
	public static void saveCheckEndTime(int time) {

		writeFile(FileConstant.FILE_NAME_TRANSMIT_CHECK_END_TIME, time);
	}

	/**
	 * save cache transmit type with always
	 */
	public static void saveAlwaysTransmit() {

		writeFile(FileConstant.FILE_NAME_DATA_TRANSM, Constant.TRANSMIT_TYPE_ALWAYS);
	}

	/**
	 * save cache transmit type with check
	 */
	public static void saveCheckTransmit() {

		writeFile(FileConstant.FILE_NAME_DATA_TRANSM, Constant.TRANSMIT_TYPE_CHECK);
	}

	/**
	 * save gprs choose state
	 * 
	 * @param choosed
	 */
	public static void saveGprsChooseState(boolean choosed) {

		byte state = (byte) 0x01;

		if (!choosed) {

			state = (byte) 0x00;
		}

		writeFile(FileConstant.FILE_NAME_GPRS_CHOOSED, state);
	}

	/**
	 * save spi write address
	 * 
	 * @param address
	 */
	public static void saveSpiAddress(int address) {

		writeFile(FileConstant.FILE_NAME_SPI_WRITE_ADDRESS, Utils.intToBytes(address, 3));
	}

	/**
	 * save apn
	 * 
	 * @param cucc
	 * @param apn
	 * @param name
	 * @param pwd
	 */
	public static void saveApn(boolean cucc, String apn, String name, String pwd) {

		String value = apn + FileConstant.FILE_STRING_SPLIP_SYMBOL + name + FileConstant.FILE_STRING_SPLIP_SYMBOL + pwd;

		if (cucc) {

			writeFile(FileConstant.FILE_NAME_APN_CUCC, value);

		} else {

			writeFile(FileConstant.FILE_NAME_APN_CMCC, value);
		}
	}

	/**
	 * save sms user list
	 * 
	 * @param users
	 */
	public static void saveSmsUsers(String[] users) {

		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < users.length; i++) {

			stringBuffer.append(users[i]);

			if (i < users.length - 1) {

				stringBuffer.append(FileConstant.FILE_STRING_SPLIP_SYMBOL);
			}
		}

		writeFile(FileConstant.FILE_NAME_SMS_USER, stringBuffer.toString());
	}

	/**
	 * save sms admin list
	 * 
	 * @param admins
	 */
	public static void saveSmsAdmins(String[] admins) {

		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < admins.length; i++) {

			stringBuffer.append(admins[i]);

			if (i < admins.length - 1) {

				stringBuffer.append(FileConstant.FILE_STRING_SPLIP_SYMBOL);
			}
		}

		writeFile(FileConstant.FILE_NAME_SMS_ADMIN, stringBuffer.toString());
	}

	/**
	 * save open transmit start time
	 * 
	 * @param time
	 */
	public static void saveOpenStartTime(int time) {

		writeFile(FileConstant.FILE_NAME_OPEN_START_TIME, time);
	}

	/**
	 * save open transmit end time
	 * 
	 * @param time
	 */
	public static void saveOpenEndTime(int time) {

		writeFile(FileConstant.FILE_NAME_OPEN_END_TIME, time);
	}

	/**
	 * save close transmit start time
	 * 
	 * @param time
	 */
	public static void saveCloseStartTime(int time) {

		writeFile(FileConstant.FILE_NAME_CLOSE_START_TIME, time);
	}

	/**
	 * save close transmit end time
	 * 
	 * @param time
	 */
	public static void saveCloseEndTime(int time) {

		writeFile(FileConstant.FILE_NAME_CLOSE_END_TIME, time);
	}

}
