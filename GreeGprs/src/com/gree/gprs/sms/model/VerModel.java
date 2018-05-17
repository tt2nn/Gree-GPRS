package com.gree.gprs.sms.model;

import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.variable.Variable;

/**
 * 服务器 查询DTU软件版本
 * 
 * @author lihaotian
 *
 */
public class VerModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		SmsModel.buildMessage(SmsConstant.SMS_TYPE_VER, Variable.App_Version);
	}

}
