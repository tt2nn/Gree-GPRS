package com.gree.gprs.entity;

public class Struct7E {

	private byte[] data;

	public void insertData(byte[] dataArray, int start, int length) {

		if (data == null) {

			data = new byte[256];
		}
		
		for (int i = 0; i < length; i++) {

			data[i] = dataArray[i + start];
		}
	}

	public boolean compare(byte[] dataArray, int start) {

		if (data != null) {

			for (int i = 0; i < 9; i++) {

				if (data[i] != dataArray[i + start]) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
