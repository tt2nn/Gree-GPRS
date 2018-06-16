package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 故障点后传输时间
 * 
 * @author lihaotian
 *
 */
public class DebtModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Error_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_DEBT, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setErrorEndTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageSetOk(SmsConstant.SMS_TYPE_DEBT);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_DEBT);
	}

}
