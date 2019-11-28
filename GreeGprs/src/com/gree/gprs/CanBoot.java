package com.gree.gprs;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import org.joshvm.j2me.dio.gpio.GPIOPinConfig;

import com.gree.gprs.can.CanServer;
import com.gree.gprs.can.delegate.CanDataDelegate;
import com.gree.gprs.can.delegate.CanSmsDelegate;
import com.gree.gprs.can.delegate.CanTcpDelegate;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.sms.SmsServer;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.timer.FeedDogTimer;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class CanBoot extends Boot {

	public static void main(String[] args) {

		Variable.App_Version = "V1.30";
		Variable.App_Version_First = (byte) 0x01;
		Variable.App_Version_Second = (byte) 0x1E;

		Variable.Gprs_Model = (byte) 0x05;
		Variable.Gprs_Net_Generation = (byte) 0x04;
		Variable.Baud_Rate = 20000;
		Variable.Choose_Max_Number = 4;

		int[] gpioPinOutNumbers = { 20, 10, 11, 30, 38, 76 };
		Variable.Gpio_Pin_Out_Numbers = gpioPinOutNumbers;
		Variable.Gpio_Key_Trigger_Mode = GPIOPinConfig.TRIGGER_BOTH_LEVELS;

		DataCenter.setDataInterface(new CanDataDelegate());

		CanTcpDelegate canTcpDelegate = new CanTcpDelegate();
		TcpServer.setTcpServerInterface(canTcpDelegate);
		TransmitModel.setTcpTransmitInterface(canTcpDelegate);

		SmsServer.setServerInterface(new CanSmsDelegate());

		new CanBoot().init();
	}

	protected void initUart() {

	}

	protected void initCan() {

		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Utils.pingServer();
		clearTempLog();
		// CmccLocation.SearchLocation();
	}

	protected void startUart() {

	}

	protected void startCan() {

		CanServer.startServer();
	}

	protected void boot() {

		new FeedDogTimer().startTimer();
	}

	/**
	 * 4g清除Log机制生成的临时文件
	 */
	private void clearTempLog() {

		new Thread(new Runnable() {

			public void run() {

				while (Gprs_Running) {

					System.out.println("clearTempLog ------");

					try {
						FileConnection fileConnection = (FileConnection) Connector.open("file:///Phone/");
						Enumeration enumeration = fileConnection.list();
						while (enumeration.hasMoreElements()) {

							String fileName = (String) enumeration.nextElement();
							if (fileName.indexOf("log.txt") != -1 && !fileName.equals("log.txt")) {

								System.out.println("clearTempLog ---fileName--- " + fileName);
								com.gree.gprs.file.FileConnection.deleteFile(fileName);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
