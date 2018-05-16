package com.gree.gprs.gpio;

import java.io.IOException;

import org.joshvm.j2me.dio.DeviceManager;
import org.joshvm.j2me.dio.gpio.GPIOPin;
import org.joshvm.j2me.dio.gpio.GPIOPinConfig;
import org.joshvm.j2me.dio.gpio.PinEvent;
import org.joshvm.j2me.dio.gpio.PinListener;

import com.gree.gprs.control.ControlCenter;

/**
 * 控制灯 <br>
 * 响应按键 <br>
 * 
 * @author lihaotian
 *
 */
public class GpioPin {

	public static GPIOPin Light_Error;
	public static GPIOPin Light_Transmit;
	public static GPIOPin Light_Hight;
	public static GPIOPin Light_Middle;
	public static GPIOPin Light_Low;

	public static GPIOPin Key_Transmit;

	/**
	 * 传输按键
	 */
	public static void gpioInit() {

		try {

			/* 传输按键 */
			GPIOPinConfig cfg0 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 8, // SPI0_1_DI
					GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_UP, GPIOPinConfig.TRIGGER_LOW_LEVEL,
					true);

			Key_Transmit = (GPIOPin) DeviceManager.open(cfg0, DeviceManager.EXCLUSIVE);

			Key_Transmit.setInputListener(new PinListener() {
				public void valueChanged(PinEvent event) {

					if (event.getValue()) {

						ControlCenter.pushKey(false);

					} else {

						ControlCenter.pushKey(true);
					}
				}
			});

			/* 异常灯 */
			GPIOPinConfig cfg1 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 6, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			Light_Error = (GPIOPin) DeviceManager.open(cfg1, DeviceManager.EXCLUSIVE);

			/* 通讯灯 */
			GPIOPinConfig cfg2 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 9, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			Light_Transmit = (GPIOPin) DeviceManager.open(cfg2, DeviceManager.EXCLUSIVE);

			/* 信号强 */
			GPIOPinConfig cfg3 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 7, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			Light_Hight = (GPIOPin) DeviceManager.open(cfg3, DeviceManager.EXCLUSIVE);

			/* 信号中 */
			GPIOPinConfig cfg4 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 23, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			Light_Middle = (GPIOPin) DeviceManager.open(cfg4, DeviceManager.EXCLUSIVE);

			/* 信号弱 */
			GPIOPinConfig cfg5 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 22, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			Light_Low = (GPIOPin) DeviceManager.open(cfg5, DeviceManager.EXCLUSIVE);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 异常灯亮
	 */
	public static void openError() {

		openLight(Light_Error);
	}

	/**
	 * 异常灯暗
	 */
	public static void closeError() {

		closeLight(Light_Error);
	}

	/**
	 * 通讯灯亮
	 */
	public static void openTransmit() {

		openLight(Light_Transmit);
	}

	/**
	 * 通讯灯暗
	 */
	public static void closeTransmit() {

		closeLight(Light_Transmit);
	}

	/**
	 * 信号强灯亮
	 * 
	 */
	public static void openHight() {

		openLight(Light_Hight);
	}

	/**
	 * 信号强灯暗
	 * 
	 */
	public static void closeHight() {

		closeLight(Light_Hight);
	}

	/**
	 * 信号中灯亮
	 * 
	 */
	public static void openMiddle() {

		openLight(Light_Middle);
	}

	/**
	 * 信号中灯暗
	 * 
	 */
	public static void closeMiddle() {

		closeLight(Light_Middle);
	}

	/**
	 * 信号弱灯亮
	 * 
	 */
	public static void openLow() {

		openLight(Light_Low);
	}

	/**
	 * 信号弱灯暗
	 * 
	 */
	public static void closeLow() {

		closeLight(Light_Low);
	}

	/**
	 * 关闭所有的信号灯
	 */
	public static void closeAllSig() {

		closeHight();
		closeMiddle();
		closeLow();
	}

	/**
	 * 关闭所有的灯
	 */
	public static void closeAllLight() {

		closeError();
		closeTransmit();
		closeAllSig();
	}

	/**
	 * 打开所有的灯
	 */
	public static void openAllLight() {

		openError();
		openTransmit();
		openHight();
		openMiddle();
		openLow();
	}

	/**
	 * 开灯
	 * 
	 * @param light
	 */
	private static void openLight(GPIOPin light) {

		try {

			if (light != null) {

				light.setValue(true);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 关灯
	 * 
	 * @param light
	 */
	private static void closeLight(GPIOPin light) {

		try {

			if (light != null) {

				light.setValue(false);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
