package com.gree.gprs.can.delegate;

import java.io.IOException;
import java.io.InputStream;

import com.gree.gprs.can.CanModel;
import com.gree.gprs.tcp.TcpModel;
import com.gree.gprs.tcp.TcpServer.TcpServerInterface;
import com.gree.gprs.tcp.model.TransmitModel.TcpTransmitInterface;
import com.gree.gprs.util.Logger;
import com.gree.gprs.variable.Variable;

public class CanTcpDelegate implements TcpTransmitInterface, TcpServerInterface {

	public void receiveServerData(byte[] data, int length) {

		if (length <= 850) {

			for (int i = 0; i < length; i++) {

				CanModel.Server_Can_Data[i] = data[i];
			}

			CanModel.Receive_Server_Data_Length = length;
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
