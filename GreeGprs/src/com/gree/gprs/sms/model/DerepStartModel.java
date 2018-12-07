package com.gree.gprs.sms.model;

import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.sms.SmsModel;

public class DerepStartModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		DataCenter.alwaysTransmit();
		SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_DEREP_START);
	}

}
