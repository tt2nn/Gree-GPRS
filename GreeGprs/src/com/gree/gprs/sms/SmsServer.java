package com.gree.gprs.sms;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import com.gree.gprs.Boot;
import com.gree.gprs.util.Logger;

/**
 * 短信服务
 * 
 * @author lihaotian
 *
 */
public class SmsServer implements Runnable {

	private static MessageConnection msgconn;
	private Message message;

	private static Thread smsThread;

	private static Object lock = new Object();

	/**
	 * 启动短信服务
	 */
	public static void startServer() {

		smsThread = new Thread(new SmsServer());
		smsThread.start();
	}

	public void run() {

		while (Boot.Gprs_Running) {

			Logger.log("Sms Server", "Server Start");

			try {

				String address = "sms://:0";
				msgconn = (MessageConnection) Connector.open(address);

				/* gets message object */
				message = msgconn.receive();

				if (message != null) {

					/* gets address of the message received */
					SmsModel.Sms_Address = message.getAddress();

					/* check for the type of message received */
					if (message instanceof TextMessage) {

						// System.out.println("Recieved Text Messsage: ===== " + ((TextMessage)
						// message).getPayloadText());
						SmsModel.Sms_Message = ((TextMessage) message).getPayloadText();
						SmsModel.analyze();

					} else if (message instanceof BinaryMessage) {

						/* get the binary data of the message */
						// byte[] data = ((BinaryMessage) message).getPayloadData();
						// System.out.println("Recieved Binary Messsage: \n" + new String(data));
						SmsModel.Sms_Message = new String(((BinaryMessage) message).getPayloadData());
						SmsModel.analyze();
					}
				}

				closeConnect();

			} catch (Exception e) {

				e.printStackTrace();
			}
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

				try {

					synchronized (lock) {

						/* to open message connection */
						MessageConnection msgconnSend = (MessageConnection) Connector.open(SmsModel.Sms_Address);

						// sending text message
						TextMessage textmsg = (TextMessage) msgconnSend.newMessage(MessageConnection.TEXT_MESSAGE);

						/* pay load text passed here */
						textmsg.setPayloadText(message);
						/* send the text message */
						msgconnSend.send(textmsg);

						msgconnSend.close();
						closeConnect();
					}

				} catch (IOException e) {

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

	public static Thread getSmsThread() {
		return smsThread;
	}

}
