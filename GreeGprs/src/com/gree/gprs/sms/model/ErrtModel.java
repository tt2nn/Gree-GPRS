package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;

/**
 * 故障点前传输时间
 * 
 * @author lihaotian
 *
 */
public class ErrtModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Error_Start_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Errt, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setErrorStartTime(Integer.parseInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Errt);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Errt);
	}

}
