package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 信号信息周期
 * 
 * @author zhangzhuang
 *
 */
public class SigModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Tcp_Sig_Period / 60 + "";

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_SIG, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setSigPeriodTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_SIG);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_SIG);
	}

}
