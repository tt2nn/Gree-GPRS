package com.gree.gprs.uart.delegate;

import com.gree.gprs.tcp.model.TransmitModel.TcpTransmitInterface;
import com.gree.gprs.uart.UartModel;

public class UartTcpDelegate implements TcpTransmitInterface {

	public void receiveServerData(byte[] data, int length) {

		switch (UartModel.Uart_Type) {

		case UartModel.UART_TYPE_7E:

			if (length <= 65) {

				for (int i = 0; i < length; i++) {

					UartModel.Server_7E_Data[i] = data[i];
				}

				UartModel.Receive_Server_Data = true;
			}

			break;

		case UartModel.UART_TYPE_MODBUS:

			if (length <= 6) {

				for (int i = 0; i < length; i++) {

					UartModel.Server_Modbus_Bit_Data[i] = data[i];
				}

				UartModel.Discrete_Inputs_Buffer.update(0, UartModel.Server_Modbus_Bit_Data, 0, length);

			} else {

				for (int i = 0; i < length; i++) {

					UartModel.Server_Modbus_Word_Data[26 + i] = data[i];
				}

				UartModel.Server_Modbus_Word_Data[25] = (byte) 0xFF;
				UartModel.Input_Registers_Stack.update(25, UartModel.Server_Modbus_Word_Data, 25, length + 1);
			}

			break;
		}
	}

}
