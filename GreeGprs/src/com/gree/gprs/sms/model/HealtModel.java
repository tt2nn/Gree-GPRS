package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;

/**
 * 厂家参数改变前传输结束时间
 * 
 * @author lihaotian
 *
 */
public class HealtModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Change_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Healt, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setChangeEndTime(Integer.parseInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Healt);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Healt);
	}

}
