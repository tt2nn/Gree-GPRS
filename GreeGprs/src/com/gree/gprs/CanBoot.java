package com.gree.gprs;

import org.joshvm.j2me.dio.gpio.GPIOPinConfig;

import com.gree.gprs.can.CanServer;
import com.gree.gprs.can.delegate.CanDataDelegate;
import com.gree.gprs.can.delegate.CanSmsDelegate;
import com.gree.gprs.can.delegate.CanTcpDelegate;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.sms.SmsServer;
import com.gree.gprs.tcp.CmccLocation;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class CanBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "V1.21";
		Variable.App_Version_First = (byte) 0x01;
		Variable.App_Version_Second = (byte) 0x15;

		Variable.Gprs_Model = (byte) 0x05;
		Variable.Baud_Rate = 20000;
		Variable.Choose_Max_Number = 4;

		int[] gpioPinOutNumbers = { 20, 10, 11, 30, 38, 76 };
		Variable.Gpio_Pin_Out_Numbers = gpioPinOutNumbers;
		Variable.Gpio_Key_Trigger_Mode = GPIOPinConfig.TRIGGER_BOTH_LEVELS;

		DataCenter.setDataInterface(new CanDataDelegate());

		CanTcpDelegate canTcpDelegate = new CanTcpDelegate();
		TcpServer.setTcpServerInterface(canTcpDelegate);
		TransmitModel.setTcpTransmitInterface(canTcpDelegate);

		SmsServer.setServerInterface(new CanSmsDelegate());

		new CanBoot().init();
	}

	protected void initUart() {

	}

	protected void initCan() {

		Utils.pingServer();
		CmccLocation.SearchLocation();
	}

	protected void startUart() {

	}

	protected void startCan() {

		CanServer.startServer();
	}

}
