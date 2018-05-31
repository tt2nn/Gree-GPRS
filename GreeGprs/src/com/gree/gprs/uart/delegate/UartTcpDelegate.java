package com.gree.gprs.uart.delegate;

import com.gree.gprs.tcp.model.TransmitModel.TcpTransmitInterface;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.uart.model.MbReadBitModel;
import com.gree.gprs.uart.model.MbReadWordModel;
import com.gree.gprs.uart.model.SeveneModel;

public class UartTcpDelegate implements TcpTransmitInterface {

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

}
