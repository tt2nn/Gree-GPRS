package com.gree.gprs.tcp;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.control.ControlCenter;
import com.gree.gprs.util.Logger;
import com.gree.gprs.variable.Variable;

public class TcpPing implements Runnable {

	private boolean privateIp = false;
	private static Object lock = new Object();
	private String host;

	private StreamConnection streamConnect;

	public void startPing(boolean privateIp) {

		this.privateIp = privateIp;

		if (privateIp) {

			host = "socket://" + Configure.Tcp_Address_Ip_Private + ":" + Configure.Tcp_Address_Port_Private;

		} else {

			host = "socket://" + Configure.Tcp_Address_Ip_Public + ":" + Configure.Tcp_Address_Port_Public;
		}

		new Thread(this).start();

	}

	public void run() {

		try {

			streamConnect = (StreamConnection) Connector.open(host);

			synchronized (lock) {

				if (privateIp) {

					Variable.setPrivateTcp();
					Variable.Gprs_Init_Success = true;
					Logger.logServer();

				} else {

					if (!Variable.Gprs_Init_Success) {

						Variable.setPublicTcp();
						Variable.Gprs_Init_Success = true;
						Logger.logServer();
					}
				}
			}

			ControlCenter.setGprsDialError(false);

		} catch (IOException e) {

			if (e.getMessage().equals("Initialize network error!\n")) {

				ControlCenter.setGprsDialError(true);
			}

			e.printStackTrace();

		} finally {

			if (streamConnect != null) {

				try {

					streamConnect.close();
					streamConnect = null;

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
