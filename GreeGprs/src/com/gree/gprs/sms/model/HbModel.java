package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 心跳间隔
 * 
 * @author lihaotian
 *
 */
public class HbModel extends SmsBaseModel {

	protected void queryParams() {

		String value1 = "heart,0,";
		String smsValue = value1 + Configure.Tcp_Heart_Beat_Period;

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_HB, smsValue);
	}

	protected void setParams(String smsValue) {

		String split = "heart,0,";
		int start = smsValue.indexOf(split);
		if (start >= 0) {

			start += split.length();
			int end = smsValue.length();
			String second = smsValue.substring(start, end);

			if (Configure.setHbPeriodTime(Utils.stringToInt(second))) {

				SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_HB);
				return;
			}
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_HB);
	}

}
