package com.gree.gprs.sms.model;

import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.sms.SmsModel;

/**
 * 打卡上报
 * 
 * @author zhangzhuang
 *
 */
public class CheckStartModel {
	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		DataCenter.registerCheckTransmit();
		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Check_Start);
	}
}
