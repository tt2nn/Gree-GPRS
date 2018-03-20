package com.gree.gprs.sms;

import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.util.Utils;

public abstract class SmsBaseModel {

	/**
	 * 解析收到的短信
	 */
	public void smsAnalyze() {

		if (Utils.stringContains(SmsModel.Sms_Message, SmsConstant.Sms_Query_Symbol)) {

			queryParams();

		} else {

			if (SmsModel.isAdmin()) {

				setParams(SmsModel.smsGetValue(SmsModel.Sms_Message));
			}
		}
	}

	/**
	 * 服务器查询GPRS信息
	 */
	protected abstract void queryParams();

	/**
	 * 服务器设置GPRS信息
	 * 
	 * @param smsValue
	 */
	protected abstract void setParams(String smsValue);

}
