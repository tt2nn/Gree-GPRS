package com.gree.gprs.util;

import java.util.Random;

/**
 * 选举逻辑 <br>
 * 总线上最多有16个GPRS模块，只能有一个模块回复总线选举帧
 * 
 * @author lihaotian
 *
 */
public class DoChoose {

	// 选举计数
	private static int chooseNum = -1;
	private static boolean chooseResp = false;
	private static int chooseRandom = 2;

	/**
	 * 选举
	 * 
	 * @return
	 */
	public static boolean choose() {

		chooseResp = false;

		if (chooseNum == -1) { // 当选举计数为-1的时候，收到选举则重新取随机数

			chooseNum = new Random().nextInt(chooseRandom);

			if (chooseRandom > 10) {

				chooseRandom = 10;

			} else {

				chooseRandom += 2;
			}
		}

		if (chooseNum == 0) { // 当计数为0的时候，可以执行选举

			chooseNum = -1;
			choosed();
			return true;
		}

		if (chooseNum != 0) { // 当计数不为0，减一

			chooseNum--;
		}

		return false;
	}

	public static void choosed() {

		chooseResp = true;
	}

	public static void reset() {

		chooseNum = -1;
		chooseRandom = 2;
		chooseResp = false;
	}

	public static boolean isChooseResp() {
		return chooseResp;
	}

}
