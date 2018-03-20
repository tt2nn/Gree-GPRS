package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;

public class OntTwoModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = (Configure.Transmit_Open_End_Time / 60) + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Open_End, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setOpenEndTime(Integer.parseInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Open_End);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Open_End);
	}

}
