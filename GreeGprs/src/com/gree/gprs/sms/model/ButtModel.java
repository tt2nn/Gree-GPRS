package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

/**
 * 按键调试周期
 * 
 * @author lihaotian
 *
 */
public class ButtModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Transmit_Pushkey_End_Time / 60 + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setPushKeyEndTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
