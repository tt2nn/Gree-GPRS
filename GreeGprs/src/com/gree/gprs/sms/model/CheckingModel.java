package com.gree.gprs.sms.model;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.variable.Variable;

/**
 * query check tyep on or off
 * 
 * @author lihaotian
 *
 */
public class CheckingModel extends SmsBaseModel {

	protected void queryParams() {

		if (Variable.Transmit_Cache_Type == Constant.TRANSMIT_TYPE_CHECK) {

			SmsModel.buildMessage(SmsConstant.Sms_Type_Checking, "on");
			return;
		}

		SmsModel.buildMessage(SmsConstant.Sms_Type_Checking, "off");
	}

	protected void setParams(String smsValue) {

	}

}
