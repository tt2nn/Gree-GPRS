package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

public class OntOneModel extends SmsBaseModel {

	protected String queryParams() {

		return (Configure.Transmit_Open_Start_Time / 60) + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setOpenStartTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
