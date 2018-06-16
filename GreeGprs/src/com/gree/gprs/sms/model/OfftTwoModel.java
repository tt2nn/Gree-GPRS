package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

public class OfftTwoModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = (Configure.Transmit_Close_End_Time / 60) + "";

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_CLOSE_END, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setCloseEndTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageSetOk(SmsConstant.SMS_TYPE_CLOSE_END);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_CLOSE_END);
	}

}
