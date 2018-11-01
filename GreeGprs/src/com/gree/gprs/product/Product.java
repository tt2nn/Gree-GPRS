package com.gree.gprs.product;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.timer.Timer;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class Product {

	private static int loopIndex = 0;
	private static boolean loopOrder = true;
	static boolean canLoop = true;

	/**
	 * 判断是否进入生产模式
	 * 
	 * @return
	 */
	public static boolean checkProductModel() {

		try {

			FileConnection fileConn = (FileConnection) Connector.open("file:///Phone/secure/product.txt");
			if (fileConn != null && fileConn.exists()) {

				return true;
			}

		} catch (IOException e1) {

			e1.printStackTrace();
		}

		return false;
	}

	/**
	 * 启动生产模式
	 */
	public static void startProductModel() {

		Timer.startTimer();
		DeviceConfigure.deviceInit();
		GpioPin.gpioInit();

		loopLight();

		try {

			Thread.sleep(20 * 1000 - Variable.System_Time);

		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		DeviceConfigure.deviceInfo();
		Logger.logDeviceInfo();

		Apn apn = Utils.getApn();
		DeviceConfigure.setApn(apn);
	}

	/**
	 * 循环亮灯
	 */
	private static void loopLight() {

		new Thread(new Runnable() {

			public void run() {

				while (canLoop) {

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					GpioPin.closeAllLight();

					switch (loopIndex) {

					case 0:
						GpioPin.openError();
						break;

					case 1:
						GpioPin.openTransmit();
						break;

					case 2:
						GpioPin.openHight();
						break;

					case 3:
						GpioPin.openMiddle();
						break;

					case 4:
						GpioPin.openLow();
						break;
					}

					if (ControlCenter.Push_Key_Time > 0) {

						loopOrder = !loopOrder;
						ControlCenter.Push_Key_Time = 0;
					}

					if (loopOrder) {

						loopIndex++;

					} else {

						loopIndex--;
					}

					if (loopIndex > 4) {

						loopIndex = 0;
					}

					if (loopIndex < 0) {

						loopIndex = 4;
					}
				}
			}

		}).start();
	}

}
