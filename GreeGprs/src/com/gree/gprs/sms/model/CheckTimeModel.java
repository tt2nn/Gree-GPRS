package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

/**
 * 打卡上报时长
 * 
 * @author zhangzhuang
 *
 */
public class CheckTimeModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Transmit_Check_End_Time + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setCheckEndTime(Utils.stringToInt(smsValue))) {

			return true;
		}

		return false;
	}

}
