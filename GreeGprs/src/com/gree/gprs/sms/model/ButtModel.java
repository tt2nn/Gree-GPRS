package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;

/**
 * 按键调试周期
 * 
 * @author lihaotian
 *
 */
public class ButtModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Pushkey_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Butt, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setPushKeyEndTime(Integer.parseInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Butt);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Butt);
	}

}
