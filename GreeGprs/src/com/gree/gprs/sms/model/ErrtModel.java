package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 故障点前传输时间
 * 
 * @author lihaotian
 *
 */
public class ErrtModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Error_Start_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_ERRT, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setErrorStartTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_ERRT);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_ERRT);
	}

}
