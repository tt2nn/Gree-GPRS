package com.gree.gprs.can.delegate;

import com.gree.gprs.can.CanDataManager;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.data.DataCenter.DataInterface;
import com.gree.gprs.variable.Variable;

public class CanDataDelegate implements DataInterface {

	public void init() {

		CanDataManager.init();
		DataCenter.Data_Send_State[0] = (byte) 0x01;
	}

	public void saveData(byte[] data) {

		CanDataManager.writeData();
	}

	public boolean queryData(int address) {

		return CanDataManager.readData(address);
	}

	public boolean queryDataHasSend() {

		return Variable.Data_Query_Buffer[1792] == (byte) 0x01;
	}

	public void markDataIsSend(int address) {

		CanDataManager.writeData(address + 1792, DataCenter.Data_Send_State);
	}

	public int saveDataBuffer(int length, byte[] data) {
		
		return 0;
	}

}
