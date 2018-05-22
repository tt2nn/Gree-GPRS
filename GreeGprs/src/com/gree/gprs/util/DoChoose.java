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
	private static int chooseRandom = 4;

	/**
	 * 选举
	 * 
	 * @return
	 */
	public static boolean choose() {

		chooseResp = false;

		if (chooseNum == -1) { // 当选举计数为-1的时候，收到选举则重新取随机数

			chooseNum = Utils.getRandom(chooseRandom);

			if (chooseRandom > 16) {

				chooseRandom = 16;

			} else {

				chooseRandom += 3;
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

	public static void resetRandomNum() {

		chooseRandom = 2;
	}

	public static void reset() {

		chooseNum = -1;
		resetRandomNum();
		chooseResp = false;
	}

	public static boolean isChooseResp() {
		return chooseResp;
	}

}
