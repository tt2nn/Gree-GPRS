package com.gree.gprs.tcp;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.gprs.configure.Configure;
import com.gree.gprs.util.Logger;
import com.gree.gprs.variable.Variable;

public class TcpPin implements Runnable {

	private boolean privateIp = false;
	private String host;

	private StreamConnection streamConnect;

	public void startPin(boolean privateIp) {

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

			if (privateIp) {

				Variable.setPrivateTcp();

			} else {

				Variable.setPublicTcp();
			}

			Variable.Gprs_Init_Success = true;
			Logger.logServer();

		} catch (IOException e) {

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
