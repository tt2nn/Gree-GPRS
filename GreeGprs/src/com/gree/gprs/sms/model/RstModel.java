package com.gree.gprs.sms.model;

import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.sms.SmsModel;

/**
 * 服务器 发送短信至 GPRS 复位DTU
 * 
 * @author lihaotian
 *
 */
public class RstModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Rst);
		ControlCenter.resetSystem();
	}

}
