package com.gree.gprs;

import com.gree.gprs.can.CanServer;
import com.gree.gprs.can.delegate.CanDataDelegate;
import com.gree.gprs.can.delegate.CanTcpDelegate;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.variable.Variable;

public class CanBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "V1.12";
		Variable.App_Version_First = (byte) 0x01;
		Variable.App_Version_Second = (byte) 0x0C;

		Variable.Gprs_Model = (byte) 0x05;
		Variable.Baud_Rate = 20000;

		int[] gpioPinOutNumbers = { 20, 10, 11, 30, 38, 76 };
		Variable.Gpio_Pin_Out_Numbers = gpioPinOutNumbers;

		DataCenter.setDataInterface(new CanDataDelegate());
		TransmitModel.setTcpTransmitInterface(new CanTcpDelegate());

		new CanBoot().init();
	}

	protected void initUart() {

	}

	protected void initCan() {
	}

	protected void startUart() {

	}

	protected void startCan() {

		CanServer.startServer();
	}

}
