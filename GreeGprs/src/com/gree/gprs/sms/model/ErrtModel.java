package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

/**
 * 故障点前传输时间
 * 
 * @author lihaotian
 *
 */
public class ErrtModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Transmit_Error_Start_Time / 60 + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setErrorStartTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
