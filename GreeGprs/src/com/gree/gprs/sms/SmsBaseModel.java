package com.gree.gprs.sms;

import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.util.Utils;

public abstract class SmsBaseModel {

	/**
	 * 解析收到的短信
	 * 
	 * @param type
	 */
	public void smsAnalyze(String type) {

		if (SmsModel.smsGetValue(SmsModel.Sms_Message).equals(SmsConstant.SMS_QUERY_SYMBOL)) {

			String message = queryParams();
			if (Utils.isNotEmpty(message)) {

				SmsModel.buildMessage(type, message);
			}

		} else {

			if (SmsModel.isAdmin()) {

				if (!Utils.isNotEmpty(type)) {

					SmsModel.buildMessageError(type);
					return;
				}

				if (setParams(SmsModel.smsGetValue(SmsModel.Sms_Message))) {

					SmsModel.buildMessageSetOk(type);

				} else {

					SmsModel.buildMessageError(type);
				}
			}
		}
	}

	/**
	 * 服务器查询GPRS信息
	 * 
	 * @return
	 */
	protected abstract String queryParams();

	/**
	 * 服务器设置GPRS信息
	 * 
	 * @param smsValue
	 * @return
	 */
	protected abstract boolean setParams(String smsValue);

}
