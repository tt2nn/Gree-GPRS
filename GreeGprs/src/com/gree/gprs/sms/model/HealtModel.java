package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 厂家参数改变前传输结束时间
 * 
 * @author lihaotian
 *
 */
public class HealtModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Change_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_HEALT, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setChangeEndTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageSetOk(SmsConstant.SMS_TYPE_HEALT);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_HEALT);
	}

}
