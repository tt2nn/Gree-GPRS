package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

/**
 * 打卡上报周期
 * 
 * @author zhangzhuang
 *
 */
public class CheckPeriodModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Transmit_Check_Period / 60 + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setCheckPeriodTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
