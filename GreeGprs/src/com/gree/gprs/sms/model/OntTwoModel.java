package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

public class OntTwoModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = (Configure.Transmit_Open_End_Time / 60) + "";

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_OPEN_END, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setOpenEndTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_OPEN_END);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_OPEN_END);
	}

}
