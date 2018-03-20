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

	public static GPIOPin pinoutError;
	public static GPIOPin pinoutCommunication;
	public static GPIOPin pinoutHight;
	public static GPIOPin pinoutMiddle;
	public static GPIOPin pinoutLow;

	public static GPIOPin keyTransmit;

	/**
	 * 传输按键
	 */
	public static void gpioInit() {

		try {

			/* 传输按键 */
			GPIOPinConfig cfg0 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 8, // SPI0_1_DI
					GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_UP, GPIOPinConfig.TRIGGER_LOW_LEVEL,
					true);

			keyTransmit = (GPIOPin) DeviceManager.open(cfg0, DeviceManager.EXCLUSIVE);

			keyTransmit.setInputListener(new PinListener() {
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
			pinoutError = (GPIOPin) DeviceManager.open(cfg1, DeviceManager.EXCLUSIVE);

			/* 通讯灯 */
			GPIOPinConfig cfg2 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 9, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			pinoutCommunication = (GPIOPin) DeviceManager.open(cfg2, DeviceManager.EXCLUSIVE);

			/* 信号强 */
			GPIOPinConfig cfg3 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 7, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			pinoutHight = (GPIOPin) DeviceManager.open(cfg3, DeviceManager.EXCLUSIVE);

			/* 信号中 */
			GPIOPinConfig cfg4 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 23, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			pinoutMiddle = (GPIOPin) DeviceManager.open(cfg4, DeviceManager.EXCLUSIVE);

			/* 信号弱 */
			GPIOPinConfig cfg5 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 22, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			pinoutLow = (GPIOPin) DeviceManager.open(cfg5, DeviceManager.EXCLUSIVE);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 异常灯亮
	 */
	public static void errorLight() {

		lightOpen(pinoutError);
	}

	/**
	 * 异常灯暗
	 */
	public static void errorDark() {

		lightClose(pinoutError);
	}

	/**
	 * 通讯灯亮
	 */
	public static void communicationLight() {

		lightOpen(pinoutCommunication);
	}

	/**
	 * 通讯灯暗
	 */
	public static void communicationDark() {

		lightClose(pinoutCommunication);
	}

	/**
	 * 信号强灯亮
	 * 
	 */
	public static void signalHighLight() {

		lightOpen(pinoutHight);
	}

	/**
	 * 信号强灯暗
	 * 
	 */
	public static void signalHighDark() {

		lightClose(pinoutHight);
	}

	/**
	 * 信号中灯亮
	 * 
	 */
	public static void signalMindleLight() {

		lightOpen(pinoutMiddle);
	}

	/**
	 * 信号中灯暗
	 * 
	 */
	public static void signalMindleDark() {

		lightClose(pinoutMiddle);
	}

	/**
	 * 信号弱灯亮
	 * 
	 */
	public static void signalLowLight() {

		lightOpen(pinoutLow);
	}

	/**
	 * 信号弱灯暗
	 * 
	 */
	public static void signalLowDark() {

		lightClose(pinoutLow);
	}

	/**
	 * 关闭所有的信号灯
	 */
	public static void signalAllDark() {

		lightClose(pinoutHight);
		lightClose(pinoutMiddle);
		lightClose(pinoutLow);
	}

	/**
	 * 关闭所有的灯
	 */
	public static void closeAllLight() {

		lightClose(pinoutError);
		lightClose(pinoutCommunication);
		signalAllDark();
	}

	/**
	 * 开灯
	 * 
	 * @param light
	 */
	private static void lightOpen(GPIOPin light) {

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
	private static void lightClose(GPIOPin light) {

		try {

			if (light != null) {

				light.setValue(false);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
