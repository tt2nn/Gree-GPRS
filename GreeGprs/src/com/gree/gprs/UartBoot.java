package com.gree.gprs;

import com.gree.gprs.data.DataCenter;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.uart.UartServer;
import com.gree.gprs.uart.delegate.UartDataDelegate;
import com.gree.gprs.uart.delegate.UartTcpDelegate;
import com.gree.gprs.variable.Variable;

public class UartBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "V1.6";
		Variable.App_Version_First = (byte) 0x01;
		Variable.App_Version_Second = (byte) 0x06;

		new UartBoot().init();

	}

	protected void initUart() {

		DataCenter.setDataInterface(new UartDataDelegate());
		TransmitModel.setTcpTransmitInterface(new UartTcpDelegate());
	}

	protected void initCan() {

	}

	protected void startUart() {

		UartServer.startServer();
	}

	protected void startCan() {

	}

}
