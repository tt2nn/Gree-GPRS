package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

/**
 * 故障点后传输时间
 * 
 * @author lihaotian
 *
 */
public class DebtModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Transmit_Error_End_Time / 60 + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setErrorEndTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
