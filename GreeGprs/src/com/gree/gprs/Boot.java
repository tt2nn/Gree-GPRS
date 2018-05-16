package com.gree.gprs;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.entity.Device;
import com.gree.gprs.file.FileReadModel;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.sms.SmsServer;
import com.gree.gprs.spi.Spi;
import com.gree.gprs.timer.Timer;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class Boot {

	public static boolean Gprs_Running = true;

	public static void main(String[] args) {

		Logger.log("System Running", " Version : " + Constant.APP_VERSION);

		Gprs_Running = true;

		// start timer
		Timer.startTimer();
		ControlCenter.startControlTimer();
		Logger.startLogTimer();

		DeviceConfigure.deviceInit();
		GpioPin.gpioInit();

		controlLight();

		DataCenter.init();

		initConfigure();

		try {

			Thread.sleep(30 * 1000 - Variable.System_Time);

		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		DeviceConfigure.deviceInfo();
		Logger.logDeviceInfo();

		Apn apn = Utils.getApn();
		DeviceConfigure.setApn(apn);
		Logger.logApn();

		getMac();

		initUart();
		initCan();

		Utils.pingServer();

		SmsServer.startServer();
		DataCenter.startTransmit();
	}

	/**
	 * Boot Control Light
	 */
	private static void controlLight() {

		GpioPin.openAllLight();
		new Thread(new Runnable() {

			public void run() {

				try {
					Thread.sleep(1 * 1000);
					GpioPin.closeAllLight();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * InitConfigure
	 */
	private static void initConfigure() {

		Configure.init();
		Variable.Gprs_Choosed = FileReadModel.queryGprsChooseState();
		Variable.Transmit_Cache_Type = FileReadModel.queryTransmitType();
		Logger.logConfigure();
		Logger.logSms();
	}

	/**
	 * Get Mac
	 */
	private static void getMac() {

		Variable.Gprs_Mac[0] = Utils.stringToByte(Device.getInstance().getImei().substring(1, 3));
		Variable.Gprs_Mac[1] = Utils.stringToByte(Device.getInstance().getImei().substring(3, 5));
		Variable.Gprs_Mac[2] = Utils.stringToByte(Device.getInstance().getImei().substring(5, 7));
		Variable.Gprs_Mac[3] = Utils.stringToByte(Device.getInstance().getImei().substring(7, 9));
		Variable.Gprs_Mac[4] = Utils.stringToByte(Device.getInstance().getImei().substring(9, 11));
		Variable.Gprs_Mac[5] = Utils.stringToByte(Device.getInstance().getImei().substring(11, 13));
		Variable.Gprs_Mac[6] = Utils.stringToByte(Device.getInstance().getImei().substring(13, 15));
	}

	/**
	 * Init Uart
	 */
	private static void initUart() {

		Spi.init(2048);
	}

	/**
	 * Init Can
	 */
	private static void initCan() {

	}

	/**
	 * start uart
	 */
	private static void startUart() {

	}

	/**
	 * start uart
	 */
	private static void startCan() {

	}

	/**
	 * 验证线程销毁
	 * 
	 * @param runThread
	 */
	private static void waitThread(Thread runThread) {

		if (runThread != null) {

			try {
				runThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
