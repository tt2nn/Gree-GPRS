package com.gree.gprs.sms.model;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.variable.Variable;

/**
 * query check tyep on or off
 * 
 * @author lihaotian
 *
 */
public class CheckingModel {

	public static void smsAnalyze() {

		if (SmsModel.smsGetValue(SmsModel.Sms_Message).equals(SmsConstant.SMS_QUERY_SYMBOL)) {

			String smsValue = "";

			if (Variable.Transmit_Cache_Type == Constant.TRANSMIT_TYPE_CHECK) {

				smsValue = "on";
			}

			smsValue = "off";

			SmsModel.buildMessage(SmsConstant.SMS_TYPE_CHECKING, smsValue);

			return;
		}

		SmsModel.buildMessageUnknow();
	}

}
