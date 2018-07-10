package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;

/**
 * 短信密码
 * 
 * @author lihaotian
 *
 */
public class PwdModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Sms_Pwd;
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setSmsPwd(smsValue)) {

			return true;
		}

		return false;
	}

}