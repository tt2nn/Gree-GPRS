package com.gree.gprs.sms.model;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.variable.Variable;

public class DerepingModel extends SmsBaseModel {

	protected String queryParams() {

		if (Variable.Transmit_Cache_Type == Constant.TRANSMIT_TYPE_DEREP) {

			return "on";
		}

		return "off";
	}

	protected boolean setParams(String smsValue) {
		return false;
	}

}
