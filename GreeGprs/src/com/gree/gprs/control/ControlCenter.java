package com.gree.gprs.control;

import com.gree.gprs.Boot;
import com.gree.gprs.constant.Constant;
import com.gree.gprs.data.DataCenter;
import com.gree.gprs.file.FileModel;
import com.gree.gprs.file.FileWriteModel;
import com.gree.gprs.gpio.GpioPin;
import com.gree.gprs.tcp.TcpServer;
import com.gree.gprs.tcp.model.LoginModel;
import com.gree.gprs.tcp.model.ParamModel;
import com.gree.gprs.tcp.model.TimeModel;
import com.gree.gprs.tcp.model.TransmitModel;
import com.gree.gprs.util.DoChoose;
import com.gree.gprs.variable.Variable;

/**
 * 工作中心
 * 
 * @author lihaotian
 *
 */
public class ControlCenter {

	private static ControlTimer controlTimer = new ControlTimer();
	private static Thread controlTimerThread;

	private static boolean waittingHeart = false;

	private static int transmitMarkError = 0;
	private static int transmitMarkOpen = 0;
	private static int transmitMarkClose = 0;
	private static int transmitMarkChange = 0;
	private static int transmitMarkWarning = 0;

	public static boolean Push_Key_Down = false;
	public static long Push_Key_Time = 0L;
	public static long Gprs_Valid_Time = 946656000000L;

	/**
	 * start control timer
	 */
	public static void startControlTimer() {

		controlTimerThread = new Thread(controlTimer);
		controlTimerThread.start();
	}

	/**
	 * set interface
	 * 
	 * @param controlInterface
	 */
	public static void setControlInterface(ControlInterface controlInterface) {

		controlTimer.setControlInterface(controlInterface);
	}

	/**
	 * 判断App是否可以工作
	 * 
	 * @return
	 */
	public static boolean canWorking() {

		if (Variable.Gprs_Init_Success && Variable.Gprs_Choosed && Variable.System_Time > Gprs_Valid_Time) {

			return true;
		}

		return false;
	}

	/**
	 * 登录
	 */
	public static void login() {

		LoginModel.login();
	}

	/**
	 * 心跳
	 */
	public static void heartBeat() {

		Variable.Heart_Beat_Time += 10 * 1000;

		if (!TcpServer.isServerWorking()) {

			TcpServer.startServer();
			return;
		}

		Variable.Gprs_Login = true;
		TimeModel.heart();
	}

	/**
	 * 心跳响应处理
	 */
	public static void heartBeatResp() {

		if (waittingHeart) {

			requestStartUpload();

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			stopTcpServer();
		}
	}

	/**
	 * 请求开始传输
	 */
	public static void requestStartUpload() {

		if (!TcpServer.isServerWorking()) {

			waittingHeart = true;
			TcpServer.startServer();

			return;
		}

		if (!Variable.Gprs_Login) {

			waittingHeart = true;
			return;
		}

		waittingHeart = false;
		TransmitModel.start();
	}

	/**
	 * 恢复数据上传
	 */
	public static void recoverUpload() {

		Variable.Gprs_Error_Type = Constant.GPRS_ERROR_TYPE_NO;
		waittingHeart = true;
		login();
	}

	/**
	 * 发送GPRS模块信息
	 */
	public static void sendGprsSignal() {

		ParamModel.gprsSignal();
	}

	/**
	 * 传输数据
	 * 
	 * @param length
	 * @param time
	 */
	public static void transmitData(int length, long time) {

		TransmitModel.dataTransm(length, time);
	}

	/**
	 * 停止TCP
	 */
	public static void stopTcpServer() {

		TcpServer.stopServer();
		Variable.Gprs_Login = false;
	}

	/* =========== 数据中心控制相关 ============== */

	/**
	 * 重新选举
	 */
	public static void chooseRest() {

		DoChoose.reset();
		Variable.Gprs_Choosed = false;
		FileWriteModel.saveGprsChooseState(false);
		GpioPin.closeTransmit();
		GpioPin.closeError();
		DataCenter.destoryTransmit();
		Variable.Gprs_Error_Type = Constant.GPRS_ERROR_TYPE_NO;
		Variable.Transmit_Cache_Type = Constant.TRANSMIT_TYPE_CHECK;
	}

	/**
	 * 选中GPRS
	 */
	public static void chooseGprs() {

		Variable.Gprs_Choosed = true;
		FileWriteModel.saveGprsChooseState(true);
		GpioPin.openTransmit();
		DataCenter.Transmit_Choose_Or_Power = false;
		controlTimer.chooseTransmit = false;
	}

