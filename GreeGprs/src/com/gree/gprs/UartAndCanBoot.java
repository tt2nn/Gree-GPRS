package com.gree.gprs;

import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.uart.UartServer;
import com.gree.gprs.uart.delegate.UartControlDelegate;
import com.gree.gprs.uart.delegate.UartDataDelegate;
import com.gree.gprs.variable.Variable;

public class UartAndCanBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "v0.1";
		Variable.App_Version_First = (byte) 0x00;
		Variable.App_Version_Second = (byte) 0x01;

		ControlCenter.setControlInterface(new UartControlDelegate());
		DataCenter.setDataInterface(new UartDataDelegate());

		new UartBoot().init();
	}

	protected void initUart() {

		UartModel.init();
	}

	protected void initCan() {

	}

	protected void startUart() {

		UartServer.startServer();
	}

	protected void startCan() {

	}

}
