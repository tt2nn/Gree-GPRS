package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

/**
 * 厂家参数改变前传输结束时间
 * 
 * @author lihaotian
 *
 */
public class HealtModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Transmit_Change_Start_Time / 60 + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setChangeStartTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