	/**
	 * 按下
	 */
	public static void pushKey(boolean down) {

		if (down) {

			Push_Key_Time = Variable.System_Time;
		}

		Push_Key_Down = down;
	}

	/**
	 * 设置标志位
	 * 
	 * @param debug
	 * @param error
	 * @param warning
	 * @param change
	 * @param open
	 * @param close
	 */
	public static void setMarker(int debug, int error, int warning, int change, int open, int close) {

		if (!Variable.Gprs_Init_Success) {

			return;
		}

		if (!DataCenter.Transmit_Choose_Or_Power) {

			return;
		}

		if (Variable.Transmit_Type != Constant.TRANSMIT_TYPE_DEBUG && debug == 1) {

			DataCenter.debugTransmit();

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_DEBUG && debug == 0) {

			DataCenter.stopTransmit(false);

			if (transmitMarkError == 0 && error == 1) {

				DataCenter.errorTransmit();

			} else if (transmitMarkOpen == 0 && open == 1) {

				DataCenter.openTransmit();

			} else if (transmitMarkClose == 0 && close == 1) {

				DataCenter.closeTransmit();

			} else if (transmitMarkChange == 0 && change == 1) {

				DataCenter.changeTransmit();

			} else if (transmitMarkWarning == 0 && warning == 1) {

				DataCenter.warningTransmit();

			} else {

				DataCenter.stopTransmit(true);
			}

		} else if (transmitMarkError == 0 && error == 1) {

			// 故障标志位由0-1，启动故障上报
			DataCenter.errorTransmit();

		} else if (transmitMarkOpen == 0 && open == 1) {

			DataCenter.openTransmit();

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN && DataCenter.arriveEndMark() && open == 0) {

			DataCenter.stopTransmit(false);

			if (transmitMarkClose == 0 && close == 1) {

				DataCenter.closeTransmit();

			} else if (transmitMarkChange == 0 && change == 1) {

				DataCenter.changeTransmit();

			} else if (transmitMarkWarning == 0 && warning == 1) {

				DataCenter.warningTransmit();

			} else {

				DataCenter.stopTransmit(true);
			}

		} else if (transmitMarkClose == 0 && close == 1) {

			DataCenter.closeTransmit();

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE && DataCenter.arriveEndMark() && close == 0) {

			DataCenter.stopTransmit(false);

			if (transmitMarkOpen == 0 && open == 1) {

				DataCenter.openTransmit();

			} else if (transmitMarkChange == 0 && change == 1) {

				DataCenter.changeTransmit();

			} else if (transmitMarkWarning == 0 && warning == 1) {

				DataCenter.warningTransmit();

			} else {

				DataCenter.stopTransmit(true);
			}

		} else if (transmitMarkChange == 0 && change == 1) {

			// 厂家参数变化标志位由0-1，启动参数变化上报
			DataCenter.changeTransmit();

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_CHANGE && DataCenter.arriveEndMark()
				&& change == 0) {

			DataCenter.stopTransmit(false);

			if (transmitMarkWarning == 0 && warning == 1) {

				DataCenter.warningTransmit();

			} else {

				DataCenter.stopTransmit(true);
			}

		} else if (transmitMarkWarning == 0 && warning == 1) {

			// 亚健康标志位由0-1，启动亚健康上报
			DataCenter.warningTransmit();

		} else if (Variable.Transmit_Type == Constant.TRANSMIT_TYPE_WARNING && warning == 0) {

			// 亚健康标志位由1-0，停止亚健康上报
			DataCenter.Transmit_Cache_Warning = false;
			DataCenter.stopTransmit(true);

		} else if (DataCenter.Transmit_Cache_Warning && warning == 1) {

			// 缓存上报模式为亚健康上报，标志位为1，继续亚健康上报
			DataCenter.warningTransmit();
		}

		transmitMarkError = error;
		transmitMarkWarning = warning;
		transmitMarkChange = change;
		transmitMarkOpen = open;
		transmitMarkClose = close;
	}

	/**
	 * 重置系统
	 */
	public static void resetSystem() {

		Boot.Gprs_Running = false;
		GpioPin.openAllLight();
		FileModel.deleteAllFile();
		System.getProperty("system.hint.powerreset");
	}

	public static int getTransmitMarkOpen() {
		return transmitMarkOpen;
	}

	public static int getTransmitMarkClose() {
		return transmitMarkClose;
	}

	public static int getTransmitMarkChange() {
		return transmitMarkChange;
	}

	public interface ControlInterface {

		public void controlPriod();
	}

}
