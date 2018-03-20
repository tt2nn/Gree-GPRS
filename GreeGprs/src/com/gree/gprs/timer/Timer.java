package com.gree.gprs.timer;

import com.gree.gprs.Boot;
import com.gree.gprs.variable.Variable;

/**
 * 定时器
 * 
 * @author lihaotian
 *
 */
public class Timer implements Runnable {

	private long synchronizedTime = 0L;

	private static Thread timerThread;

	/**
	 * 启动Timer
	 */
	public static void startTimer() {

		timerThread = new Thread(new Timer());
		timerThread.start();
	}

	public void run() {

		synchronizedTime = Variable.System_Time;

		while (Boot.Gprs_Running) {

			try {

				Thread.sleep(500);

				if (Variable.System_Delta_Time != 0 && Variable.System_Time - synchronizedTime >= 30 * 1000) {

					Variable.System_Time = System.currentTimeMillis() + Variable.System_Delta_Time;
					synchronizedTime = Variable.System_Time;

				} else {

					Variable.System_Time += 500;
				}

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	public static Thread getTimerThread() {
		return timerThread;
	}

}
