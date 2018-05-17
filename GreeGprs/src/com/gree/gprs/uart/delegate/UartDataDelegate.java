package com.gree.gprs.uart.delegate;

import com.gree.gprs.data.DataCenter;
import com.gree.gprs.data.DataCenter.DataInterface;
import com.gree.gprs.spi.Spi;
import com.gree.gprs.uart.UartModel;
import com.gree.gprs.variable.Variable;

public class UartDataDelegate implements DataInterface {

	public void init() {

		UartModel.init();
		Spi.init(2048);
		DataCenter.Data_Send_State[0] = (byte) 0x01;
	}

	public void saveData(byte[] data) {

		Spi.writeData();
	}

	public boolean queryData(int address) {

		return Spi.readData(address);
	}

	public boolean queryDataHasSend() {

		return Variable.Data_Query_Buffer[1792] == (byte) 0x01;
	}

	public void markDataIsSend(int address) {

		Spi.writeData(address + 1792, DataCenter.Data_Send_State);
	}

	public int saveDataBuffer(int poi, byte[] data, int length) {

		for (int i = 0; i < length; i++) {

			Variable.Data_Cache_Buffer[i + poi] = data[i];
		}

		return length;
	}

}
