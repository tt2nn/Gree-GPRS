package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;

/**
 * 打卡上报周期
 * 
 * @author zhangzhuang
 *
 */
public class CheckPeriodModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Check_Period / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Check_Period, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setCheckPeriodTime(Integer.parseInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Check_Period);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Check_Period);
	}

}
