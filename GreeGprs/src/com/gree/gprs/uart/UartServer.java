package com.gree.gprs.uart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.gprs.Boot;
import com.gree.gprs.can.CanModel;
import com.gree.gprs.can.delegate.CanTcpDelegate;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.uart.delegate.UartAndCanDataDelegate;
import com.gree.gprs.uart.delegate.UartDataDelegate;
import com.gree.gprs.uart.delegate.UartTcpDelegate;
import com.gree.gprs.util.Logger;
import com.gree.gprs.variable.Variable;

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

	private boolean firstCheck = false;
	private boolean uartTransmit = false;

	private static CanModel canModel;
	private static int sendNum = 0;

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

				// 确认Uart Or Can
				if (!firstCheck) {

					for (int i = 0; i < readLength - 1; i++) {

						if (readBuffer[i] == (byte) 0xF5 && readBuffer[i + 1] == (byte) 0xF6) {

							firstCheck = true;
							uartTransmit = false;

							if (canModel == null) {

								canModel = new CanModel();
								new Thread(canModel).start();
							}

							Variable.Choose_Max_Number = 4;
							Variable.Baud_Rate = 20000;

							DataCenter.setDataInterface(new UartAndCanDataDelegate());
							DataCenter.init();
							TransmitModel.setTcpTransmitInterface(new CanTcpDelegate());
							TcpServer.setTcpServerInterface(new UartTcpDelegate());

							break;

						} else if (readBuffer[i] == (byte) 0xFA && readBuffer[i + 1] == (byte) 0xFB) {

							firstCheck = true;
							uartTransmit = true;
							DataCenter.setDataInterface(new UartDataDelegate());
							DataCenter.init();
							UartTcpDelegate uartTcpDelegate = new UartTcpDelegate();
							TransmitModel.setTcpTransmitInterface(uartTcpDelegate);
							TcpServer.setTcpServerInterface(uartTcpDelegate);

							break;
						}
					}
				}

				// Can
				if (!uartTransmit) {

					if (sendNum < 2 && !Variable.Gprs_Choosed) {

						UartModel.Uart_Out_Buffer[0] = 0x00;
						sendNum++;
						sendData(1);
					}

					for (int i = 0; i < readLength; i++) {

						if (start == 0 && readBuffer[i] == (byte) 0xF5) {

							start = 1;
							continue;
						}

						if (start == 1) {

							if (readBuffer[i] != (byte) 0xF6) {

								start = 0;

							} else {

								start = 2;
							}

							continue;
						}

						if (start == 2) {

							if (end == 0) {

								if (readBuffer[i] == (byte) 0xF7) {

									end = 1;
								}

								CanModel.Can_Data_In_Buffer[inBufferPoi] = readBuffer[i];
								inBufferPoi++;

								continue;
							}

							if (end == 1) {

								if (readBuffer[i] != (byte) 0xF8) {

									end = 0;

									CanModel.Can_Data_In_Buffer[inBufferPoi] = readBuffer[i];
									inBufferPoi++;

								} else {

									CanModel.Can_Data_Length = inBufferPoi - 1;
									canModel.analyze();
									resetVariable();
								}
							}
						}
					}

				} else {

					// Uart
					sendNum = 2;
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

		if (sendNum < 2 && !Variable.Gprs_Choosed && length > 1) {

			return;
		}

		try {

			if (outputStream != null) {

				outputStream.write(UartModel.Uart_Out_Buffer, 0, length);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 喂狗
	 */
	public static void feedDog() {

		if (outputStream != null) {

			try {

				outputStream.write(UartModel.Uart_Out_Buffer, 0, 16);

			} catch (IOException e) {
				e.printStackTrace();
			}
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
