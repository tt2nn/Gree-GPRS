package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 按键调试周期
 * 
 * @author lihaotian
 *
 */
public class ButtModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Pushkey_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_BUTT, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setPushKeyEndTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_BUTT);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_BUTT);
	}

}
