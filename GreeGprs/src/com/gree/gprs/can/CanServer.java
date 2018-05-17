package com.gree.gprs.can;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.gprs.Boot;
import com.gree.gprs.util.Logger;

public class CanServer implements Runnable {

	private static StreamConnection streamConnect;
	private static InputStream inputStream;
	private static OutputStream outputStream;

	/**
	 * start can server
	 */
	public static void startServer() {

		CanServer uartServer = new CanServer();
		new Thread(uartServer).start();
	}

	public void run() {

		while (Boot.Gprs_Running) {

			try {

				String host = "can:can0;baudrate=20000";

				streamConnect = (StreamConnection) Connector.open(host);

				inputStream = streamConnect.openInputStream();
				outputStream = streamConnect.openOutputStream();

				Logger.log("Can Server", "---- Start Can Server ----");

				receiveData();

			} catch (IOException ioe) {

				ioe.printStackTrace();

			} finally {

				stopServer();
				clearStream();
			}
		}
	}

	/**
	 * 数据获取
	 */
	private static void receiveData() {

		try {

			if (inputStream != null) {

				int total = 0;
				while ((total = inputStream.read(CanModel.Can_Data_In_Buffer)) != -1) {

					// Logger.log("Can Get Message", CanModel.Can_Data_In_Buffer, 0, total);

					CanModel.Can_Data_Length = total;
					CanModel.analyze();
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 发送数据
	 */
	public static void sendData(int length) {

		try {

			if (outputStream != null) {

				outputStream.write(CanModel.Can_Data_Out_Buffer, 0, length);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 关闭流
	 */
	private static void closeStream() {

		try {

			if (inputStream != null) {

				inputStream.close();
			}

			if (outputStream != null) {

				outputStream.close();
			}

			if (streamConnect != null) {

				streamConnect.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清空流
	 */
	private static void clearStream() {

		inputStream = null;
		outputStream = null;
		streamConnect = null;
	}

	/**
	 * 停止服务
	 */
	public static void stopServer() {

		closeStream();
	}

}
