package com.gree.gprs.can.delegate;

import com.gree.gprs.can.CanModel;
import com.gree.gprs.tcp.model.TransmitModel.TcpTransmitInterface;

public class CanTcpDelegate implements TcpTransmitInterface {

	public void receiveServerData(byte[] data, int length) {

		for (int i = 0; i < length; i++) {

			CanModel.Server_Can_Data[i] = data[i];
		}

		CanModel.Can_Data_Length = length;
	}

}
