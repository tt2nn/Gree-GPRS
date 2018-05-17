package com.gree.gprs;

import com.gree.gprs.can.CanServer;
import com.gree.gprs.can.delegate.CanDataDelegate;
import com.gree.gprs.can.delegate.CanTcpDelegate;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.variable.Variable;

public class CanBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "V0.2";
		Variable.App_Version_First = (byte) 0x00;
		Variable.App_Version_Second = (byte) 0x02;

		new UartBoot().init();
	}

	protected void initUart() {

	}

	protected void initCan() {

		DataCenter.setDataInterface(new CanDataDelegate());
		TransmitModel.setTcpTransmitInterface(new CanTcpDelegate());
	}

	protected void startUart() {

	}

	protected void startCan() {

		CanServer.startServer();
	}

}
