package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 接入点
 * 
 * @author lihaotian
 *
 */
public class ApnModel extends SmsBaseModel {

	protected void queryParams() {

		Apn apn = Utils.getApn();

		String smsValue = apn.getApnName() + SmsConstant.SMS_SPLIT_VALUE_SYMBOL + apn.getUserName()
				+ SmsConstant.SMS_SPLIT_VALUE_SYMBOL + apn.getPassword();

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_APN, smsValue);
	}

	protected void setParams(String smsValue) {

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.SMS_SPLIT_VALUE_SYMBOL, start);
		String apn = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.indexOf(SmsConstant.SMS_SPLIT_VALUE_SYMBOL, start);
		String name = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		String pwd = smsValue.substring(start, end);

		if (Configure.setApn(Utils.simCucc(), apn, name, pwd)) {

			SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_APN);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_APN);
	}

}
