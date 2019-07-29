package com.gree.gprs.product;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;

import com.gree.gprs.Boot;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.timer.Timer;
import com.gree.gprs.util.Logger;

public class Product {

	private static String netUrl = "";
	private static String uartHost = "";
	private static boolean uartState = false;
	private static boolean netState = false;

	/**
	 * 判断是否进入生产模式
	 * 
	 * @return
	 */
	public static boolean checkProductModel() {

		try {

			// 判断文件是否存在
			FileConnection fileConn = (FileConnection) Connector.open("file:///Phone/secure/product.txt");
			if (fileConn != null && fileConn.exists()) {

				System.out.println("start product model");

				InputStream inputStream = fileConn.openInputStream();
				byte[] buffer = new byte[1024];
				int len = inputStream.read(buffer);

				if (len > 0) {

					String setting = new String(buffer, 0, len);

					// 获取文件中 配置的 网络地址 和 串口地址
					if (setting != null && setting.length() > 0) {

						int slip = setting.indexOf("\r\n");

						if (slip > 0) {

							netUrl = setting.substring(0, slip);
							uartHost = setting.substring(slip + 2, setting.length());

							System.out.println("netUrl == " + netUrl);
							System.out.println("uartHost == " + uartHost);

							return true;
						}
					}
				}
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

		// 启动计时器
		Timer.startTimer();

		// 初始化GPIO
		DeviceConfigure.deviceInit();
		GpioPin.gpioInit();

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 获取设备信息
		DeviceConfigure.deviceInfo();
		Logger.logDeviceInfo();

		// 点亮所有的灯
		GpioPin.openAllLight();

		// 判断是否按键
		while (ControlCenter.Push_Key_Time == 0) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// 关闭所有的灯
		GpioPin.closeAllLight();

		// 检查串口与网络通信
		checkUart();
		checkTcp();

		// 判断网络与串口状态
		while (!netState || !uartState) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// 启动跑马灯
		loopLight();

		// 删除文件
		com.gree.gprs.file.FileConnection.deleteFile("secure/product.txt");
	}

	/**
	 * 循环亮灯
	 */
	private static void loopLight() {

		new Thread(new Runnable() {

			public void run() {

				int loopIndex = 0;

				while (Boot.Gprs_Running) {

					try {
						Thread.sleep(500);
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

					loopIndex++;

					if (loopIndex > 4) {

						loopIndex = 0;
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

				while (!uartState) {

					try {

						String host = uartHost;

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

					try {
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	/**
	 * 网络
	 */
	private static void checkTcp() {

		System.out.println("start tcp-----");

		String host = "socket://" + netUrl;

		while (!netState) {

			try {

				StreamConnection streamConnect = (StreamConnection) Connector.open(host);

				System.out.println("tcp connect ------");

				netState = true;

				streamConnect.close();

				break;

			} catch (IOException e) {

				e.printStackTrace();
			}

			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
