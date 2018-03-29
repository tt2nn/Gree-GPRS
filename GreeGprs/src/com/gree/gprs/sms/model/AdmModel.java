package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 管理员号码
 * 
 * @author lihaotian
 *
 */
public class AdmModel extends SmsBaseModel {

	protected void queryParams() {

		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < Configure.Sms_Admin_List.length; i++) {

			String string = (i + 1) + SmsConstant.Sms_Split_Value_Symbol + Configure.Sms_Admin_List[i];

			if (stringBuffer.length() + string.length() > 70) {

				stringBuffer.deleteCharAt(stringBuffer.length() - 1);

				SmsModel.buildMessage(SmsConstant.Sms_Type_Adm, stringBuffer.toString());

				stringBuffer = new StringBuffer();
			}

			stringBuffer.append(string);

			if (i < Configure.Sms_Admin_List.length - 1) {

				stringBuffer.append(SmsConstant.Sms_Split_Value_Symbol);
			}
		}

		SmsModel.buildMessage(SmsConstant.Sms_Type_Adm, stringBuffer.toString());
	}

	protected void setParams(String smsValue) {

		if (!Utils.isNotEmpty(smsValue)) {

			SmsModel.buildMessageError(SmsConstant.Sms_Type_Adm);
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

				if (Utils.isNotEmpty(phone) && num > 0 && num < Configure.Sms_Admin_List.length && phone.length() >= 5
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

					Configure.Sms_Admin_List[i] = phones[i];
					isChange = true;
				}
			}

			if (isChange) {

				FileWriteModel.saveSmsAdmins(Configure.Sms_Admin_List);
				SmsModel.buildMessageOk(SmsConstant.Sms_Type_Adm);
			}
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Adm);
	}

}
