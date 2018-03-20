package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;

/**
 * 短信密码
 * 
 * @author lihaotian
 *
 */
public class PwdModel extends SmsBaseModel {

	protected void queryParams() {

		SmsModel.buildMessage(SmsConstant.Sms_Type_Pwd, Configure.Sms_Pwd);
	}

	protected void setParams(String smsValue) {

		if (Configure.setSmsPwd(smsValue)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Pwd);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Pwd);
	}

}
