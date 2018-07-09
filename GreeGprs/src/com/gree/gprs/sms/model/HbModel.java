package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

/**
 * 心跳间隔
 * 
 * @author lihaotian
 *
 */
public class HbModel extends SmsBaseModel {

	protected String queryParams() {

		String value1 = "heart,0,";
		return value1 + Configure.Tcp_Heart_Beat_Period;
	}

	protected boolean setParams(String smsValue) {

		String split = "heart,0,";
		int start = smsValue.indexOf(split);
		if (start >= 0) {

			start += split.length();
			int end = smsValue.length();

			if (end > start) {

				String second = smsValue.substring(start, end);

				if (Configure.setHbPeriodTime(Utils.stringToInt(second))) {

					return true;
				}
			}
		}

		return false;
	}

}
