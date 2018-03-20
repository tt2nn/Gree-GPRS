package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;

/**
 * 故障点后传输时间
 * 
 * @author lihaotian
 *
 */
public class DebtModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Configure.Transmit_Error_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Debt, smsValue);
	}

	protected void setParams(String smsValue) {

		if (Configure.setErrorEndTime(Integer.parseInt(smsValue) * 60)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Debt);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Debt);
	}

}
