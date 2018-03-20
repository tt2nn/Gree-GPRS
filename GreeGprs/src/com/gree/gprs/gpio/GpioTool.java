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

		GpioPin.signalAllDark();

		if (level >= 12 && level <= 14) {

			GpioPin.signalLowLight();

		} else if (level >= 15 && level <= 17) {

			GpioPin.signalLowLight();
			GpioPin.signalMindleLight();

		} else if (level >= 18) {

			GpioPin.signalLowLight();
			GpioPin.signalMindleLight();
			GpioPin.signalHighLight();
		}
	}

	/**
	 * 获取通讯灯的状态
	 * 
	 * @return
	 */
	public static boolean getCommunicationValue() {

		return getLightValue(GpioPin.pinoutCommunication);
	}

	/**
	 * 获取异常灯状态
	 * 
	 * @return
	 */
	public static boolean getErrorValue() {

		return getLightValue(GpioPin.pinoutError);
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
