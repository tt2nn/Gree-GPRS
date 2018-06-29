package com.gree.gprs.timer;

import com.gree.gprs.Boot;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.gpio.GpioTool;
import com.gree.gprs.variable.Variable;

public class TransmitTimer implements Runnable {

	public static void startTimer() {

		new Thread(new TransmitTimer()).start();
	}

	public void run() {

		while (Boot.Gprs_Running) {

			// 检查通讯灯
			if (Variable.Gprs_Choosed) {

				// 上传数据时灯闪烁
				if (Variable.Transmit_Type != Constant.TRANSMIT_TYPE_STOP && !GpioTool.getErrorValue()) {

					if (GpioTool.getTransmitValue()) {

						GpioPin.closeTransmit();

					} else {

						GpioPin.openTransmit();
					}

				} else {

					if (!GpioTool.getTransmitValue()) {

						GpioPin.openTransmit();
					}
				}

			} else if (GpioTool.getTransmitValue()) {

				GpioPin.closeTransmit();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
