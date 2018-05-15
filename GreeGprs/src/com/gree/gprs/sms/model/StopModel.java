package com.gree.gprs.sms.model;

import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.data.DataCenter;

/**
 * 服务器下发短信 GPRS模块断开连接
 * 
 * @author lihaotian
 *
 */
public class StopModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		DataCenter.destoryTransmit();
		com.gree.gprs.sms.SmsModel.buildMessageOk(SmsConstant.SMS_TYPE_STOP);
	}

}
