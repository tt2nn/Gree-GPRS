package com.gree.gprs.entity;

public class Device {

	private static Device device;

	private Device() {
	};

	public static synchronized Device getInstance() {

		if (device == null) {

			device = new Device();
		}

		return device;
	}

	/**
	 * 硬件序列号
	 */
	private String imei;
	/**
	 * 卡的序列号
	 */
	private String imsi;
	/**
	 * 制卡商序列号
	 */
	private String iccid;
	/**
	 * 1.联通<br>
	 * 0.移动<br>
	 */
	private int mnc;
	/**
	 * mcc 460 中国
	 */
	private int mcc;
	/**
	 * 基站信息
	 */
	private int lac;

	public void reset() {

		imsi = "";
		iccid = "";
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public int getMnc() {
		return mnc;
	}

	public void setMnc(int mnc) {
		this.mnc = mnc;
	}

	public int getMcc() {
		return mcc;
	}

	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

}
