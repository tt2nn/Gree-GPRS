package com.gree.gprs.configure;

import java.io.IOException;

import org.joshvm.j2me.cellular.AccessPoint;
import org.joshvm.j2me.cellular.CellInfo;
import org.joshvm.j2me.cellular.CellularDeviceInfo;
import org.joshvm.j2me.cellular.NetworkInfo;

import com.gree.gprs.entity.Apn;
import com.gree.gprs.entity.Device;
import com.gree.gprs.util.Utils;

/**
 * 
 * 
 * @author lihaotian
 *
 */
public class DeviceConfigure {

	private static CellularDeviceInfo[] devices;

	/**
	 * Init Device
	 * 
	 * @return
	 */
	public static void deviceInit() {

		devices = CellularDeviceInfo.listCellularDevices();
	}

	/**
	 * Check Device
	 * 
	 * @return
	 */
	public static boolean hasDevice() {

		if (devices != null && devices.length > 0) {

			return true;
		}

		return false;
	}

	/**
	 * 获取设备信息
	 */
	public static void deviceInfo() {

		try {

			if (hasDevice()) {

				Device.getInstance().setImei(devices[0].getIMEI());
				Device.getInstance().setImsi(devices[0].getIMSI());
				Device.getInstance().setIccid(devices[0].getICCID());

				NetworkInfo networkInfo = devices[0].getNetworkInfo();
				if (networkInfo != null) {

					Device.getInstance().setMnc(networkInfo.getMNC());
					Device.getInstance().setMcc(networkInfo.getMCC());
				}

				CellInfo cellInfo = devices[0].getCellInfo();
				if (cellInfo != null) {

					Device.getInstance().setLac(cellInfo.getLAC());
					Device.getInstance().setCid(cellInfo.getCellID());
				}
			}

		} catch (IOException e) {

			Device.getInstance().reset();
			e.printStackTrace();
		}
	}

	/**
	 * 获取网络信号级别
	 * 
	 * @return
	 */
	public static int getNetworkSignalLevel() {

		try {

			if (hasDevice()) {

				return devices[0].getNetworkSignalLevel();
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 设置APN
	 * 
	 * @param apn
	 */
	public static void setApn(Apn apn) {

		try {

			if (hasDevice()) {

				AccessPoint accessPoint = new AccessPoint(apn.getApnName(), apn.getUserName(), apn.getPassword());
				devices[0].setAPN(accessPoint);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 获取APN信息
	 * 
	 * @return
	 */
	public static Apn getApn() {

		Apn apn = new Apn();

		try {

			if (hasDevice()) {

				AccessPoint accessPoint = devices[0].getCurrentAPNSetting();
				apn.setApnName(accessPoint.getName());
				apn.setUserName(accessPoint.getUserName());
				apn.setPassword(accessPoint.getPassword());
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return apn;
	}

	/**
	 * Check Network
	 * 
	 * @return
	 */
	public static boolean hasNetwork() {

		if (hasDevice()) {

			if (Device.getInstance().getMcc() > 0) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Check Sim
	 * 
	 * @return
	 */
	public static boolean hasSim() {

		if (hasDevice()) {

			if (Utils.isNotEmpty(Device.getInstance().getImsi()) && Device.getInstance().getImsi().length() > 1) {

				return true;
			}
		}

		return false;
	}

}
