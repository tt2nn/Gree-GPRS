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

	protected void queryParams() {

		String[] messArray = new String[10];
		int poi = 0;
		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < Configure.Sms_User_List.length; i++) {

			String string = (i + 1) + SmsConstant.Sms_Split_Value_Symbol + Configure.Sms_User_List[i];

			if (stringBuffer.length() + string.length() > 70) {

				stringBuffer.deleteCharAt(stringBuffer.length() - 1);

				messArray[poi] = stringBuffer.toString();
				poi++;

				stringBuffer = new StringBuffer();
			}

			stringBuffer.append(string);

			if (i < Configure.Sms_User_List.length - 1) {

				stringBuffer.append(SmsConstant.Sms_Split_Value_Symbol);
			}
		}
		messArray[poi] = stringBuffer.toString();

		SmsModel.sendMessageArray(SmsConstant.Sms_Type_Usron, messArray);
	}

	protected void setParams(String smsValue) {

		if (!Utils.isNotEmpty(smsValue)) {

			SmsModel.buildMessageError(SmsConstant.Sms_Type_Usron);
			return;
		}

		smsValue = smsValue + SmsConstant.Sms_Split_Value_Symbol;

		int start = 0;
		int end = 0;
		boolean isPhone = false;
		int num = 0;

		String[] phones = new String[5];
		boolean isError = false;

		while ((end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start)) != -1) {

			if (!isPhone) {

				String numString = smsValue.substring(start, end);
				num = Utils.stringToInt(numString) - 1;

				start = end + 1;
				isPhone = true;

			} else {

				String phone = smsValue.substring(start, end);

				if (Utils.isNotEmpty(phone) && num >= 0 && num < Configure.Sms_User_List.length && phone.length() >= 5
						&& phone.length() <= 24) {

					phones[num] = phone;

				} else {

					isError = true;
					break;
				}

				start = end + 1;
				isPhone = false;
			}
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
				SmsModel.buildMessageOk(SmsConstant.Sms_Type_Usron);
				return;
			}
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Usron);
	}

}
