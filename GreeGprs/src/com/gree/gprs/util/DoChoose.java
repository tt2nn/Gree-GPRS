package com.gree.gprs.util;

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
	private static int chooseRandom = 12;
	private static boolean firstResp = false;
	private static int fastNum = 0;

	/**
	 * 选举
	 * 
	 * @return
	 */
	public static boolean choose(boolean needFast) {

		chooseResp = false;

		if (chooseNum == -1) { // 当选举计数为-1的时候，收到选举则重新取随机数

			chooseNum = Utils.getRandom(chooseRandom);
		}

		if (!firstResp || chooseNum == 0) { // 当计数为0的时候，可以执行选举

			firstResp = true;
			chooseRandom = 12;
			chooseNum = -1;
			choosed();

			return true;
		}

		if (fastNum < 2 && needFast && chooseNum > 2) {

			chooseRandom = chooseRandom / 2;
			chooseNum = -1;
			fastNum++;

			return choose(false);
		}

		chooseNum--;

		return false;
	}

	public static void choosed() {

		chooseResp = true;
	}

	public static void reset() {

		fastNum = 0;
		chooseRandom = 12;
		chooseNum = -1;
		chooseResp = false;
	}

	public static boolean isChooseResp() {
		return chooseResp;
	}

}
