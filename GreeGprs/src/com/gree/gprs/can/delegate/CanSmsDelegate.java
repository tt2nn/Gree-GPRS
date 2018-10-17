package com.gree.gprs.can.delegate;

import com.gree.gprs.sms.SmsModel;
import com.gree.gprs.sms.SmsServer.SmsServerInterface;
import com.gree.gprs.util.Utils;

public class CanSmsDelegate implements SmsServerInterface {

	public void checkPhoneNumber() {

		if (Utils.stringContains(SmsModel.Sms_Address, "1069800006512610")) {

			int poi = SmsModel.Sms_Address.indexOf("1069800006512610");
			String newAddress = SmsModel.Sms_Address.substring(0, poi) + "86"
					+ SmsModel.Sms_Address.substring(poi, SmsModel.Sms_Address.length());

			SmsModel.Sms_Address = newAddress;
		}
	}

}
