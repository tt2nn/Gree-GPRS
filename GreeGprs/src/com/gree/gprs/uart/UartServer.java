package com.gree.gprs.uart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.gprs.Boot;
import com.gree.gprs.util.Logger;

/**
 * 串口服务
 * 
 * @author lihaotian
 *
 */
public class UartServer implements Runnable {

	private static StreamConnection streamConnect;
	private static InputStream inputStream;
	private static OutputStream outputStream;

	private static byte[] readBuffer = new byte[256];
	private static int inBufferPoi = 0;
	private static int start = 0;
	private static int end = 0;

	private static Thread uartThread;

	/**
	 * 启动串口通信
	 */
	public static void startServer() {

		// Utils.resetModbusData(Variable.Server_Data_Long_Buffer);
		// Utils.resetData(Variable.Server_Data_Short_Buffer);

		UartServer uartServer = new UartServer();
		uartThread = new Thread(uartServer);
		uartThread.start();

	}

	public void run() {

		while (Boot.Gprs_Running) {

			try {

				String host = "comm:COM1;baudrate=9600";

				streamConnect = (StreamConnection) Connector.open(host);

				inputStream = streamConnect.openInputStream();
				outputStream = streamConnect.openOutputStream();

				Logger.log("Uart Server", "---- Start Uart Server ----");

				receiveData();

			} catch (Exception e) {

				e.printStackTrace();

			} finally {

				stopServer();
				clearStream();
			}
		}
	}

	/**
	 * 接收数据
	 */
	private void receiveData() throws Exception {

		if (inputStream != null) {

			resetVariable();

			int readLength = 0;
			while ((readLength = inputStream.read(readBuffer)) != -1) {

				if (inBufferPoi == UartModel.Uart_In_Buffer.length) {

					resetVariable();
				}

				for (int i = 0; i < readLength; i++) {

					if (start == 0 && readBuffer[i] == (byte) 0xFA) {

						start = 1;
						continue;
					}

					if (start == 1) {

						if (readBuffer[i] != (byte) 0xFB) {

							start = 0;

						} else {

							start = 2;
						}

						continue;
					}

					if (start == 2) {

						if (end == 0) {

							if (readBuffer[i] == (byte) 0xFC) {

								end = 1;
							}

							UartModel.Uart_In_Buffer[inBufferPoi] = readBuffer[i];
							inBufferPoi++;

							continue;
						}

						if (end == 1) {

							if (readBuffer[i] != (byte) 0xFD) {

								end = 0;

								UartModel.Uart_In_Buffer[inBufferPoi] = readBuffer[i];
								inBufferPoi++;

							} else {

								UartModel.Uart_In_Buffer_Length = inBufferPoi - 1;
								UartModel.analyze();
								resetVariable();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * reset inBufferPoi、start、end
	 */
	private static void resetVariable() {

		inBufferPoi = 0;
		start = 0;
		end = 0;
	}

	/**
	 * 发送数据
	 */
	public static void sendData(int length) {

		try {

			if (outputStream != null) {

				outputStream.write(UartModel.Uart_Out_Buffer, 0, length);
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

	public static Thread getUartThread() {
		return uartThread;
	}

}