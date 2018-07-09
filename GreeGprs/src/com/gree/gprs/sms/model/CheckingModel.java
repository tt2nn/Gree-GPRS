package com.gree.gprs.sms.model;

import com.gree.gprs.constant.Constant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.variable.Variable;

/**
 * query check tyep on or off
 * 
 * @author lihaotian
 *
 */
public class CheckingModel extends SmsBaseModel {

	protected String queryParams() {

		if (Variable.Transmit_Cache_Type == Constant.TRANSMIT_TYPE_CHECK) {

			return "on";
		}

		return "off";
	}

	protected boolean setParams(String smsValue) {

		return true;
	}

}
