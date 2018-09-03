package com.gree.gprs.sms;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

import com.gree.gprs.util.Logger;
import com.gree.gprs.util.Utils;

/**
 * 短信服务
 * 
 * @author lihaotian
 *
 */
public class SmsServer implements Runnable, MessageListener {

	private static MessageConnection msgconn;
	private Message message;

	/**
	 * 启动短信服务
	 */
	public static void startServer() {

		try {

			String address = "sms://:0";
			msgconn = (MessageConnection) Connector.open(address, Connector.READ);
			msgconn.setMessageListener(new SmsServer());

			Logger.log("SMS Server", "---- Start Sms Server ----");

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void notifyIncomingMessage(MessageConnection arg0) {

		new Thread(new SmsServer()).start();
	}

	public void run() {

		try {

			if (msgconn != null) {

				message = msgconn.receive();

				if (message != null) {

					SmsModel.Sms_Address = message.getAddress();

					if (message instanceof TextMessage) {

						SmsModel.Sms_Message = ((TextMessage) message).getPayloadText();
						SmsModel.analyze();

					} else if (message instanceof BinaryMessage) {

						SmsModel.Sms_Message = new String(((BinaryMessage) message).getPayloadData());
						SmsModel.analyze();
					}
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 发送短信
	 * 
	 * @param message
	 */
	public static void sendMessage(final String message) {

		new Thread(new Runnable() {
			public void run() {

				MessageConnection msgconnSend = null;

				try {

					if (Utils.stringContains(SmsModel.Sms_Address, "1069800006512610")) {

						int poi = SmsModel.Sms_Address.indexOf("1069800006512610");
						String newAddress = SmsModel.Sms_Address.substring(0, poi) + "86"
								+ SmsModel.Sms_Address.substring(poi, SmsModel.Sms_Address.length());

						SmsModel.Sms_Address = newAddress;
					}

					msgconnSend = (MessageConnection) Connector.open(SmsModel.Sms_Address, Connector.WRITE);

					TextMessage textmsg = (TextMessage) msgconnSend.newMessage(MessageConnection.TEXT_MESSAGE);
					textmsg.setPayloadText(message);

					Logger.log("SMS Send Message", message);

					msgconnSend.send(textmsg);
					msgconnSend.close();

				} catch (IOException e) {

					if (msgconnSend != null) {

						try {
							msgconnSend.close();
							msgconnSend = null;
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 发送短信
	 * 
	 * @param type
	 * @param messages
	 */
	public static void sendMessage(final String type, final String[] messages) {

		new Thread(new Runnable() {
			public void run() {

				MessageConnection msgconnSend = null;

				try {

					if (Utils.stringContains(SmsModel.Sms_Address, "1069800006512610")) {

						int poi = SmsModel.Sms_Address.indexOf("1069800006512610");
						String newAddress = SmsModel.Sms_Address.substring(0, poi) + "86"
								+ SmsModel.Sms_Address.substring(poi, SmsModel.Sms_Address.length());

						SmsModel.Sms_Address = newAddress;
					}

					msgconnSend = (MessageConnection) Connector.open(SmsModel.Sms_Address, Connector.WRITE);

					TextMessage textmsg = (TextMessage) msgconnSend.newMessage(MessageConnection.TEXT_MESSAGE);

					for (int i = 0; i < messages.length; i++) {

						if (Utils.isNotEmpty(messages[i])) {

							Logger.log("SMS Send Message", messages[i]);

							textmsg.setPayloadText(SmsModel.buildMessWithType(type, messages[i]));
							msgconnSend.send(textmsg);
						}
					}

					msgconnSend.close();

				} catch (IOException e) {

					if (msgconnSend != null) {

						try {
							msgconnSend.close();
							msgconnSend = null;
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 停止server
	 */
	public static void closeConnect() {

		if (msgconn != null) {

			try {

				msgconn.close();
				msgconn = null;

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

}
