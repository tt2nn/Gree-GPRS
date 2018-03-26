package com.gree.gprs.file;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.constant.FileConstant;

public class FileReadModel extends FileModel {

	/**
	 * query gprs choose state
	 * 
	 * @return
	 */
	public static boolean queryGprsChooseState() {

		return readFileBool(FileConstant.FILE_NAME_GPRS_CHOOSED);
	}

	/**
	 * query sms password
	 * 
	 * @return
	 */
	public static String querySmsPwd() {

		return readFileString(FileConstant.FILE_NAME_SMS_PASSWORD);
	}

	/**
	 * query cache transmit type
	 * 
	 * @return
	 */
	public static byte queryTransmitType() {

		int length = readFile(FileConstant.FILE_NAME_DATA_TRANSM);

		if (length == 1) {

			return FileModel.File_Buffer[2];
		}

		return Constant.TRANSMIT_TYPE_CHECK;
	}

	/**
	 * query tcp public address
	 * 
	 * @param privateAddress
	 * @return
	 */
	public static String queryTcpAddress(boolean privateAddress) {

		if (privateAddress) {

			return readFileString(FileConstant.FILE_NAME_TCP_ADDRESS_PRIVATE);

		} else {

			return readFileString(FileConstant.FILE_NAME_TCP_ADDRESS_PUBLIC);
		}
	}

	/**
	 * query heartBeat time
	 * 
	 * @return
	 */
	public static int queryHbTime() {

		return readFileInt(FileConstant.FILE_NAME_TCP_HEART_BEAT_PERIOD);
	}

	/**
	 * query error transmit start time
	 * 
	 * @return
	 */
	public static int queryErrorStartTime() {

		return readFileInt(FileConstant.FILE_NAME_TRANSMIT_ERROR_START_TIME);
	}

	/**
	 * query error transmit end time
	 * 
	 * @return
	 */
	public static int queryErrorEndTime() {

		return readFileInt(FileConstant.FILE_NAME_TRANSMIT_ERROR_END_TIME);
	}

	/**
	 * query change transmit end time
	 * 
	 * @return
	 */
	public static int queryChangeEndTime() {

		return readFileInt(FileConstant.FILE_NAME_TRANSMIT_CHANGE_END_TIME);
	}

	/**
	 * query push key transmit end time
	 * 
	 * @return
	 */
	public static int queryPushKeyEndTime() {

		return readFileInt(FileConstant.FILE_NAME_TRANSMIT_PUSHKEY_END_TIME);
	}

	/**
	 * query sig tcp period time
	 * 
	 * @return
	 */
	public static int querySigPeriod() {

		return readFileInt(FileConstant.FILE_NAME_TCP_SIG_PERIOD);
	}

	/**
	 * query check transmit period
	 * 
	 * @return
	 */
	public static int queryCheckPeriod() {

		return readFileInt(FileConstant.FILE_NAME_TRANSMIT_CHECK_PERIOD);
	}

	/**
	 * query check transmit end time
	 * 
	 * @return
	 */
	public static int queryCheckEndTime() {

		return readFileInt(FileConstant.FILE_NAME_TRANSMIT_CHECK_END_TIME);
	}

	/**
	 * query apn file info
	 * 
	 * @param cucc
	 * @return
	 */
	public static String queryApn(boolean cucc) {

		if (cucc) {

			return readFileString(FileConstant.FILE_NAME_APN_CUCC);

		} else {

			return readFileString(FileConstant.FILE_NAME_APN_CMCC);
		}
	}

	/**
	 * query sms user
	 * 
	 * @return
	 */
	public static String querySmsUser() {

		return readFileString(FileConstant.FILE_NAME_SMS_USER);
	}

	/**
	 * query sms admin
	 * 
	 * @return
	 */
	public static String querySmsAdmin() {

		return readFileString(FileConstant.FILE_NAME_SMS_ADMIN);
	}

	/**
	 * query open transmit start time
	 * 
	 * @return
	 */
	public static int queryOpenStartTime() {

		return readFileInt(FileConstant.FILE_NAME_OPEN_START_TIME);
	}

	/**
	 * query open transmit end time
	 * 
	 * @return
	 */
	public static int queryOpenEndTime() {

		return readFileInt(FileConstant.FILE_NAME_OPEN_END_TIME);
	}

	/**
	 * query close transmit start time
	 * 
	 * @return
	 */
	public static int queryCloseStartTime() {

		return readFileInt(FileConstant.FILE_NAME_CLOSE_START_TIME);
	}

	/**
	 * query close transmit end time
	 * 
	 * @return
	 */
	public static int queryCloseEndTime() {

		return readFileInt(FileConstant.FILE_NAME_CLOSE_END_TIME);

	}

	/**
	 * query data address
	 * 
	 * @return
	 */
	public static int queryDataAddress() {

		return readFileInt(FileConstant.FILE_NAME_DATA_SAVE_ADDRESS);
	}

}
