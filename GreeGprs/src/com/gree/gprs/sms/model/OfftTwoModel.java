package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

public class OfftTwoModel extends SmsBaseModel {

	protected String queryParams() {

		return (Configure.Transmit_Close_End_Time / 60) + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setCloseEndTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
