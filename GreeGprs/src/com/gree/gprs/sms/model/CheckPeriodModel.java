package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 打卡上报周期
 * 
 * @author zhangzhuang
 *
 */
public class CheckPeriodModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Check_Period / 60 + "";

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_CHECK_PERIOD, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setCheckPeriodTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_CHECK_PERIOD);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_CHECK_PERIOD);
	}

}
