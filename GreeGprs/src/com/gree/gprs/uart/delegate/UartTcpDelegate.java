package com.gree.gprs.uart.delegate;

import com.gree.gprs.tcp.model.TransmitModel.TcpTransmitInterface;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.variable.UartVariable;

public class UartTcpDelegate implements TcpTransmitInterface {

	public void receiveServerData(byte[] data, int length) {

		switch (UartVariable.Uart_Type) {

		case UartModel.UART_TYPE_7E:

			if (length <= 65) {

				for (int i = 0; i < length; i++) {

					UartVariable.Server_7E_Data[i] = data[i];
				}

				UartVariable.Receive_Server_Data = true;
			}

			break;

		case UartModel.UART_TYPE_MODBUS:

			if (length <= 6) {

				for (int i = 0; i < length; i++) {

					UartVariable.Server_Modbus_Bit_Data[i] = data[i];
				}

			} else {

				for (int i = 0; i < length; i++) {

					UartVariable.Server_Modbus_Word_Data[26 + i] = data[i];
				}
			}

			break;
		}
	}

}
