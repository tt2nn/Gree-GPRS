package com.gree.gprs.can.delegate;

import com.gree.gprs.can.CanDataManager;
import com.gree.gprs.data.DataCenter.DataInterface;
import com.gree.gprs.util.Utils;
import com.gree.gprs.variable.Variable;

public class CanDataDelegate implements DataInterface {

	private byte[] dataSendState = new byte[2048];
	private byte[] canIds = new byte[4];

	public void init() {

		CanDataManager.init();
		dataSendState[1792] = (byte) 0x01;
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

		CanDataManager.writeData(address + 1792, dataSendState);
	}

	public int saveDataBuffer(int poi, byte[] data, int length) {

		int bufferPoi = poi;

		Variable.Data_Cache_Buffer[bufferPoi] = (byte) 0xAA;
		bufferPoi++;

		Variable.Data_Cache_Buffer[bufferPoi] = (byte) 0xAA;
		bufferPoi++;

		int checkPoi = bufferPoi;

		Variable.Data_Cache_Buffer[bufferPoi] = (byte) ((byte) 0x80 | data[4]);
		bufferPoi++;

		getCanIds(data);
		for (int i = 0; i < 4; i++) {

			Variable.Data_Cache_Buffer[bufferPoi] = canIds[i];
			bufferPoi++;
		}

		int dataLength = data[4];
		for (int i = 0; i < dataLength; i++) {

			Variable.Data_Cache_Buffer[bufferPoi] = data[i + 8];
			bufferPoi++;
		}

		byte check = Variable.Data_Cache_Buffer[checkPoi];
		for (int i = checkPoi + 1; i < bufferPoi; i++) {

			check = (byte) (check ^ Variable.Data_Cache_Buffer[i]);
		}
		Variable.Data_Cache_Buffer[bufferPoi] = check;
		bufferPoi++;

		Variable.Data_Cache_Buffer[bufferPoi] = (byte) 0xFF;
		bufferPoi++;

		return bufferPoi - poi;
	}

	/**
	 * get can id
	 * 
	 * @param data
	 */
	public void getCanIds(byte[] data) {

		byte b1 = (byte) (data[3] << 3);
		byte b2 = (byte) (data[2] >> 5);
		if (Utils.byteGetBit(data[2])[7] == 1) {

			b2 = (byte) (b2 ^ (byte) 0xF8);
		}
		canIds[0] = (byte) (b1 | b2);

		byte b3 = (byte) (data[2] << 3);
		byte b4 = (byte) (b3 >> 1);
		if (Utils.byteGetBit(b3)[7] == 1) {
			b4 = (byte) (b4 ^ (byte) 0x80);
		}
		byte b5 = (byte) (data[1] >> 6);
		if (Utils.byteGetBit(data[1])[7] == 1) {
			b5 = (byte) (b5 ^ (byte) 0xFC);
		}
		canIds[1] = (byte) (b4 | b5);

		byte b6 = (byte) (data[1] << 2);
		byte b7 = (byte) (b6 >> 1);
		if (Utils.byteGetBit(b6)[7] == 1) {

			b7 = (byte) (b7 ^ (byte) 0x80);
		}
		byte b8 = (byte) Utils.byteGetBit(data[0])[7];
		canIds[2] = (byte) (b7 | b8);

		byte b9 = data[0];
		if (Utils.byteGetBit(b9)[7] == 1) {

			b9 = (byte) (b9 ^ (byte) 0x80);
		}
		canIds[3] = b9;
	}

}
