package com.gree.gprs;

import org.joshvm.ams.jams.NetworkStatusMonitor;

import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.uart.UartServer;
import com.gree.gprs.uart.delegate.UartControlDelegate;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class UartAndCanBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "v0.3";
		Variable.App_Version_First = (byte) 0x00;
		Variable.App_Version_Second = (byte) 0x03;

		int[] gpioPinOutNumbers = { 8, 22, 23, 7, 9, 6 };
		Variable.Gpio_Pin_Out_Numbers = gpioPinOutNumbers;

		ControlCenter.setControlInterface(new UartControlDelegate());

		new UartAndCanBoot().init();
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

				Utils.pingServer();
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
