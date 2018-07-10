package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.util.Utils;

/**
 * 信号信息周期
 * 
 * @author zhangzhuang
 *
 */
public class SigModel extends SmsBaseModel {

	protected String queryParams() {

		return Configure.Tcp_Sig_Period / 60 + "";
	}

	protected boolean setParams(String smsValue) {

		if (Configure.setSigPeriodTime(Utils.stringToInt(smsValue) * 60)) {

			return true;
		}

		return false;
	}

}
