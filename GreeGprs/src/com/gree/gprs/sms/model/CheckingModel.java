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

			SmsModel.buildMessage(SmsConstant.SMS_TYPE_CHECKING, "on");
			return;
		}

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_CHECKING, "off");
	}

	protected void setParams(String smsValue) {

		SmsModel.buildMessageError(SmsConstant.SMS_TYPE_CHECKING);
	}

}
