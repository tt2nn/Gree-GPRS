package com.gree.gprs;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.entity.Device;
import com.gree.gprs.file.FileReadModel;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.product.Product;
import com.gree.gprs.sms.SmsServer;
import com.gree.gprs.timer.Timer;
import com.gree.gprs.timer.TransmitTimer;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public abstract class Boot {

	public static boolean Gprs_Running = true;

	public void init() {

		boot();

		if (Product.checkProductModel()) {

			Product.startProductModel();
			return;
		}

		Logger.log("System Running", " Version : " + Variable.App_Version);

		Gprs_Running = true;

		// start timer
		Timer.startTimer();
		Logger.startLogTimer();

		DeviceConfigure.deviceInit();
		GpioPin.gpioInit();
		DataCenter.init();
		initConfigure();

		TransmitTimer.startTimer();

		try {

			Thread.sleep(10 * 1000 - Variable.System_Time);

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

		SmsServer.startServer();
		DataCenter.startTransmit();

		startUart();
		startCan();
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
	 * Boot
	 */
	protected abstract void boot();

	/**
	 * Init Uart
	 */
	protected abstract void initUart();

	/**
	 * Init Can
	 */
	protected abstract void initCan();

	/**
	 * start uart
	 */
	protected abstract void startUart();

	/**
	 * start uart
	 */
	protected abstract void startCan();

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
