package com.gree.gprs.sms.model;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.constant.SmsConstant;
import com.gree.gprs.sms.SmsBaseModel;
import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.variable.Variable;

/**
 * 域名、IP，端口
 * 
 * @author lihaotian
 *
 */
public class ServModel extends SmsBaseModel {

	protected void queryParams() {

		String smsValue = Variable.Tcp_Address_Ip + SmsConstant.Sms_Split_Value_Symbol + Variable.Tcp_Address_Port;
		SmsModel.buildMessage(SmsConstant.Sms_Type_Serv, smsValue);
	}

	protected void setParams(String smsValue) {

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);

		if (end < smsValue.length()) {

			String ip = smsValue.substring(start, end);

			start = end + 1;
			end = smsValue.length();

			String port = smsValue.substring(start, end);

			if (Configure.setTcpAddress(Variable.Tcp_Address_Private, ip, port)) {

				SmsModel.buildMessageOk(SmsConstant.Sms_Type_Serv);
				return;
			}
		}

		SmsModel.buildMessageError(SmsConstant.Sms_Type_Serv);
	}

}
