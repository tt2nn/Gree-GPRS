package com.gree.gprs.product;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;

import org.joshvm.ams.jams.NetworkStatusMonitor;

import com.gree.gprs.Boot;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.entity.Device;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.timer.Timer;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;

public class Product {

	private static int loopIndex = 0;
	private static boolean loopOrder = true;
	private static boolean loopState = true;

	private static boolean uartState = false;
	private static boolean tcpState = false;

	private static int productNo = 0;

	/**
	 * 判断是否进入生产模式
	 * 
	 * @return
	 */
	public static boolean checkProductModel() {

		try {

			FileConnection fileConn = (FileConnection) Connector.open("file:///Phone/secure/product1.txt");
			if (fileConn != null && fileConn.exists()) {

				productNo = 1;
				System.out.println("start product model");
				return true;
			}

			FileConnection fileConn1 = (FileConnection) Connector.open("file:///Phone/secure/product2.txt");
			if (fileConn1 != null && fileConn1.exists()) {

				productNo = 2;
				System.out.println("start product model other");
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
		checkUart();

		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("111111111111");
		DeviceConfigure.deviceInfo();
		System.out.println("222222222222");
		Logger.logDeviceInfo();
		System.out.println("333333333333");
		Apn apn = Utils.getApn();
		System.out.println("444444444444");
		DeviceConfigure.setApn(apn);
		System.out.println("555555555555");

		new Thread(new Runnable() {

			public void run() {

				int i = 0;
				while (i < 10 && NetworkStatusMonitor.requestStatus() == NetworkStatusMonitor.CONNECTING) {

					try {
						Thread.sleep(1000);
						i++;
						System.out.println("66666666666");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.out.println("77777777777");
				checkTcp();

				loopState = false;

				if (tcpState && uartState) {

					com.gree.gprs.file.FileConnection.deleteFile("secure/product" + productNo + ".txt");
				}
			}
		}).start();
	}

	/**
	 * 循环亮灯
	 */
	private static void loopLight() {

		new Thread(new Runnable() {

			public void run() {

				while (Boot.Gprs_Running) {

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					GpioPin.closeAllLight();

					if (loopState) {

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

					} else {

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						GpioPin.openTransmit();
						GpioPin.openHight();
						GpioPin.openMiddle();
						GpioPin.openLow();
						if (!tcpState || !uartState) {

							GpioPin.openError();
						}
					}
				}
			}

		}).start();
	}

	/**
	 * 检测串口
	 */
	private static void checkUart() {

		System.out.println("start uart-----");

		new Thread(new Runnable() {

			public void run() {

				try {

					String host = "comm:COM1;baudrate=9600";

					StreamConnection streamConnect = (StreamConnection) Connector.open(host);
					InputStream inputStream = streamConnect.openInputStream();

					System.out.println("uart connect-----");

					byte[] readBuffer = new byte[256];
					int readLength = 0;
					while ((readLength = inputStream.read(readBuffer)) != -1) {

						if (readLength > 0) {

							uartState = true;
						}
					}

				} catch (Exception e) {

					e.printStackTrace();
				}
			}

		}).start();
	}

	/**
	 * 网络
	 */
	private static void checkTcp() {

		System.out.println("start tcp-----");

		String host = "socket://118.190.93.145:9879";

		try {

			StreamConnection streamConnect = (StreamConnection) Connector.open(host);
			OutputStream outputStream = streamConnect.openOutputStream();

			System.out.println("tcp connect-----");

			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(Device.getInstance().getImei());
			stringBuffer.append(",");
			stringBuffer.append(Device.getInstance().getIccid());
			stringBuffer.append(",");
			stringBuffer.append(DeviceConfigure.getNetworkSignalLevel());
			stringBuffer.append(",");
			stringBuffer.append(uartState);
			stringBuffer.append(",");
			stringBuffer.append(productNo);
			stringBuffer.append(";\n\r");

			outputStream.write(stringBuffer.toString().getBytes());
			outputStream.flush();
			outputStream.close();

			tcpState = true;

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
