package com.gree.gprs.uart.delegate;

import com.gree.gprs.data.DataCenter.DataInterface;
import com.gree.gprs.spi.Spi;
import com.gree.gprs.variable.Variable;

public class UartDataDelegate implements DataInterface {

	private byte[] dataSendState = new byte[256];

	public void init() {

		Spi.init(4096);
		dataSendState[0] = (byte) 0x01;
	}

	public void saveData(byte[] data) {

		Spi.writeData();
	}

	public boolean queryData(int address) {

		return Spi.readData(address);
	}

	public boolean queryDataHasSend() {

		return Variable.Data_Query_Buffer[3840] == (byte) 0x01;
	}

	public void markDataIsSend(int address) {

		Spi.writeData(address + 3840, dataSendState);
	}

	public int saveDataBuffer(int poi, byte[] data, int length) {

		for (int i = 0; i < length; i++) {

			Variable.Data_Cache_Buffer[i + poi] = data[i];
		}

		return length;
	}

}
