package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

public class OntTwoModel extends SmsBaseModel {

	protected String queryParams() {

		return (Configure.Transmit_Open_End_Time / 60) + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setOpenEndTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
