package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

public class OntOneModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = (Configure.Transmit_Open_Start_Time / 60) + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Open_Start, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setOpenStartTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Open_Start);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Open_Start);
	}

}
