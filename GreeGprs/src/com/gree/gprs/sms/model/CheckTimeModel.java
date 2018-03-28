package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 打卡上报时长
 * 
 * @author zhangzhuang
 *
 */
public class CheckTimeModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = (Configure.Transmit_Check_End_Time / 60) + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Check_Time, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setCheckEndTime(Utils.stringToInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Check_Time);
			return;
		}

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Check_Time);
	}

}
