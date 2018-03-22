package com.gree.gprs;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.entity.Device;
import com.gree.gprs.file.FileReadModel;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.sms.SmsServer;
import com.gree.gprs.tcp.TcpPin;
import com.gree.gprs.timer.Timer;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class Boot {

	public static boolean Gprs_Running = true;

	public static void main(String[] args) {

		Logger.log("System Running", "Start Run");

		Gprs_Running = true;

		// start timer
		Timer.startTimer();
		ControlCenter.startControlTimer();
		Logger.startLogTimer();

		DeviceConfigure.deviceInit();
		// Spi.init(2048);
		GpioPin.gpioInit();
		GpioPin.closeAllLight();
		DataCenter.init();

		Configure.init();
		Variable.Gprs_Choosed = FileReadModel.queryGprsChooseState();
		Variable.Transmit_Cache_Type = FileReadModel.queryTransmitType();

		try {

			Thread.sleep(30 * 1000 - Variable.System_Time);

		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		DeviceConfigure.deviceInfo();

		Apn apn = Utils.getApn();
		DeviceConfigure.setApn(apn);

		Variable.Gprs_Mac[0] = Utils.stringToByte(Device.getInstance().getImei().substring(1, 3));
		Variable.Gprs_Mac[1] = Utils.stringToByte(Device.getInstance().getImei().substring(3, 5));
		Variable.Gprs_Mac[2] = Utils.stringToByte(Device.getInstance().getImei().substring(5, 7));
		Variable.Gprs_Mac[3] = Utils.stringToByte(Device.getInstance().getImei().substring(7, 9));
		Variable.Gprs_Mac[4] = Utils.stringToByte(Device.getInstance().getImei().substring(9, 11));
		Variable.Gprs_Mac[5] = Utils.stringToByte(Device.getInstance().getImei().substring(11, 13));
		Variable.Gprs_Mac[6] = Utils.stringToByte(Device.getInstance().getImei().substring(13, 15));

		try {

			new TcpPin().startPin(true);
			Thread.sleep(5000);
			new TcpPin().startPin(false);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SmsServer.startServer();
		DataCenter.startTransmit();

		// 等待所有线程销毁
		// waitThread(Timer.getTimerThread());
		// waitThread(ControlTimer.getControlTimerThread());
		// waitThread(DataCenter.getDataCenterThread());
		// waitThread(TcpServer.getTcpThread());
		// waitThread(SmsServer.getSmsThread());
		// waitThread(UartServer.getUartThread());
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
