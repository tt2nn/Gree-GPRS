package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.entity.Device;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.util.Utils;

/**
 * 接入点
 * 
 * @author lihaotian
 *
 */
public class ApnModel extends SmsBaseModel {

	protected void queryParams() {

		Apn apn = Utils.getApn();

		String smsValue = apn.getApnName() + SmsConstant.Sms_Split_Value_Symbol + apn.getUserName()
				+ SmsConstant.Sms_Split_Value_Symbol + apn.getPassword();

		SmsModel.buildMessage(SmsConstant.Sms_Type_Apn, smsValue);
	}

	protected void setParams(String smsValue) {

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String apn = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String name = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		String pwd = smsValue.substring(start, end);

		if (Configure.setApn(Device.getInstance().getMnc() == 1, apn, name, pwd)) {

			SmsModel.buildMessageOk(SmsConstant.Sms_Type_Apn);
			return;
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Apn);
	}

}
