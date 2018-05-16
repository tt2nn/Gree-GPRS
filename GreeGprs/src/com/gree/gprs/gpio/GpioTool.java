package com.gree.gprs.gpio;

import java.io.IOException;

import org.joshvm.j2me.dio.ClosedDeviceException;
import org.joshvm.j2me.dio.UnavailableDeviceException;
import org.joshvm.j2me.dio.gpio.GPIOPin;

/**
 * Gpio工具类
 * 
 * @author lihaotian
 *
 */
public class GpioTool {

	/**
	 * 设置信号灯
	 * 
	 * @param level
	 */
	public static void setSignLevel(int level) {

		GpioPin.closeAllSig();

		if (level >= 12 && level <= 14) {

			GpioPin.openLow();

		} else if (level >= 15 && level <= 17) {

			GpioPin.openLow();
			GpioPin.openMiddle();

		} else if (level >= 18) {

			GpioPin.openLow();
			GpioPin.openMiddle();
			GpioPin.openHight();
		}
	}

	/**
	 * 获取通讯灯的状态
	 * 
	 * @return
	 */
	public static boolean getTransmitValue() {

		return getLightValue(GpioPin.Light_Transmit);
	}

	/**
	 * 获取异常灯状态
	 * 
	 * @return
	 */
	public static boolean getErrorValue() {

		return getLightValue(GpioPin.Light_Error);
	}

	/**
	 * 通讯灯的状态
	 */
	private static boolean getLightValue(GPIOPin gpioPin) {

		if (gpioPin != null) {

			try {

				return gpioPin.getValue();

			} catch (UnavailableDeviceException e) {

				e.printStackTrace();

			} catch (ClosedDeviceException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		return false;
	}

}
