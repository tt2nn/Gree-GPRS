package com.gree.gprs.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.gprs.Boot;
import com.gree.gprs.configure.DeviceConfigure;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.entity.Apn;
import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

/**
 * TCP服务
 * 
 * @author lihaotian
 *
 */
public class TcpServer implements Runnable {

	private static StreamConnection streamConnect;
	private static InputStream inputStream;
	private static OutputStream outputStream;

	private static boolean serverWorking = false;
	private static boolean serverNormal = false;

	private static int Server_ReConnect_Num = 0;
	private static int Write_Error_Num = 0;
	private static final int Server_ReConnect_Short_Time = 10 * 1000;
	private static final int Server_ReConnect_Long_Time = 30 * 1000;

	private static Thread tcpThread;

	/**
	 * 启动服务器
	 */
	public static void startServer() {

		if (Variable.Change_Vpn) {

			Variable.Change_Vpn = false;

			Apn apn = Utils.getApn();
			DeviceConfigure.setApn(apn);
		}

		serverWorking = true;
		Server_ReConnect_Num = 0;

		TcpServer tcpServer = new TcpServer();

		tcpThread = new Thread(tcpServer);
		tcpThread.start();
	}

	public void run() {

		while (Boot.Gprs_Running && serverWorking) {

			try {

				String host = "socket://" + Variable.Tcp_Address_Ip + ":" + Variable.Tcp_Address_Port;

				streamConnect = (StreamConnection) Connector.open(host);

				outputStream = streamConnect.openOutputStream();
				inputStream = streamConnect.openInputStream();

				Logger.log("Tcp Server", "---- Start Tcp Server ----");

				serverNormal = true;

				if (!Variable.Gprs_Login) {

					ControlCenter.login();
				}

				receiveData();

			} catch (IOException ioe) {

				ioe.printStackTrace();

			} finally {

				if (serverWorking) {

					Logger.log("Tcp Server", "---- Tcp Server Error ----");

					try {

						DataCenter.pauseTransmit();
						closeStream();

						if (Server_ReConnect_Num == 5) {

							if (Variable.GPRS_ERROR_TYPE == Constant.GPRS_ERROR_TYPE_NO) {

								Variable.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_SERVER;
							}
						}
						Server_ReConnect_Num++;

						if (Server_ReConnect_Num > 5) {

							Thread.sleep(Server_ReConnect_Long_Time);

						} else {

							Thread.sleep(Server_ReConnect_Short_Time);
						}
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				} else {
					Logger.log("Tcp Server", "---- Tcp Server Stop ----");
				}

				clearStream();
			}
		}
	}

	/**
	 * 数据获取
	 */
	private static void receiveData() throws IOException {

		if (inputStream != null) {

			int total = 0;
			while ((total = inputStream.read(Variable.Tcp_In_Buffer)) != -1) {

				Logger.log("Tcp Get Message", Variable.Tcp_In_Buffer, 0, total);

				TcpModel.analyze();
			}
		}
	}

	/**
	 * 发送数据
	 */
	public static synchronized void sendData(byte[] data, int length) {

		try {

			if (outputStream != null) {

				outputStream.write(data, 0, length);
				Server_ReConnect_Num = 0;
				Write_Error_Num = 0;
			}

		} catch (IOException e) {

			e.printStackTrace();
			
			Write_Error_Num++;
			if (Write_Error_Num == 5) {

				closeStream();
			}
		}
	}

	/**
	 * 关闭流
	 */
	private static void closeStream() {

		serverNormal = false;

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

		serverWorking = false;
		closeStream();
	}

	public static boolean isServerNormal() {
		return serverNormal;
	}

	public static boolean isServerWorking() {
		return serverWorking;
	}

	public static Thread getTcpThread() {
		return tcpThread;
	}

}
