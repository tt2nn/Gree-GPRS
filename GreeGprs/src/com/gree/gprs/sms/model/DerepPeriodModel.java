package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

public class DerepPeriodModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Transmit_Derep_Period / 60 + "";
	}

	protected boolean setParams(String smsValue) {

		return Configure.setDerepPeriodTime(Utils.stringToInt(smsValue) * 60);
	}

}
