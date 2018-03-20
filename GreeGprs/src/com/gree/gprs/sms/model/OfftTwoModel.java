package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;

public class OfftTwoModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = (Configure.Transmit_Close_End_Time / 60) + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Close_End, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setCloseEndTime(Integer.parseInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Close_End);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Close_End);
	}

}
