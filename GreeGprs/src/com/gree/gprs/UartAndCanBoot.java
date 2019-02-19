package com.gree.gprs;

import org.joshvm.ams.jams.NetworkStatusMonitor;
import org.joshvm.j2me.dio.DeviceManager;
import org.joshvm.j2me.dio.gpio.GPIOPin;
import org.joshvm.j2me.dio.gpio.GPIOPinConfig;

import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.uart.UartServer;
import com.gree.gprs.uart.delegate.UartControlDelegate;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class UartAndCanBoot extends Boot {

	public static void main(String[] args) {

		Init_M3_GREENLAND(false);

		Variable.App_Version = "v0.10";
		Variable.App_Version_First = (byte) 0x00;
		Variable.App_Version_Second = (byte) 0x0A;

		Variable.Gprs_Model = (byte) 0x11;

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

	/**
	 * m3启动模式
	 * 
	 * @param programming
	 */
	private static void Init_M3_GREENLAND(boolean programming) {

		try {
			GPIOPinConfig cfg = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 29, // UART0_DTR
					GPIOPinConfig.DIR_OUTPUT_ONLY, GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE,
					false);
			GPIOPin pinm3boot0 = (GPIOPin) DeviceManager.open(cfg, DeviceManager.EXCLUSIVE);

			cfg = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 4, // UART0_CTS
					GPIOPinConfig.DIR_OUTPUT_ONLY, GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE,
					true);
			GPIOPin pinm3reset = (GPIOPin) DeviceManager.open(cfg, DeviceManager.EXCLUSIVE);

			pinm3reset.setValue(true);
			Thread.sleep(1000);
			pinm3boot0.setValue(programming);
			Thread.sleep(1000);
			pinm3reset.setValue(false);
			Thread.sleep(1000);
			pinm3reset.setValue(true);
			Thread.sleep(1000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
