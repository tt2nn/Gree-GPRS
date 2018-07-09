package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 普通手机账号
 * 
 * @author lihaotian
 *
 */
public class UsronModel extends SmsBaseModel {

	protected String queryParams() {

		String[] messArray = new String[10];
		int poi = 0;
		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < Configure.Sms_User_List.length; i++) {

			String string = (i + 1) + SmsConstant.SMS_SPLIT_VALUE_SYMBOL + Configure.Sms_User_List[i];

			if (stringBuffer.length() + string.length() > 70) {

				stringBuffer.deleteCharAt(stringBuffer.length() - 1);

				messArray[poi] = stringBuffer.toString();
				poi++;

				stringBuffer = new StringBuffer();
			}

			stringBuffer.append(string);

			if (i < Configure.Sms_User_List.length - 1) {

				stringBuffer.append(SmsConstant.SMS_SPLIT_VALUE_SYMBOL);
			}
		}
		messArray[poi] = stringBuffer.toString();

		SmsModel.sendMessageArray(SmsConstant.SMS_TYPE_USRON, messArray);
		return "";
	}

	protected boolean setParams(String smsValue) {

		smsValue = smsValue + SmsConstant.SMS_SPLIT_VALUE_SYMBOL;

		int start = 0;
		int end = 0;
		boolean isPhone = false;
		int num = 0;

		String[] phones = new String[10];
		boolean isError = false;

		while ((end = smsValue.indexOf(SmsConstant.SMS_SPLIT_VALUE_SYMBOL, start)) != -1) {

			if (end <= start) {

				return false;
			}

			if (!isPhone) {

				String numString = smsValue.substring(start, end);
				num = Utils.stringToInt(numString) - 1;

				start = end + 1;
				isPhone = true;

			} else {

				String phone = smsValue.substring(start, end);

				if (Utils.isNotEmpty(phone) && num >= 0 && num < Configure.Sms_User_List.length && phone.length() >= 5
						&& phone.length() <= 24 && Utils.checkNumber(phone)) {

					phones[num] = phone;

				} else {

					isError = true;
					break;
				}

				start = end + 1;
				isPhone = false;
			}
		}

		if (isPhone) {

			return false;
		}

		if (!isError) {

			boolean isChange = false;

			for (int i = 0; i < phones.length; i++) {

				if (Utils.isNotEmpty(phones[i])) {

					Configure.Sms_User_List[i] = phones[i];
					isChange = true;
				}
			}

			if (isChange) {

				FileWriteModel.saveSmsUsers(Configure.Sms_User_List);
				return true;
			}
		}

		return false;
	}

}
