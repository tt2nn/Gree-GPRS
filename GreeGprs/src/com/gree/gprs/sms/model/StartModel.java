package com.gree.gprs.sms.model;

import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.sms.SmsModel;

/**
 * 服务器下发短信，开始GPRS连接服务器，并主动上传数据
 * 
 * @author lihaotian
 *
 */
public class StartModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		DataCenter.alwaysTransmit();
		SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_START);
	}

}
