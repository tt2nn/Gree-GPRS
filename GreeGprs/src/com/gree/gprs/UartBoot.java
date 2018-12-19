package com.gree.gprs;

import org.joshvm.ams.jams.NetworkStatusMonitor;

import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.uart.UartServer;
import com.gree.gprs.uart.delegate.UartControlDelegate;
import com.gree.gprs.uart.delegate.UartDataDelegate;
import com.gree.gprs.uart.delegate.UartTcpDelegate;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class UartBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "V1.30";
		Variable.App_Version_First = (byte) 0x01;
		Variable.App_Version_Second = (byte) 0x1E;

		ControlCenter.setControlInterface(new UartControlDelegate());
		DataCenter.setDataInterface(new UartDataDelegate());

		UartTcpDelegate uartTcpDelegate = new UartTcpDelegate();
		TcpServer.setTcpServerInterface(uartTcpDelegate);
		TransmitModel.setTcpTransmitInterface(uartTcpDelegate);

		new UartBoot().init();
	}

	protected void initUart() {

		UartModel.init();

		new Thread(new Runnable() {

			public void run() {

				while (NetworkStatusMonitor.requestStatus() == NetworkStatusMonitor.CONNECTING) {

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Utils.pingServer();
				// CmccLocation.SearchLocation();
			}
		}).start();
	}

	protected void initCan() {

	}

	protected void startUart() {

		UartServer.startServer();
	}

	protected void startCan() {

	}

}
