package com.gree.gprs.timer;

import java.io.IOException;

import org.joshvm.j2me.dio.ClosedDeviceException;
import org.joshvm.j2me.dio.DeviceManager;
import org.joshvm.j2me.dio.DeviceNotFoundException;
import org.joshvm.j2me.dio.InvalidDeviceConfigException;
import org.joshvm.j2me.dio.UnavailableDeviceException;
import org.joshvm.j2me.dio.UnsupportedAccessModeException;
import org.joshvm.j2me.dio.UnsupportedDeviceTypeException;
import org.joshvm.j2me.dio.gpio.GPIOPin;
import org.joshvm.j2me.dio.gpio.GPIOPinConfig;

/**
 * 喂狗Timer
 * 
 * @author lihaotian
 *
 */
public class FeedDogTimer implements Runnable {

	private GPIOPin feedDog;

	public void startTimer() {

		GPIOPinConfig cfg1 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 21, GPIOPinConfig.DIR_OUTPUT_ONLY,
				GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
		try {

			feedDog = (GPIOPin) DeviceManager.open(cfg1, DeviceManager.EXCLUSIVE);

		} catch (InvalidDeviceConfigException e) {
			e.printStackTrace();
		} catch (UnsupportedDeviceTypeException e) {
			e.printStackTrace();
		} catch (DeviceNotFoundException e) {
			e.printStackTrace();
		} catch (UnavailableDeviceException e) {
			e.printStackTrace();
		} catch (UnsupportedAccessModeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		new Thread(this).start();
	}

	public void run() {

		while (true) {

			try {

				feedDog.setValue(true);
				Thread.sleep(5 * 1000);
				feedDog.setValue(false);
				Thread.sleep(5 * 1000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (UnavailableDeviceException e) {
				e.printStackTrace();
			} catch (ClosedDeviceException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
