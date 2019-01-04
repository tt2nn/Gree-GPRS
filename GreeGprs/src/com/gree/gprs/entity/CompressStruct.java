package com.gree.gprs.entity;

public class CompressStruct {

	private byte[] data;
	private int compressIndex = 0;

	public CompressStruct(int compressIndex) {

		this.compressIndex = compressIndex;
		data = new byte[256];
	}

	public void insertData(byte[] dataArray, int start, int length) {

		for (int i = 0; i < length; i++) {

			data[i] = dataArray[i + start];
		}
	}

	public boolean compare(byte[] dataArray, int start) {

		if (data != null) {

			for (int i = 0; i < compressIndex; i++) {

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
