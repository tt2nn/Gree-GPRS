package com.gree.gprs.uart.delegate;

import java.io.IOException;
import java.io.InputStream;

import com.gree.gprs.tcp.TcpModel;
import com.gree.gprs.tcp.TcpServer.TcpServerInterface;
import com.gree.gprs.tcp.model.TransmitModel.TcpTransmitInterface;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.uart.model.MbReadBitModel;
import com.gree.gprs.uart.model.MbReadWordModel;
import com.gree.gprs.uart.model.SeveneModel;
import com.gree.gprs.util.Logger;
import com.gree.gprs.variable.Variable;

public class UartTcpDelegate implements TcpTransmitInterface, TcpServerInterface {

	public void receiveServerData(byte[] data, int length) {

		switch (UartModel.Uart_Type) {

		case UartModel.UART_TYPE_7E:

			if (length <= 65) {

				SeveneModel.receiveServerData(data, length);
			}

			break;

		case UartModel.UART_TYPE_MODBUS:

			if (length <= 6) {

				MbReadBitModel.receiveServerData(data, length);

			} else {

				MbReadWordModel.receiveServerData(data, length);
			}

			break;
		}
	}

	public void receiveData(InputStream inputStream) throws IOException {

		if (inputStream != null) {

			int total = 0;
			while ((total = inputStream.read(Variable.Tcp_In_Buffer)) != -1) {

				Logger.log("Tcp Get Message", Variable.Tcp_In_Buffer, 0, total);

				TcpModel.analyze();
			}
		}
	}

}
